package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.manager.service.ItemParamService;
import cn.e3.utils.E3mallResult;

@Controller
public class ItemParamController {
	
	
	//注入规格服务对象
	@Autowired
	private ItemParamService itemParamService;
	
	/**
	 * 需求:根据分类id查询商品的规格模版
	 * 请求:/item/param/query/itemcatid/{categoryId}
	 * 参数:Long categoryId
	 * 返回值:E3mallResult.ok(ItemParam)
	 */
	@RequestMapping("/item/param/query/itemcatid/{categoryId}")
	@ResponseBody
	public E3mallResult findItemParamWithCategoryId(@PathVariable Long categoryId){
		//调用规格服务
		E3mallResult result = itemParamService.findItemParamWithCategoryId(categoryId);
		return result;
	}

}
