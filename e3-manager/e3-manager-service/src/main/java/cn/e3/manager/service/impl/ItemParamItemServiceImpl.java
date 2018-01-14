package cn.e3.manager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.manager.service.ItemParamItemService;
import cn.e3.mapper.TbItemParamItemMapper;
import cn.e3.pojo.TbItemParamItem;
import cn.e3.pojo.TbItemParamItemExample;
import cn.e3.pojo.TbItemParamItemExample.Criteria;
import cn.e3.utils.E3mallResult;
@Service
public class ItemParamItemServiceImpl implements ItemParamItemService {
	
	
	//注入规格参数接口
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;

	/**
	 * 需求:根据商品id查询规格参数
	 * 参数:Long itemId
	 * 返回值:E3mallResult
	 */
	public E3mallResult findItemParamItemByItemId(Long itemId) {
		// 创建example对象
		TbItemParamItemExample example = new TbItemParamItemExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数:根据商品id查询规格参数
		createCriteria.andItemIdEqualTo(itemId);
		//执行查询
		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
		
		TbItemParamItem itemParamItem = null;
		
		//判断规格参数数据是否存在
		if(list!=null && list.size()>0){
			itemParamItem = list.get(0);
		}
		//返回值
		return E3mallResult.ok(itemParamItem);
	}

}
