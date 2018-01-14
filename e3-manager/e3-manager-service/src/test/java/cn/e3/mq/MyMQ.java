package cn.e3.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class MyMQ {
	
	
	/**
	 * 需求:测试使用mq的java客户端连接activeMQ消息服务器,实现发送消息
	 * @throws Exception 
	 */
	@Test
	public void sendMessageWithPTP() throws Exception{
		//指定消费服务器地址: ip,端口,通信协议
		String brokerURL = "tcp://192.168.66.66:61616";
		//创建消息服务工厂对象,连接消息服务器
		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		//从工厂中获取一个连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接中获取一个回话对象session
		//参数1:自定义事物策略,如果使用自定义事物策略(设置为true),第二个activeMQ提供事物将会被忽略
		//参数2:使用activeMQ自动提供事物策略,使用activeMQ自动应答模式 AUTO_ACKNOWLEDGE
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//创建消息空间,且给消息空间起一个名称 myqueue
		Queue queue = session.createQueue("myqueue");
		
		//从session中获取消息发送者,且告知消息往那发送
		MessageProducer producer = session.createProducer(queue);
		
		
		//创建消息对象,封装消息
		TextMessage message = new ActiveMQTextMessage();
		message.setText("张三丰,天下无敌!");
		//发送消息
		producer.send(message);
		
		//关闭资源
		producer.close();
		session.close();
		connection.close();
		
		
		
		
		
		
		
		
		
		
	}
	
	/**
	 * 需求:测试使用mq的java客户端连接activeMQ消息服务器,实现发送消息
	 * @throws Exception 
	 */
	@Test
	public void sendMessageWithPS() throws Exception{
		//指定消费服务器地址: ip,端口,通信协议
		String brokerURL = "tcp://192.168.66.66:61616";
		//创建消息服务工厂对象,连接消息服务器
		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		//从工厂中获取一个连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接中获取一个回话对象session
		//参数1:自定义事物策略,如果使用自定义事物策略(设置为true),第二个activeMQ提供事物将会被忽略
		//参数2:使用activeMQ自动提供事物策略,使用activeMQ自动应答模式 AUTO_ACKNOWLEDGE
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//创建消息空间,且给消息空间起一个名称 mytopic
		//点对点:queue
		//发布订阅: topic
		Topic topic = session.createTopic("mytopic");
		
		//从session中获取消息发送者,且告知消息往那发送
		MessageProducer producer = session.createProducer(topic);
		
		
		//创建消息对象,封装消息
		TextMessage message = new ActiveMQTextMessage();
		message.setText("张三丰,天下无敌!,发布订阅模式");
		//发送消息
		producer.send(message);
		
		//关闭资源
		producer.close();
		session.close();
		connection.close();
				
	}

}
