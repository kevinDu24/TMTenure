package cn.net.leadu.util.esign;

import com.timevale.esign.sdk.tech.bean.PersonBean;
import com.timevale.esign.sdk.tech.bean.PosBean;
import com.timevale.esign.sdk.tech.bean.SignPDFStreamBean;
import com.timevale.esign.sdk.tech.bean.UpdateOrganizeBean;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.result.Result;
import com.timevale.esign.sdk.tech.bean.seal.OrganizeTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
import com.timevale.esign.sdk.tech.impl.constants.SignType;
import com.timevale.esign.sdk.tech.service.*;
import com.timevale.esign.sdk.tech.service.factory.*;
import com.timevale.tech.sdk.bean.HttpConnectionConfig;
import com.timevale.tech.sdk.bean.ProjectConfig;
import com.timevale.tech.sdk.bean.SignatureConfig;
import com.timevale.tech.sdk.constants.AlgorithmType;
import com.timevale.tech.sdk.constants.HttpType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/***
 * e签宝快捷签SDK辅助类
 * 
 * @author Ching
 *
 */
public class SignHelper {
	private static Logger LOG = LoggerFactory.getLogger(SignHelper.class);

	/***
	 * 项目初始化 使用到的接口：sdk.init(proCfg, httpConCfg, sCfg);
	 */
	public static void initProject() {

		ProjectConfig proCfg = new ProjectConfig();

		// 项目ID(应用ID)
		proCfg.setProjectId("1111564463");// 1111563517
		// 项目Secret(应用Secret)
		proCfg.setProjectSecret("eb61b9c5a45a5b3cde5f5eecb6dd0c27"); // 95439b0863c241c63a861b87d1e647b7
		// 开放平台地址
		proCfg.setItsmApiUrl("http://itsm.tsign.cn/tgmonitor/rest/app!getAPIInfo2"); // http://121.40.164.61:8080/tgmonitor/rest/app!getAPIInfo2

		HttpConnectionConfig httpConCfg = new HttpConnectionConfig();
		// 协议类型，默认https
		httpConCfg.setHttpType(HttpType.HTTPS);
		// 请求失败重试次数，默认5次
		httpConCfg.setRetry(5);
		// 代理服务IP地址
		// httpConCfg.setProxyIp(null);
		// 代理服务端口
		// httpConCfg.setProxyPort(0);

		SignatureConfig sCfg = new SignatureConfig();
		// 算法类型，默认HMAC-SHA256
		sCfg.setAlgorithm(AlgorithmType.HMACSHA256); // 可选RSA，但推荐使用HMACSHA256
		// e签宝公钥，可以从开放平台获取。若算法类型为RSA，此项必填
		sCfg.setEsignPublicKey("");
		// 平台私钥，可以从开放平台下载密钥生成工具生成。若算法类型为RSA，此项必填
		sCfg.setPrivateKey("");
		System.out.println("--项目初始化...");
		EsignsdkService sdk = EsignsdkServiceFactory.instance();
		Result result = sdk.init(proCfg, httpConCfg, sCfg);
		if (0 != result.getErrCode()) {
			LOG.info("--项目初始化失败：errCode=" + result.getErrCode() + " msg=" + result.getMsg() );
		} else {
			System.out.println("--项目初始化成功！errCode=" + result.getErrCode() + " msg=" + result.getMsg());
		}
	}

	/***
	 * 坐标定位签署的PosBean
	 */
	public static PosBean setXYPosBean(String page,int x,int y) {
		PosBean posBean = new PosBean();
		// 定位类型，0-坐标定位，1-关键字定位，默认0，若选择关键字定位，签署类型(signType)必须指定为关键字签署才会生效。
		posBean.setPosType(0);
		// 签署页码，若为多页签章，支持页码格式“1-3,5,8“，若为坐标定位时，不可空
		posBean.setPosPage(page);
		// 签署位置X坐标，默认值为0，以pdf页面的左下角作为原点，控制横向移动距离，单位为px
		posBean.setPosX(x);
		// 签署位置Y坐标，默认值为0，以pdf页面的左下角作为原点，控制纵向移动距离，单位为px
		posBean.setPosY(y);
		// 印章展现宽度，将以此宽度对印章图片做同比缩放。详细查阅接口文档的15 PosBean描述
		posBean.setWidth(159);
		return posBean;
	}

