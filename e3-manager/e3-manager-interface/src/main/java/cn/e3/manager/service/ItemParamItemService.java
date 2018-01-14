package cn.e3.manager.service;

import cn.e3.utils.E3mallResult;

public interface ItemParamItemService {
	
	/**
	 * 需求:根据商品id查询规格参数
	 * 参数:Long itemId
	 * 返回值:E3mallResult
	 */
	public E3mallResult findItemParamItemByItemId(Long itemId);

}
