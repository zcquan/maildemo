package cn.e3.manager.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3.manager.service.ItemService;
import cn.e3.mapper.TbItemDescMapper;
import cn.e3.mapper.TbItemMapper;
import cn.e3.mapper.TbItemParamItemMapper;
import cn.e3.mapper.TbItemParamMapper;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.pojo.TbItemDescExample;
import cn.e3.pojo.TbItemDescExample.Criteria;
import cn.e3.pojo.TbItemExample;
import cn.e3.pojo.TbItemParamItem;
import cn.e3.utils.DatagridPagebean;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.IDUtils;
@Service
public class ItemServiceImpl implements ItemService {
	
	//注入商品mapper接口代理对象
	@Autowired
	private TbItemMapper itemMapper;
	
	//注入商品描述mapper接口代理对象
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	//注入商品规格参数mapper接口代理对象
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	
	
	
	//注入消息模版发送对象
	@Autowired
	private JmsTemplate jmsTemplate;
	
	//注入mq消息服务器空间对象
	@Autowired
	private ActiveMQTopic activeMQTopic;

	/**
	 * 需求:根据id查询商品数据
	 */
	public TbItem findItemByID(Long itemId) {
		// 根据主键查询商品数据
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		return item;
	}

	/**
	 * 需求:分页查询商品列表
	 * 参数:Integer page,Integer rows
	 * 返回值:DatagridPagebean
	 * 思考:
	 * 服务是否发布?
	 */
	public DatagridPagebean findItemListByPage(Integer page, Integer rows) {
		// 创建example对象
		TbItemExample example = new TbItemExample();
		//在查询之前,设置分页
		PageHelper.startPage(page, rows);
		//执行查询
		List<TbItem> list = itemMapper.selectByExample(example);
		
		//创建PageInfo,分页插件PageInfo对象封装了所有的分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
		
		//创建分页包装类对象DatagridPagebean,封装分页信息
		DatagridPagebean pagebean = new DatagridPagebean();
		//设置总记录数
		pagebean.setTotal(pageInfo.getTotal());
		//设置总记录
		pagebean.setRows(list);
		
		
		//返回分页对象
		return pagebean;
	}

	/**
	 * 需求:保存商品对象
	 * 参数:TbItem item,TbItemDesc itemDesc,ItemParam praram
	 * 返回值:E3mallResult
	 * 添加,修改,删除 发送消息给搜索服务:搜索服务接受消息后立马同步索引库
	 * 
	 */
	public E3mallResult saveItem(TbItem item, TbItemDesc itemDesc,String itemParams) {
		// 生成商品id
		//毫秒+随机数
		final long itemId = IDUtils.genItemId();
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除'
		item.setStatus((byte)1);
		//创建时间
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		
		//保存商品对象
		itemMapper.insert(item);
		
		//保存商品描述对象
		itemDesc.setItemId(itemId);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		
		//保存商品描述对象
		itemDescMapper.insert(itemDesc);
		
		//创建商品规格对象,封装规格值
		TbItemParamItem itemParamItem = new TbItemParamItem();
		//设置相关属性值
		itemParamItem.setItemId(itemId);
		itemParamItem.setParamData(itemParams);
		itemParamItem.setCreated(date);
		itemParamItem.setUpdated(date);
		
		
		//保存商品规格参数
		itemParamItemMapper.insertSelective(itemParamItem);
		
		//发送消息
		//使用jms模版对象发送消息
		jmsTemplate.send(activeMQTopic, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// 发送消息
				return session.createTextMessage(""+itemId);
			}
		});
		
		return E3mallResult.ok();
	}

	/**
	 * 根据id查询商品描述数据
	 * @param itemId
	 * @return
	 */
	public TbItemDesc findItemDescByID(Long itemId) {
		//创建example对象
		TbItemDescExample example = new TbItemDescExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数
		createCriteria.andItemIdEqualTo(itemId);
		//执行查询
		List<TbItemDesc> list = itemDescMapper.selectByExampleWithBLOBs(example);
		
		TbItemDesc itemDesc = null;
		
		//判断
		if(list!=null && list.size()>0){
			itemDesc = list.get(0);
		}
		return itemDesc;
	}

}
