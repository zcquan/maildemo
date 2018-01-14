package cn.e3.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3.content.service.ContentService;
import cn.e3.jedis.dao.JedisDao;
import cn.e3.mapper.TbContentMapper;
import cn.e3.pojo.TbContent;
import cn.e3.pojo.TbContentExample;
import cn.e3.pojo.TbContentExample.Criteria;
import cn.e3.utils.AdItem;
import cn.e3.utils.DatagridPagebean;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;
@Service
public class ContentServiceImpl implements ContentService {
	
	//注入内容mapper接口代理对象
	@Autowired
	private TbContentMapper contentMapper;
	
	//注入广告宽
	@Value("${WIDTH}")
	private Integer WIDTH;
	
	@Value("${WIDTHB}")
	private Integer WIDTHB;
	
	//高
	@Value("${HEIGHT}")
	private Integer HEIGHT;
	
	@Value("${HEIGHTB}")
	private Integer HEIGHTB;
	
	//注入jedisDao对象
	@Autowired
	private JedisDao jedisDao;
	
	//注入首页缓存唯一标识
	@Value("${INDEX_CACHE}")
	private String INDEX_CACHE;
	
	
	

	/**
	 * 需求:根据外键查询广告内容数据进行分页展示
	 * 参数:Long categoryId,Integer page,Integer rows
	 * 返回值:DatagridPagebean
	 */
	public DatagridPagebean findContentListByPage(Long categoryId,
			Integer page, Integer rows) {
		// 创建example对象
		TbContentExample example = new TbContentExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数:根据外键查询广告内容数据
		createCriteria.andCategoryIdEqualTo(categoryId);
		
		//设置分页查询参数
		PageHelper.startPage(page, rows);
		
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		
		//创建pageINfo分页工具类对象,从pageINfo获取分页数据
		PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(list);
		
		//创建分页包装类对象
		DatagridPagebean pagebean = new DatagridPagebean();
		//设置总记录数
		pagebean.setTotal(pageInfo.getTotal());
		//设置总记录
		pagebean.setRows(list);
		
		return pagebean;
	}

	/**
	 * 需求:保存广告内容数据
	 * 参数:TbContent content
	 * 返回值:E3mallResult
	 * 缓存同步:
	 * 删除,修改,添加广告数据时先删除缓存.
	 * 用户再次查询,缓存已经不存在了,需要从新从数据库查询新的数据.
	 * 从而达到缓存同步目的
	 */
	public E3mallResult saveContent(TbContent content) {
		
		//先删除缓存
		jedisDao.hdel(INDEX_CACHE, content.getCategoryId()+"");
		
		// 补全时间
		Date date = new Date();
		content.setUpdated(date);
		content.setCreated(date);
		//保存
		contentMapper.insertSelective(content);
		return E3mallResult.ok();
	}

	/**
	 * 需求:查询首页加载广告数据
	 * @param categoryId
	 * @return
	 * 首页,获取其他广告页面加载广告数据是从数据库中查询,为了减轻数据库压力,给广告服务添加缓存.
	 * 广告数据查询时候,首先先查询redis缓存,如果缓存中有数据,直接返回即可,否则在次查询数据库
	 * 但同时,把查询的数据库数据放入缓存.
	 * redis缓存服务器数据库结构:
	 * 缓存数据结构:hash
	 * key:INDEX_CACHE(首页缓存)  FOOD_CACHE(食品页面缓存)  CLOSE_CACHE(服装缓存)
	 * field:categoryId(缓存分类)
	 * value:json(缓存数据)
	 * 缓存流程:
	 * 1,先查询缓存,如果有缓存,直接返回,不再查询数据库
	 * 2,如果缓存不存在,查询数据库,同时需要把查询的数据放入缓存
	 */
	public List<AdItem> findContentAdList(Long categoryId) {
		
		try {
			//先查询缓存
			String adJson = jedisDao.hget(INDEX_CACHE, categoryId+"");
			//判断redis中是否存在广告缓存
			if(StringUtils.isNotBlank(adJson)){
				//把广告json字符串数据转换成集合对象,返回
				List<AdItem> adList = JsonUtils.jsonToList(adJson, AdItem.class);
				
				return adList;
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//创建广告数据封装集合对象List<AdItem>
		List<AdItem> adList = new ArrayList<AdItem>();
		
		// 创建example对象
		TbContentExample example = new TbContentExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数
		createCriteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		//循环广告内容数据,把广告内容封装广告集合对象
		for (TbContent tbContent : list) {
			//创建广告对象AdItem
			AdItem ad = new AdItem();
			//提示信息
			ad.setAlt(tbContent.getSubTitle());
			//售卖地址
			ad.setHref(tbContent.getUrl());
			//图片地址
			ad.setSrc(tbContent.getPic());
			//图片地址
			ad.setSrcB(tbContent.getPic2());
			
			//设置图片宽,高
			ad.setHeight(HEIGHT);
			ad.setHeightB(HEIGHTB);
			ad.setWidth(WIDTH);
			ad.setWidthB(WIDTHB);
			
			//把广告对象添加到广告集合
			adList.add(ad);
		}
		
		
		//放入缓存,当其他用户查询广告数据时候,查询缓存数据
		jedisDao.hset(INDEX_CACHE, categoryId+"", JsonUtils.objectToJson(adList));
		
		
		return adList;
	}

}
