package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.search.service.SearchItemService;
import cn.e3.utils.E3mallResult;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SolrIndexController {
	
	//注入搜索服务代理对象
	//1,引入接口
	//2,引入服务
	@Autowired
	private SearchItemService searchItemService;
	
	/**
	 * 需求:询索引库域字段对应数据库值写入索引库
	 * 请求:/index/item/import
	 * 参数:无
	 * 参数:E3mallResult
	 */
	@RequestMapping("/index/item/import")
	@ResponseBody
	public E3mallResult findDatabaseToSolrIndex(){
		//调用远程service服务方法
		E3mallResult result = searchItemService.findDatabaseToSolrIndex();
		return result;
	}

}
