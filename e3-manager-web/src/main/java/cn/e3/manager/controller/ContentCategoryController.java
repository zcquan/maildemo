package cn.e3.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.content.service.ContentCategoryService;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.TreeNode;

@Controller
public class ContentCategoryController {
	
	//注入广告服务分类对象
	//1,引入广告服务接口
	//2,引入广告服务
	@Autowired
	private ContentCategoryService categoryService;
	
	/**
	 * 需求:根据父id查询节点下面子节点
	 * 请求:/content/category/list
	 * 参数:Long parentId
	 * 返回值:json格式List<TreeNode>
	 * 服务是否引入?
	 * 业务:
	 * 1,easyUI 树形节点传递的参数是id,id和方法中参数名称不匹配,因此需要起别名
	 * 2,属性页面加载初始化时,查询树形节点没有传递参数,因此首次加载,需要默认加载顶级树形节点,默认值:0
	 */
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<TreeNode> findContentCategoryTreeNodeList(
			@RequestParam(defaultValue="0",value="id") Long parentId){
		//调用广告服务 分类对象方法
		List<TreeNode> nodeList = categoryService.findContentCategoryTreeNodeList(parentId);
		return nodeList;
	}
	
	/**
	 * 需求:新建树形节点
	 * 请求:/content/category/create
	 * 参数:Long parentId,String name
	 * 返回值:json格式E3mallResult
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3mallResult createNode(Long parentId,String name){
		//调用远程服务方法
		E3mallResult result = categoryService.createNode(parentId, name);
		return result;
	}

}
