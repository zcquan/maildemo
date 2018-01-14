package cn.e3.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.JedisCluster;

public class JedisSpring {

	/**
	 * 需求:jedis整合spirng,测试redis集群
	 * 
	 */
	@Test
	public void springwithJedis() {
		// 加载spring配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext(
				"classpath*:spring/jedis.xml");
		//获取集群;对象
		JedisCluster jc = app.getBean(JedisCluster.class);
		//设置值
		jc.set("address", "武当山");
	}
}
