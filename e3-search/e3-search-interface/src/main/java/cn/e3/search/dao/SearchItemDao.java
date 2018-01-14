package cn.e3.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3.search.pojo.SolrPage;

public interface SearchItemDao {
	
	/**
	 * 需求:根据参数查询索引库
	 * 参数:SolrQuery solrQuery
	 * 返回值:分页包装类对象SolrPage
	 */
	public SolrPage findSolrIndex(SolrQuery solrQuery);

}
