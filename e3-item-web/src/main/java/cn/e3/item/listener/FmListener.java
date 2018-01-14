package cn.e3.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;
import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 需求:接受消息,同步生成静态页面
 * @author Administrator
 * 同步静态页面流程:
 * 1,后台管理系统添加,修改,删除时候,发送消息
 * 2,静态系统接受消息,根据消息内容(商品id)查询数据库,同步静态页面
 */
public class FmListener implements MessageListener{
	
	//注入商品服务对象
	@Autowired
	private ItemService itemService;
	
	//注入freemarker核心对象
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	//注入静态服务器地址
	@Value("${STATIC_SERVER_URL}")
	private String STATIC_SERVER_URL;

	@Override
	public void onMessage(Message message) {
		try {
			//接受消息
			Long itemId = null;
			if(message instanceof TextMessage){
				TextMessage m = (TextMessage) message;
				itemId = Long.parseLong(m.getText());
			}
			
			
			//获取freemarker配置对象
			Configuration cf = freeMarkerConfigurer.getConfiguration();
			//获取模版对象
			Template template = cf.getTemplate("item.ftl");
			
			//创建map对象
			Map<String, Object> maps = new HashMap<String, Object>();
			
			//休眠1s
			Thread.sleep(1000);
			
			//模版页面需求数据
			TbItem item = itemService.findItemByID(itemId);
			
			//把数据放入map
			maps.put("item", item);
			
			//创建输出流对象: ftp 工具 远程上传linux服务器
			Writer out = new FileWriter(new File(STATIC_SERVER_URL+itemId+".html"));
			
			template.process(maps, out);
			
			
			out.close();
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
