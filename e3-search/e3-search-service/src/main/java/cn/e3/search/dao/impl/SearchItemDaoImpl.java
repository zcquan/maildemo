package cn.e3.search.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3.search.dao.SearchItemDao;
import cn.e3.search.pojo.SearchItem;
import cn.e3.search.pojo.SolrPage;
@Repository
public class SearchItemDaoImpl implements SearchItemDao {
	
	//注入solr服务对象
	@Autowired
	private SolrServer solrServer;

	/**
	 * 需求:根据参数查询索引库
	 * 参数:SolrQuery solrQuery
	 * 返回值:分页包装类对象SolrPage
	 */
	public SolrPage findSolrIndex(SolrQuery solrQuery) {
			//创建分页包装类对象
			SolrPage page = new SolrPage();
			//创建封装搜索商品列表数据集合
			List<SearchItem> sList = new ArrayList<SearchItem>();
		try {
			// T根据参数查询索引库
			QueryResponse response = solrServer.query(solrQuery);
			
			//从Response中获取文档集合对象
			SolrDocumentList results = response.getResults();
			
			//获取命中总记录数
			Long count = results.getNumFound();
			
			//把总记录数设置到分页包装类对象
			page.setTotalCount(count.intValue());
			
			//循环文档集合对象,获取文档数据
			for (SolrDocument sdoc : results) {
				//创建搜索商品对象
				SearchItem item = new SearchItem();
				//根据索引域字段获取文档数据
				String id = (String) sdoc.get("id");
				//把文档索引域值设置到搜索对象中
				item.setId(Long.parseLong(id));
				
				//标题
				String item_title = (String) sdoc.get("item_title");
				
				//获取高亮
				Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
				//第一个map的key是id
				Map<String, List<String>> map = highlighting.get(id);
				//第二个map的key是高亮字段
				List<String> list = map.get("item_title");
				//判断高亮是否存在
				if(list!=null && list.size()>0){
					item_title = list.get(0);
				}
				
				
				item.setTitle(item_title);
				
				//买点
				String item_sell_point = (String) sdoc.get("item_sell_point");
				item.setSell_point(item_sell_point);
				
				//价格
				Long item_price = (Long) sdoc.get("item_price");
				item.setPrice(item_price);
				
				//图片地址
				String item_image = (String) sdoc.get("item_image");
				item.setImage(item_image);
				
				//类别
				String item_category_name = (String) sdoc.get("item_category_name");
				item.setCategory_name(item_category_name);
				
				//把从索引库查询文档数据添加商品集合
				sList.add(item);
			}
			
			//把集合添加分页包装类对象
			page.setItemList(sList);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return page;
	}

}