	/***
	 * 关键字定位签署的PosBean
	 */
	public static PosBean setKeyPosBeanConfirm(String key) {
		PosBean posBean = new PosBean();
		// 定位类型，0-坐标定位，1-关键字定位，默认0，若选择关键字定位，签署类型(signType)必须指定为关键字签署才会生效。
		posBean.setPosType(1);
		// 关键字签署时不可空 */
		posBean.setKey(key);
		// 关键字签署时会对整体pdf文档进行搜索，故设置签署页码无效
		// posBean.setPosPage("1");
		// 签署位置X坐标，以关键字所在位置为原点进行偏移，默认值为0，控制横向移动距离，单位为px
		posBean.setPosX(90);
		// 签署位置Y坐标，以关键字所在位置为原点进行偏移，默认值为0，控制纵向移动距离，单位为px
		posBean.setPosY(0);
		// 印章展现宽度，将以此宽度对印章图片做同比缩放。详细查阅接口文档的15 PosBean描述
		posBean.setWidth(100);
		return posBean;
	}

	/***
	 * 关键字定位签署的PosBean
	 */
	public static PosBean setKeyPosBean(String key) {
		PosBean posBean = new PosBean();
		// 定位类型，0-坐标定位，1-关键字定位，默认0，若选择关键字定位，签署类型(signType)必须指定为关键字签署才会生效。
		posBean.setPosType(1);
		// 关键字签署时不可空 */
		posBean.setKey(key);
		// 关键字签署时会对整体pdf文档进行搜索，故设置签署页码无效
		// posBean.setPosPage("1");
		// 签署位置X坐标，以关键字所在位置为原点进行偏移，默认值为0，控制横向移动距离，单位为px
		posBean.setPosX(60);
		// 签署位置Y坐标，以关键字所在位置为原点进行偏移，默认值为0，控制纵向移动距离，单位为px
		posBean.setPosY(-25);
		// 印章展现宽度，将以此宽度对印章图片做同比缩放。详细查阅接口文档的15 PosBean描述
		posBean.setWidth(100);
		return posBean;
	}

	/***
	 * 文件流签署的PDF文档信息
	 */
	public static SignPDFStreamBean setSignPDFStreamBean(byte[] pdfFileStream) {
		SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
		// 待签署文档本地二进制数据
		signPDFStreamBean.setStream(pdfFileStream);
		// 文档名称，e签宝签署日志对应的文档名，若为空则取文档路径中的名称
		// signPDFStreamBean.setFileName("pdf文件名");
		// 文档编辑密码，当目标PDF设置权限密码保护时必填 */
		// signPDFStreamBean.setOwnerPassword(null);
		return signPDFStreamBean;
	}

	/***
	 * 平台自身PDF摘要签署（文件二进制流,确认函）； 盖章位置通过坐标定位； 使用到接口：SelfSignServiceFactory.instance();
	 * selfSignService.localSignPdf(signPDFStreamBean, posBean, sealId,
	 * SignType.Single);
	 */
	public static FileDigestSignResult platformSignByStreammConfirm(String srcPdfFile) {
		// 设置文件流签署的PDF文档信息
		SignPDFStreamBean signPDFStreamBean = setSignPDFStreamBean(FileHelper.getBytes(srcPdfFile));
		// 设置坐标定位签署的PosBean，坐标定位方式支持单页签章、多页签章和骑缝章，但对关键字签章指定页码无效；
		PosBean posBean = setKeyPosBean("出租人签字/盖章");
		// 设置签署类型为 关键字签章
		SignType signType = SignType.Key;
		// 设置签署印章，www.tsign.cn官网设置的默认签名sealId = 0
		int sealId = 0;

		System.out.println("----开始平台自身PDF摘要签署...");
		SelfSignService selfSignService = SelfSignServiceFactory.instance();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(signPDFStreamBean, posBean, sealId,
				signType);
		if (0 != fileDigestSignResult.getErrCode()) {
			LOG.info("平台自身PDF摘要签署（文件流）失败，errCode=" + fileDigestSignResult.getErrCode() + " msg="
					+ fileDigestSignResult.getMsg());
		} else {
			System.out.println("----平台自身PDF摘要签署成功！SignServiceId = " + fileDigestSignResult.getSignServiceId());
		}
		return fileDigestSignResult;

	}

