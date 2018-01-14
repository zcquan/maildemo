package cn.e3.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@RestController
public class FmController {
	
	//注入freemarker模版核心对象
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	/**
	 * 需求:测试freemarker使用
	 * @throws Exception 
	 * @throws ParseException 
	 * @throws MalformedTemplateNameException 
	 * @throws TemplateNotFoundException 
	 */
	@RequestMapping("/fm/{name}")
	public String showFm(Model model,@PathVariable String name) throws Exception{
		//从核心配置对象中获取freemarker配置对象
		Configuration cf = freeMarkerConfigurer.getConfiguration();
		//直接读取服务器模版文档
		Template template = cf.getTemplate("hello.ftl");
		model.addAttribute("hello", name);
		//创建输出流对象
		Writer out = new FileWriter(new File("F:\\template\\out\\springWithfm.html"));
		//生成html
		template.process(model, out);
	
		return "success";
	}

}
