package cn.e3.search.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3.search.pojo.SolrPage;
import cn.e3.search.service.SearchItemService;

@Controller
public class SearchItemController {
	
	//注入搜索服务
	@Autowired
	private SearchItemService searchItemService;
	
	/**
	 * 需求:接受参数,封装参数查询索引库
	 * 请求:http://localhost:8085/search.html?q=
	 * 参数:String qName
	 * 返回值:search.jsp
	 */
	@RequestMapping("search")
	public String search(@RequestParam(value="q") String qName,
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="60") Integer rows,
			Model model){
		
		//乱码处理
		try {
			qName = new String(qName.getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//调用远程搜索服务方法
		SolrPage sPage = searchItemService.findSolrIndex(qName, page, rows);
		//回显主查询条件
		model.addAttribute("query", qName);
		//回显总页码
		model.addAttribute("totalPages", sPage.getTotalPages());
		//回显当前页
		model.addAttribute("page", page);
		
		//回显商品记录
		model.addAttribute("itemList", sPage.getItemList());
		
		return "search";
	}

}
