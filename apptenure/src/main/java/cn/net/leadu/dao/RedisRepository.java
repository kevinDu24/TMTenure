package cn.net.leadu.dao;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by PengChao on 16/9/14.
 */

@Repository
public class RedisRepository {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Resource(name="stringRedisTemplate")
    ValueOperations<String,String> valOpsStr;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @Resource(name="redisTemplate")
    ValueOperations<Object, Object> valOps;

    public void save(Object key,Object value,int time){

        valOps.set(key,value,time, TimeUnit.SECONDS);
    }

    public void save(Object key, Object value){
        valOps.set(key, value);
    }

    public Object get(Object key){

        return valOps.get(key);
    }

    public void delete(Object key){
        redisTemplate.delete(key);
    }

    public List<String> getAllKeys(String pattern){
        List<String> keyList = Lists.newArrayList();
        Set<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().keys((pattern+"*").getBytes());
        Iterator<byte[]> it = keys.iterator();
        while(it.hasNext()){
            byte[] data = (byte[])it.next();
            keyList.add(new String(data, 0, data.length));
        }
        return keyList;
    }

    public void deleteByPattern(String pattern){
        List<String> keyList = Lists.newArrayList();
        Set<byte[]> keys = redisTemplate.getConnectionFactory().getConnection().keys((pattern+"*").getBytes());
        Iterator<byte[]> it = keys.iterator();
        while(it.hasNext()){
            byte[] data = (byte[])it.next();
            keyList.add(new String(data, 0, data.length));
        }
        keyList.forEach(key -> {
            redisTemplate.delete(key);
        });

    }
}
