package cn.e3.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class ReceiveMessage {
	
	/**
	 * 需求:接受消息
	 * 模式:点对点模式
	 * 方式:同步模式
	 * @throws Exception 
	 */
	@Test
	public void receiveMessageWithTONGBU() throws Exception{
		//指定消息服务器地址: ip,协议,端口
		String brokerURL = "tcp://192.168.66.66:61616";
		//创建消息工厂对象,连接activeMQ消息服务器
		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		//从工厂对象中获取连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取session回话对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//指定接受消息空间地址,指定地址和创建发送消息空间地址一致
		Queue queue = session.createQueue("myqueue");
		//指定消息接受者,指定消息的消费地址
		MessageConsumer consumer = session.createConsumer(queue);
		
		//获取消息
		Message message = consumer.receive(1000);
		if(message instanceof TextMessage){
			TextMessage m = (TextMessage) message;
			System.out.println("点对点模式接受消息:"+m.getText());
		}
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
		
		
	}
	
	/**
	 * 需求:接受消息
	 * 模式:点对点模式
	 * 方式:异步模式
	 * @throws Exception 
	 */
	@Test
	public void receiveMessageWithYIBU() throws Exception{
		//指定消息服务器地址: ip,协议,端口
		String brokerURL = "tcp://192.168.66.66:61616";
		//创建消息工厂对象,连接activeMQ消息服务器
		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		//从工厂对象中获取连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取session回话对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//指定接受消息空间地址,指定地址和创建发送消息空间地址一致
		Queue queue = session.createQueue("myqueue");
		//指定消息接受者,指定消息的消费地址
		MessageConsumer consumer = session.createConsumer(queue);
		
		//异步模式接受消息(监听模式)
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
				if(message instanceof TextMessage){
					TextMessage m = (TextMessage) message;
					try {
						System.out.println("点对点模式接受消息:"+m.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		//模拟服务,端口一直阻塞
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
		
		
	}
	
	@Test
	public void receiveMessageWithYIBUPS() throws Exception{
		//指定消息服务器地址: ip,协议,端口
		String brokerURL = "tcp://192.168.66.66:61616";
		//创建消息工厂对象,连接activeMQ消息服务器
		ConnectionFactory cf = new ActiveMQConnectionFactory(brokerURL);
		//从工厂对象中获取连接
		Connection connection = cf.createConnection();
		//开启连接
		connection.start();
		//从连接对象中获取session回话对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//指定接受消息空间地址,指定地址和创建发送消息空间地址一致
		Topic topic = session.createTopic("mytopic");
		//指定消息接受者,指定消息的消费地址
		MessageConsumer consumer = session.createConsumer(topic);
		
		//异步模式接受消息(监听模式)
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				// TODO Auto-generated method stub
				if(message instanceof TextMessage){
					TextMessage m = (TextMessage) message;
					try {
						System.out.println("点对点模式接受消息:"+m.getText());
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		//模拟服务,端口一直阻塞
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
		
		
		
	}


}
