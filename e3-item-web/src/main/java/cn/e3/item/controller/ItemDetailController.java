package cn.e3.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.manager.service.ItemParamItemService;
import cn.e3.manager.service.ItemParamService;
import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.utils.E3mallResult;

@Controller
public class ItemDetailController {
	
	//注入商品服务对象
	@Autowired
	private ItemService itemService;
	
	//注入商品规格参数服务对象
	@Autowired
	private ItemParamItemService itemParamItemService;
	
	/**
	 * 需求:进入商品详情页面
	 * 请求:http://localhost:8086/151324585003255.html
	 * 业务:
	 * 商品详情页面需要什么数据
	 * 1,商品数据
	 * 2,商品描述
	 * 3,商品规格
	 */
	@RequestMapping("{itemId}")
	public String showItem(@PathVariable Long itemId,Model model){
		//根据id查询商品数据
		TbItem item = itemService.findItemByID(itemId);
		//根据id查询商品描述数据
		TbItemDesc itemDesc = itemService.findItemDescByID(itemId);
		
		model.addAttribute("item", item);
		
		model.addAttribute("itemDesc", itemDesc);
		
		return "item";
	}
	
	/**
	 * 需求: 查询商品描述信息
	 * 请求:/item/desc/{itemId}
	 * 返回值:itemDesc
	 */
	@RequestMapping("/item/desc/{itemId}")
	@ResponseBody
	public TbItemDesc  findItemDesc(@PathVariable Long itemId){
		TbItemDesc itemDesc = itemService.findItemDescByID(itemId);
		
		return itemDesc;
	}
	
	
	/**
	 * 需求:查询商品规格信息
	 * 请求:/item/param/{itemId}
	 * 返回值:E3mallResult
	 */
	@RequestMapping("/item/param/{itemId}")
	@ResponseBody
	public E3mallResult findItemParamItem(@PathVariable Long itemId){
		//调用远程服务
		E3mallResult result = itemParamItemService.findItemParamItemByItemId(itemId);
		return result;
	}
}
