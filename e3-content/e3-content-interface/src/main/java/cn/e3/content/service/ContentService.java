package cn.e3.content.service;

import java.util.List;

import cn.e3.pojo.TbContent;
import cn.e3.utils.AdItem;
import cn.e3.utils.DatagridPagebean;
import cn.e3.utils.E3mallResult;

public interface ContentService {
	
	/**
	 * 需求:根据外键查询广告内容数据进行分页展示
	 * 参数:Long categoryId,Integer page,Integer rows
	 * 返回值:DatagridPagebean
	 */
	public DatagridPagebean findContentListByPage(Long categoryId,Integer page,Integer rows);
	/**
	 * 需求:保存广告内容数据
	 * 参数:TbContent content
	 * 返回值:E3mallResult
	 */
	public E3mallResult saveContent(TbContent content);
	/**
	 * 需求:查询首页加载广告数据
	 * @param categoryId
	 * @return
	 */
	public List<AdItem> findContentAdList(Long categoryId);

}
