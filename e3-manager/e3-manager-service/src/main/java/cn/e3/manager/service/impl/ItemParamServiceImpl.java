package cn.e3.manager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.manager.service.ItemParamService;
import cn.e3.mapper.TbItemParamMapper;
import cn.e3.pojo.TbItemParam;
import cn.e3.pojo.TbItemParamExample;
import cn.e3.pojo.TbItemParamExample.Criteria;
import cn.e3.utils.E3mallResult;
@Service
public class ItemParamServiceImpl implements ItemParamService {
	
	//注入商品规格模版mapper接口代理对象
	@Autowired
	private TbItemParamMapper itemParamMapper;

	/**
	 * 需求:根据分类id查询商品的规格模版
	 * 参数:Long categoryId
	 * 返回值:E3mallResult.ok(ItemParam)
	 */
	public E3mallResult findItemParamWithCategoryId(Long categoryId) {
		// 创建example对象
		TbItemParamExample example = new TbItemParamExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数:根据分类id查询商品的规格模版
		createCriteria.andItemCatIdEqualTo(categoryId);
		//执行查询
		List<TbItemParam> list = itemParamMapper.selectByExampleWithBLOBs(example);
		
		TbItemParam itemParam = null;
		
		//判断此分类是否关联了模版
		if(list!=null && list.size()>0){
			itemParam = list.get(0);
		}
		
		//返回值
		return E3mallResult.ok(itemParam);
	}

}