	/***
	 * 平台自身PDF摘要签署（文件二进制流）； 盖章位置通过坐标定位； 使用到接口：SelfSignServiceFactory.instance();
	 * selfSignService.localSignPdf(signPDFStreamBean, posBean, sealId,
	 * SignType.Single);
	 */
	public static FileDigestSignResult platformSignByStreamm(String srcPdfFile) {
		// 设置文件流签署的PDF文档信息
		SignPDFStreamBean signPDFStreamBean = setSignPDFStreamBean(FileHelper.getBytes(srcPdfFile));
		// 设置坐标定位签署的PosBean，坐标定位方式支持单页签章、多页签章和骑缝章，但对关键字签章指定页码无效；
		PosBean posBean = setXYPosBean("1-2",170,714);
		// 设置签署类型为 单页签章，坐标定位方式支持单页签章、多页签章和骑缝章
		SignType signType = SignType.Edges;
		// 设置签署印章，www.tsign.cn官网设置的默认签名sealId = 0
		int sealId = 0;

		System.out.println("----开始平台自身PDF摘要签署(骑缝章)...");
		SelfSignService selfSignService = SelfSignServiceFactory.instance();
		FileDigestSignResult fileDigestSignResult = selfSignService.localSignPdf(signPDFStreamBean, posBean, sealId,
				signType);
		if (0 != fileDigestSignResult.getErrCode()) {
			LOG.info("平台自身PDF摘要签署（(骑缝章)文件流）失败，errCode=" + fileDigestSignResult.getErrCode() + " msg="
					+ fileDigestSignResult.getMsg());
		} else {
			System.out.println("----平台自身PDF摘要签署成功(骑缝章)！SignServiceId = " + fileDigestSignResult.getSignServiceId());
		}

		System.out.println("----开始平台自身PDF摘要签署(关键字)...");
		// 设置坐标定位签署的PosBean，坐标定位方式支持单页签章、多页签章和骑缝章，但对关键字签章指定页码无效；
		PosBean posBean1 = setKeyPosBean("出租人签字/盖章");
		// 设置签署类型为 关键字签章
		SignType signType1 = SignType.Key;
		SignPDFStreamBean signPDFStreamBean1 = setSignPDFStreamBean(fileDigestSignResult.getStream());
		FileDigestSignResult fileDigestSignResult1 = selfSignService.localSignPdf(signPDFStreamBean1, posBean1, sealId,
				signType1);
		if (0 != fileDigestSignResult1.getErrCode()) {
			LOG.info("平台自身PDF摘要签署（(关键字)文件流）失败，errCode=" + fileDigestSignResult.getErrCode() + " msg="
					+ fileDigestSignResult1.getMsg());
		} else {
			System.out.println("----平台自身PDF摘要签署成功(关键字)！SignServiceId = " + fileDigestSignResult.getSignServiceId());
		}
		return fileDigestSignResult1;

	}

	/***
	 * 平台下个人用户PDF摘要签署（文件二进制流）；盖章位置通过关键字定位； 使用到接口：UserSignServiceFactory.instance();
	 * userSignService.localSignPDF(accountId,addSealResult.getSealData(),
	 * signPDFStreamBean, posBean, SignType.Single);
	 */
	public static FileDigestSignResult userPersonSignByStreamConfirm(byte[] pdfFileStream, String accountId,
															  String sealData, String mobile, String code) {

		// 设置文件流签署的PDF文档信息
		SignPDFStreamBean signPDFStreamBean = setSignPDFStreamBean(pdfFileStream);
		// 设置坐标定位签署的PosBean，坐标定位方式支持单页签章、多页签章和骑缝章，但对关键字签章指定页码无效；
		PosBean posBean = setKeyPosBeanConfirm("承租人签字");
		// 设置签署类型为 关键字签章
		SignType signType = SignType.Key;

		System.out.println("----开始平台个人客户的PDF摘要签署...");
		UserSignService userSignService = UserSignServiceFactory.instance();
		FileDigestSignResult fileDigestSignResult = userSignService.localSafeSignPDF3rd(accountId, sealData, signPDFStreamBean,
				posBean, signType, mobile, code);
		if (0 != fileDigestSignResult.getErrCode()) {
			LOG.info("平台个人客户的PDF摘要签署失败，errCode=" + fileDigestSignResult.getErrCode() + " msg="
					+ fileDigestSignResult.getMsg());
		} else {
			System.out.println("平台个人客户的PDF摘要签署成功！SignServiceId = " + fileDigestSignResult.getSignServiceId());
		}
		return fileDigestSignResult;
	}
	

