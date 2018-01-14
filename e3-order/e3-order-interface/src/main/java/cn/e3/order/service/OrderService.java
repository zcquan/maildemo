package cn.e3.order.service;

import cn.e3.order.pojo.OrderInfo;
import cn.e3.utils.E3mallResult;

public interface OrderService {
	
	/**
	 * 需求:提交订单(把订单数据保存到数据库)
	 * 参数:OrderInfo
	 * 返回值:订单号
	 */
	public E3mallResult createOrder(OrderInfo orderInfo);

}
