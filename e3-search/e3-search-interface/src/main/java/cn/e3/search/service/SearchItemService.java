package cn.e3.search.service;

import cn.e3.search.pojo.SolrPage;
import cn.e3.utils.E3mallResult;

public interface SearchItemService {
	
	/**
	 * 需求:询索引库域字段对应数据库值写入索引库
	 * 参数:无
	 * 参数:E3mallResult
	 */
	public E3mallResult findDatabaseToSolrIndex();
	/**
	 * 需求:接受参数,封装参数查询索引库
	 * 参数:String qName
	 * 返回值:SolrPage
	 */
	public SolrPage findSolrIndex(String qName,Integer page,Integer rows);

}
