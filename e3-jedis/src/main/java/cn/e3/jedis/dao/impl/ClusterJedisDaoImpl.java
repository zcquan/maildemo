package cn.e3.jedis.dao.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.JedisCluster;
import cn.e3.jedis.dao.JedisDao;

@Repository
public class ClusterJedisDaoImpl implements JedisDao {

	// 注入集群对象
	@Autowired
	private JedisCluster jc;

	@Override
	public String set(String key, String value) {
		String set = jc.set(key, value);
		return set;
	}

	@Override
	public String get(String key) {
		String value = jc.get(key);
		return value;
	}

	@Override
	public Long incr(String key) {
		// TODO Auto-generated method stub
		Long incr = jc.incr(key);
		return incr;
	}

	@Override
	public Long decr(String key) {
		// TODO Auto-generated method stub
		Long decr = jc.decr(key);
		return decr;
	}

	@Override
	public Long hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		Long hset = jc.hset(key, field, value);
		return hset;
	}

	@Override
	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		String hget = jc.hget(key, field);
		return hget;
	}

	@Override
	public Long hdel(String key, String field) {
		// TODO Auto-generated method stub
		Long hdel = jc.hdel(key, field);
		return hdel;
	}

	@Override
	public Long expire(String key, int seconds) {
		// TODO Auto-generated method stub
		Long expire = jc.expire(key, seconds);
		return expire;
	}

	@Override
	public Long ttl(String key) {
		// TODO Auto-generated method stub
		Long ttl = jc.ttl(key);
		return ttl;
	}

	// 判断hash类型是否存在相同数据
	public Boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		Boolean hexists = jc.hexists(key, field);
		return hexists;
	}

	// sorted-set有序集合,给购物车商品id排序
	public Long zadd(String key, double score, String member) {
		// TODO Auto-generated method stub
		Long zadd = jc.zadd(key, score, member);
		return zadd;
	}

	// 反向获取sorted-set有序集合数据
	public Set<String> zrevrange(String key, long start, long end) {
		// TODO Auto-generated method stub
		Set<String> zrevrange = jc.zrevrange(key, start, end);
		return zrevrange;
	}

	// 删除sorted-set有序集合数据
	public Long zrem(String key, String member) {
		// TODO Auto-generated method stub
		Long zrem = jc.zrem(key, member);
		return zrem;
	}
}
