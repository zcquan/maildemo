package cn.e3.search.mapper;

import java.util.List;

import cn.e3.search.pojo.SearchItem;

public interface SearchItemMapper {
	/**
	 * 需求:查询索引库域字段对应数据库值写入索引库
	 * @return
	 */
	public List<SearchItem> findDatabaseToSolrIndex();
	
	/**
	 * 需求:查询索引库域字段对应数据库值,根据id查询
	 * @return
	 */
	public SearchItem findDatabaseToSolrIndexWithID(Long itemId);
}
