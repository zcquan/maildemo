package cn.e3.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.content.service.ContentService;
import cn.e3.utils.AdItem;
import cn.e3.utils.JsonUtils;

@Controller
public class PageController {
	
	
	//注入广告内容服务
	//1,引入接口
	//2,引入服务
	@Autowired
	private ContentService contentService;
	
	//注入大广告位唯一标识
	@Value("${BIG_AD_CATEGORY_ID}")
	private Long BIG_AD_CATEGORY_ID;
	
	/**
	 * 需求:跳转到门户系统首页
	 * 业务:
	 * 跳转到门户系统首页之前,需要做一些初始化工作:
	 * 1,初始化大广告数据
	 * 2,初始化小广告数据
	 * 3,初始化楼层广告
	 * 4,.........
	 * 
	 */
	@RequestMapping("index")
	public String showIndex(Model model){
		
		//1,初始化大广告数据
		//调用广告内容服务,查询大广告数据
		//大广告位区域id是89,查询首页广告数据时候是根据分类id查询,分类id一旦确定,不再改变,是一个常量
		List<AdItem> adList = contentService.findContentAdList(BIG_AD_CATEGORY_ID);
		//把广告数据转换成json字符串
		String adJson = JsonUtils.objectToJson(adList);
		//页面回显
		model.addAttribute("ad1", adJson);
		return "index";
	}

}