	/***
	 * 平台下个人用户PDF摘要签署（文件二进制流）；盖章位置通过关键字定位； 使用到接口：UserSignServiceFactory.instance();
	 * userSignService.localSignPDF(accountId,addSealResult.getSealData(),
	 * signPDFStreamBean, posBean, SignType.Single);
	 */
	public static FileDigestSignResult userPersonSignByStream(byte[] pdfFileStream, String accountId,
			String sealData, String mobile, String code, String type) {

		// 设置文件流签署的PDF文档信息
		SignPDFStreamBean signPDFStreamBean = setSignPDFStreamBean(pdfFileStream);
		// 设置坐标定位签署的PosBean，坐标定位方式支持单页签章、多页签章和骑缝章，但对关键字签章指定页码无效；
		PosBean posBean = new PosBean();
		if("1".equals(type)){
			posBean = setKeyPosBean("承租人签字/盖章");
		} else if("2".equals(type)){
			posBean = setKeyPosBean("还款银行卡户主签字");
		}
		// 设置签署类型为 关键字签章
		SignType signType = SignType.Key;

		System.out.println("----开始平台个人客户的PDF摘要签署...");
		UserSignService userSignService = UserSignServiceFactory.instance();
		FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
		if("1".equals(type)){
			fileDigestSignResult = userSignService.localSafeSignPDF3rd(accountId, sealData, signPDFStreamBean,
					posBean, signType, mobile, code);
		} else if("2".equals(type)){
			fileDigestSignResult = userSignService.localSignPDF(accountId, sealData, signPDFStreamBean,
					posBean, signType);
		}
		if (0 != fileDigestSignResult.getErrCode()) {
			LOG.info("平台个人客户的PDF摘要签署失败，errCode=" + fileDigestSignResult.getErrCode() + " msg="
					+ fileDigestSignResult.getMsg());
		} else {
			System.out.println("平台个人客户的PDF摘要签署成功！SignServiceId = " + fileDigestSignResult.getSignServiceId());
		}
		return fileDigestSignResult;
	}


	/***
	 * 创建个人账户 使用到接口：accountService.addAccount(organizeBean);
	 */
	public static String addPersonAccount(String idCard, String name) {
		PersonBean personBean = new PersonBean();
		// 姓名
		personBean.setName(name);
		// 身份证号/护照号
		personBean.setIdNo(idCard);

		System.out.println("----开始创建个人账户...");
		AccountService accountService = AccountServiceFactory.instance();
		AddAccountResult addAccountResult = accountService.addAccount(personBean);
		if (0 != addAccountResult.getErrCode()) {
			LOG.info("创建个人账户失败，errCode=" + addAccountResult.getErrCode() + " msg=" + addAccountResult.getMsg());
		} else {
			System.out.println("创建个人账户成功！accountId = " + addAccountResult.getAccountId());
		}
		return addAccountResult.getAccountId();

	}


