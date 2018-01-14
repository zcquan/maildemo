package cn.e3.mq;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMQ {

	/**
	 * 需求:spring整合mq接受消息
	 * @throws IOException 
	 */
	@Test
	public void receiveMessage() throws IOException {
		// 加载spring配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext(
				"classpath*:spring/activemq-consumer.xml");
		//模拟服务
		System.in.read();

	}

}
