package cn.net.leadu.service;

import cn.net.leadu.config.MessageProperties;
import cn.net.leadu.dao.RedisRepository;
import cn.net.leadu.dao.UserWebRepository;
import cn.net.leadu.domain.UserWeb;
import cn.net.leadu.dto.PageDto;
import cn.net.leadu.dto.SendShortMessageDto;
import cn.net.leadu.dto.WebSearchDto;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.util.CommonUtils;
import cn.net.leadu.util.MessageUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Web端系统service
 * Created by zcHu on 2017/8/28.
 */
@Service
public class SystemWebService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private UserWebRepository userWebRepository;

    @Autowired
    private MessageUtil messageUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageProperties messageProperties;

    @Autowired
    private SystemService systemService;


    /**
     * 获取短信验证码（web端）
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getRadomCodeWeb(String phoneNum) throws Exception{
        List<UserWeb> userList =  userWebRepository.findAll();
        if(userList == null || userList.isEmpty()){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "此手机号不在可登录用户名单内"), HttpStatus.OK);
        }
        boolean flag = false;
        for(UserWeb user : userList){
            if(phoneNum.equals(user.getPhoneNum())){
                flag = true;
                break;
            }
        }
        if(!flag){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "此手机号码无登录权限"), HttpStatus.OK);
        }
        String code = "" + (int)(Math.random()*900000+100000);
        String timeStamp = String.valueOf(System.currentTimeMillis());
        redisRepository.save(timeStamp, code, 300);
        //调用外部的发送短信接口
        String send = "";
        try {
            SendShortMessageDto sendShortMessageDto = new SendShortMessageDto();
            sendShortMessageDto.setPhoneNum(phoneNum);
            sendShortMessageDto.setText(messageProperties.getPszMsg().replace("xxxxxx",code));
            ResponseEntity<Message> responseEntity = systemService.sendShortMessage(sendShortMessageDto);
            send = responseEntity.getBody().getStatus();
            //主系统发送失败，再由梦网科技发送
            if("ERROR".equals(send)){
                send = messageUtil.senRadomCode(phoneNum, code);
            }
            systemService.saveMessageLog(phoneNum, messageProperties.getPszMsg().replace("xxxxxx",code), send);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        if(send != null && !"".equals(send) && ("SUCCESS".equals(send) || send.length()>10)){
            //解析后的返回值不为空且长度大于10，则是提交成功
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)timeStamp), HttpStatus.OK);
        }else{//解析后的返回值不为空且长度不大于10，则提交失败，返回错误码
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 登录接口（web端）
     * @param timeStamp
     * @param code
     * @return
     */
    public ResponseEntity<Message> loginWeb( String timeStamp, String code) {
        if (redisRepository.get(timeStamp) == null) {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "验证码已过期,请重新获取验证码"), HttpStatus.OK);
        }
        if (!redisRepository.get(timeStamp).equals(code)) {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "验证码错误"), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 获取二次营销申请状况（web端）
     * @param webSearchDto
     * @return
     */
    public ResponseEntity<Message> search(WebSearchDto webSearchDto, int page, int size) {
        HibernateEntityManager hEntityManager = (HibernateEntityManager)entityManager;
        Session session = hEntityManager.getSession();
        Query query = session.createSQLQuery(getQuerySql(webSearchDto , page ,  size,1, webSearchDto.getSearchType())).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        setParams(webSearchDto,query);
        List<Map<String,Object>>  list = query.list();
        PageDto pageDto = new PageDto();
        pageDto.setPage(page);
        pageDto.setSize(size);
        pageDto.setContent(list);
        query = session.createSQLQuery(getQuerySql(webSearchDto , page ,  size,0,webSearchDto.getSearchType()));
        setParams(webSearchDto,query);
        Object  countList = query.uniqueResult();
        pageDto.setTotalElements(Long.parseLong(countList.toString()));
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, pageDto), HttpStatus.OK);
    }

    /**
     * 构建sql
     * @param webSearchDto
     * @param page
     * @param size
     * @return
     */
    public String getQuerySql(WebSearchDto webSearchDto, int page , int size,Integer flag, String searchType){
        StringBuffer querySql = new StringBuffer();
        StringBuffer whereSql = new StringBuffer();
        querySql.append(" select  ");
        if(flag == 1 || flag == 2) {
            querySql.append(" t.name as name, t.phone_num as phoneNum , t.id_card as idCard ," +
                    " t.apply_num as applyNum, t.money as money, t.month as month, t.id_card_url as idCardUrl," +
                    " t.face_image_url as faceImageUrl, t.confirmation_signed_pdf as confirmationSignedPdf, " +
                    " t.contact_signed_pdf as contactSignedPdf, t.create_time as createTime ,t.update_time as updateTime");
        }else{
            querySql.append(" count(t.phone_num) ");
        }
        querySql.append(" from personal_info t ");
        whereSql.append(" where 1=1 ");
        if("0".equals(searchType)){
            whereSql.append(" and t.state = '0' ");
        } else if("1".equals(searchType)){
            whereSql.append(" and t.state = '1' ");
        }

        if(!CommonUtils.isNull(webSearchDto.getName())){
            whereSql.append(" and t.name like :name ");
        }

        if(!CommonUtils.isNull(webSearchDto.getApplyNum())){
            whereSql.append(" and t.apply_num like :applyNum ");
        }

        if(!CommonUtils.isNull(webSearchDto.getPhoneNum())){
            whereSql.append(" and t.phone_num like :phoneNum ");
        }

        if(flag !=0){
            if("0".equals(searchType)){
                whereSql.append(" ORDER BY t.create_time DESC ");
            } else if("1".equals(searchType)){
                whereSql.append(" ORDER BY t.update_time DESC ");
            }
        }

        if(flag == 1)
            whereSql.append(" limit "+ size +" offset "+(page -1) * size);
        String sql = querySql.toString() + whereSql.toString();
        return sql;
    }

    public void setParams(WebSearchDto webSearchDto,Query query){

        if(!CommonUtils.isNull(webSearchDto.getName())){
            query.setParameter("name",CommonUtils.likePartten(webSearchDto.getName()));
        }
        if(!CommonUtils.isNull(webSearchDto.getApplyNum())){
            query.setParameter("applyNum",CommonUtils.likePartten(webSearchDto.getApplyNum()));
        }

        if(!CommonUtils.isNull(webSearchDto.getPhoneNum())){
            query.setParameter("phoneNum",CommonUtils.likePartten(webSearchDto.getPhoneNum()));
        }

    }
}