	/***
	 * 创建个人账户的印章； 使用到接口：sealService.addTemplateSeal(accountId,
	 * PersonTemplateType.SQUARE, SealColor.RED);
	 */
	public static AddSealResult addPersonTemplateSeal(String accountId) {
		// 印章模板类型：矩形印章
		PersonTemplateType personTemplateType = PersonTemplateType.BORDERLESS;
		// 印章颜色：红色
		SealColor sealColor = SealColor.BLACK;

		System.out.println("----开始创建个人账户的印章...");
		SealService sealService = SealServiceFactory.instance();
		AddSealResult addSealResult = sealService.addTemplateSeal(accountId, personTemplateType, sealColor);
		if (0 != addSealResult.getErrCode()) {
			LOG.info("创建个人模板印章失败，errCode=" + addSealResult.getErrCode() + " msg=" + addSealResult.getMsg());
		} else {
			System.out.println("创建个人模板印章成功！SealData = " + addSealResult.getSealData());
		}
		return addSealResult;

	}

	/***
	 * 创建企业账户的印章,该企业账户印章是一个相对概念。可以理解成是你们公司的客户企业印章；
	 * 使用到接口：sealService.addTemplateSeal(accountId, OrganizeTemplateType.STAR,
	 * SealColor.RED, "合同专用章", "下弦文");
	 */
	public static AddSealResult addOrganizeTemplateSeal(String accountId) {
		/*
		 * hText 生成印章中的横向文内容 如“合同专用章、财务专用章” qText 生成印章中的下弦文内容 公章防伪码（一串13位数字）
		 * 如91010086135601
		 */

		// 印章模板类型：标准公章
		OrganizeTemplateType organizeTemplateType = OrganizeTemplateType.STAR;
		// 印章颜色：红色
		SealColor sealColor = SealColor.RED;
		// 横向文字
		String hText = "合同专用章";
		// 下弦文字
		String qText = "91010086135601";
		System.out.println("----开始创建企业账户的印章...");
		SealService sealService = SealServiceFactory.instance();
		AddSealResult addSealResult = sealService.addTemplateSeal(accountId, organizeTemplateType, sealColor, hText,
				qText);
		if (0 != addSealResult.getErrCode()) {
			LOG.info("创建企业模板印章失败，errCode=" + addSealResult.getErrCode() + " msg=" + addSealResult.getMsg());
		} else {
			System.out.println("创建企业模板印章成功！SealData = " + addSealResult.getSealData());
		}
		return addSealResult;

	}
	
	/***
	 * 上传印章图片制作SealData；
	 * 使用到接口：Apache Commons Codec的org.apache.commons.codec.binary.Base64
	 * 该方法属于 第三方jar实现，并非快捷签SDK提供；
	 */
	public static String getSealDataByImage(String imgFilePath) {

		System.out.println("----开始将上传的印章图片转成SealData数据...");
		/* commons-codec-1.10.jar 第三方技术实现 */
		String sealData = FileHelper.GetImageStr(imgFilePath);
		System.out.println("----上传的印章图片转成SealData数据完成！sealData:" + sealData);
		return sealData;
	}
	
	/***
	 * 保存签署后的文件流
	 */
	public static boolean saveSignedByStream(byte[] signedStream,String signedFolder,String signedFileName) {
		System.out.println("----开始保存签署后文件...");
		boolean isSuccess = false;
		Map<String,String> fileResult = FileHelper.saveFileByStream(signedStream, signedFolder,signedFileName);
		if (0 != Integer.parseInt(fileResult.get("errCode"))) {
			LOG.info("保存签署后文件失败，errCode=" + fileResult.get("errCode") + " msg=" + fileResult.get("msg"));
		} else {
			isSuccess = true;
			System.out.println("保存签署后文件成功！errCode=" + fileResult.get("errCode") + " msg=" + fileResult.get("msg"));
		}
		return isSuccess;

	}

	/***
	 * 向指定手机发送短信
	 * @param accountId
	 */
	public static boolean sendMessage(String accountId,String mobile){

		UpdateOrganizeBean updateOrganizeBean = new UpdateOrganizeBean();
		updateOrganizeBean.setMobile(mobile);

		MobileService mobileService = MobileServiceFactory.instance();
		Result result = mobileService.sendSignMobileCode3rd(accountId, mobile);
		if (0 != result.getErrCode()) {
			LOG.info("发送短信失败，errCode=" + result.getErrCode() + " msg=" + result.getMsg());
			return false;
		} else {
			System.out.println("发送短信成功！");
			return true;
		}
	}

}
