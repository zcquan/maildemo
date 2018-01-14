package cn.e3.fm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MyFreemakerTest {
	
	/**
	 * 需求:freemarker入门案例: 获取基本数据
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * 模版语法:
	 * 获取基本数据类型
	 * 语法: ${key} key就是map的key
	 * @throws Exception s
	 */
	@Ignore
	@Test
	public void test01() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("hello.ftl");
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("hello", "freemarker很简单,非常简单!");
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\first.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}
	
	/**
	 * 需求:获取特殊数字类型数据
	 * 例如:
	 * map.put("num",0.2);
	 * 金额:$0.2
	 * 百分比:20%
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * 模版语法:
	 * 获取基本数据类型
	 * 语法: 
	 * 金额:${num?currency}
	 * 百分比:${num?percent}
	 * @throws Exception s
	 */
	@Ignore
	@Test
	public void test02() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("num.ftl");
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("num", 0.2);
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\num.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}
	
	/**
	 * 需求:fm对null值处理
	 * 例如:
	 * map.put("name",null);
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * 模版语法:
	 * 1,?
	 * ${name?default('0')}
	 * 2,!
	 * ${name!"默认值"}
	 * ${name!}
	 * 3,if
	 * <#if name??>
	 * ${name}
	 * </#if>
	 */
	@Ignore
	@Test
	public void test03() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("null.ftl");
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("name","张无忌");
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\name.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}
	
	/**
	 * 需求:获取pojo类型的数据
	 * 例如:
	 * map.put("p",person);
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * 模版语法:
	 * 获取基本数据类型
	 * 语法:
	 * ${p.id!}
	 * ${p.name!}
	 * ${p.age!}
	 * ${p.address!}
	 * @throws Exception s
	 */
	@Ignore
	@Test
	public void test04() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("person.ftl");
		
		//创建person对象
		Person p = new Person();
		p.setId("u1010111111");
		p.setName("金山炮");
		p.setAge(12);
		p.setAddress("北朝鲜");
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("p", p);
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\p.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}
	
	/**
	 * 需求:获取list集合类型的数据
	 * 例如:
	 * List<Person> pList = new ArrayList();
	 * map.put("pList",pList);
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * el表达式获取:
	 * <c:foreach item="${pList}" var="p" varStatus="p">
	 * 	${p.id}
	 *  .......
	 * </c:foreach>
	 * 模版语法:
	 * 语法:
	 * <#list pList as p>
	 * 角标:${别名_index}
	 * ${p.id!}
	 * ${p.name!}
	 * ${p.age!}
	 * ${p.address!}
	 * <#list>
	 * 判断语法:
	 * <#if conditon>
	 * <#elseif conditon>
	 * <#else>
	 * </#if>
	 * 需求:列表展示,奇数行红色展示,偶数行蓝色展示
	 * @throws Exception s
	 */
	@Ignore
	@Test
	public void test05() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("list.ftl");
		
		//创建list集合,封装对象
		List<Person> pList = new ArrayList<Person>();
		//创建person对象
		Person p1 = new Person();
		p1.setId("u1010111111");
		p1.setName("金山炮");
		p1.setAge(12);
		p1.setAddress("北朝鲜");
		
		Person p2 = new Person();
		p2.setId("u1010111112");
		p2.setName("川普");
		p2.setAge(12);
		p2.setAddress("美国华盛顿");
		
		
		Person p3 = new Person();
		p3.setId("u1010111131");
		p3.setName("金山炮他媳妇");
		p3.setAge(16);
		p3.setAddress("北朝鲜");
		
		pList.add(p1);
		pList.add(p2);
		pList.add(p3);
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("pList", pList);
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\list.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}
	
	/**
	 * 需求:获取时间类型数据
	 * 例如:
	 * map.put("today",new Date());
	 * freemarker生成静态页面三要素:
	 * 1,数据<map>
	 * 2,freemarkerAPI
	 * 3,模版文件(.ftl)[HTML+模版指令]
	 * 模版语法:
	 * 时间:${today?time}
	 * 日期:${today?date}
	 * 日期时间:${today?datetime}
	 * 格式化:${today?string('yyyy-MM-dd')}
	 */
	@Ignore
	@Test
	public void test06() throws Exception{
		//创建freemarker核心配置对象
		Configuration cf = new Configuration(Configuration.getVersion());
		//指定模版文件存储路径
		cf.setDirectoryForTemplateLoading(new File("F:\\template"));
		//指定模版文件编码
		cf.setDefaultEncoding("UTF-8");
		//读取模版文件,获取模版对象
		Template template = cf.getTemplate("date.ftl");
		
		//准备数据
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("today",new Date());
		
		//创建一个输出流对象,把生成html页面写入磁盘
		Writer out = new FileWriter(new File("F:\\template\\out\\date.html"));
		
		//生成HTML页面
		template.process(maps, out);
		
		//关闭
		out.close();
		
		
	}


}
