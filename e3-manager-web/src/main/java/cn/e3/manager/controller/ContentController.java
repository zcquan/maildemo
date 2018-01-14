package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.content.service.ContentService;
import cn.e3.pojo.TbContent;
import cn.e3.utils.DatagridPagebean;
import cn.e3.utils.E3mallResult;
@Controller
public class ContentController {
	
	//注入广告内容服务
	//1,引入接口
	//2,引入服务
	@Autowired
	private ContentService contentService;
	
	
	/**
	 * 需求:根据外键查询广告内容数据进行分页展示
	 * 请求:/content/query/list
	 * 参数:Long categoryId,Integer page,Integer rows
	 * 返回值:json格式DatagridPagebean
	 */
	@RequestMapping("/content/query/list")
	@ResponseBody
	public DatagridPagebean findContentListByPage(Long categoryId,
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="10") Integer rows){
		//调用远程广告服务方法
		DatagridPagebean pagebean = contentService.findContentListByPage(categoryId, page, rows);
		return pagebean;
	}
	
	/**
	 * 需求:保存广告内容数据
	 * 请求:/content/save
	 * 参数:TbContent content
	 * 返回值:json格式E3mallResult
	 */
	@RequestMapping("/content/save")
	@ResponseBody
	public E3mallResult saveContent(TbContent content){
		//调用远程服务方法
		E3mallResult result = contentService.saveContent(content);
		return result;
		
	}
	

}
