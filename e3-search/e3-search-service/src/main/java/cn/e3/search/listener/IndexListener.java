package cn.e3.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3.search.mapper.SearchItemMapper;
import cn.e3.search.pojo.SearchItem;

/**
 * 接受消息
 * 
 * @author Administrator 
 * 业务: 
 * 1,监听器异步接受消息,消息内容是商品id 
 * 2,根据商品id查询数据库 *         
 * 3,solr服务将会把数据库新查询数据写入索引库,达到索引库同步
 * 
 */
public class IndexListener implements MessageListener {
	
	//注入mapper接口代理对象
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	//注入solr服务对象
	@Autowired
	private SolrServer solrServer;

	public void onMessage(Message message) {

		try {
			// 初始化一个商品id
			Long itemId = null;

			if (message instanceof TextMessage) {
				// 接受消息
				TextMessage m = (TextMessage) message;
				// 获取商品id
				itemId = Long.parseLong(m.getText());
				//根据商品id查询数据库新的数据
				SearchItem searchItem = searchItemMapper.findDatabaseToSolrIndexWithID(itemId);
				
				//把数据库数据封装到文档对象
				SolrInputDocument doc = new SolrInputDocument();
				//封装文档域字段所对应值
				//封装文档域所对应数据库查询值
				doc.addField("id", searchItem.getId());
				
				//标题
				doc.addField("item_title", searchItem.getTitle());
				//买点
				doc.addField("item_sell_point", searchItem.getSell_point());
				//价格
				doc.addField("item_price", searchItem.getPrice());
				//图片地址
				doc.addField("item_image", searchItem.getImage());
				//商品类别
				doc.addField("item_category_name", searchItem.getCategory_name());
				//商品描述
				doc.addField("item_desc", searchItem.getItem_desc());
				
				//使用solr服务对象把索引文档对象写入索引库,实现索引库同步
				solrServer.add(doc);
				
				//提交
				solrServer.commit();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
