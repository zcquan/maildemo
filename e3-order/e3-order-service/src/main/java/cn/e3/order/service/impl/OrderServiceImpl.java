package cn.e3.order.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.mapper.TbOrderItemMapper;
import cn.e3.mapper.TbOrderMapper;
import cn.e3.mapper.TbOrderShippingMapper;
import cn.e3.order.pojo.OrderInfo;
import cn.e3.order.service.OrderService;
import cn.e3.pojo.TbOrder;
import cn.e3.pojo.TbOrderItem;
import cn.e3.pojo.TbOrderShipping;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.IDUtils;
@Service
public class OrderServiceImpl implements OrderService {
	
	//注入订单mapper接口代理对象
	@Autowired
	private TbOrderMapper orderMapper;
	
	//注入订单明细mapper接口代理对象
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	//注入收货人地址mapper接口代理对象
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	/**
	 * 需求:提交订单(把订单数据保存到数据库)
	 * 参数:OrderInfo
	 * 返回值:订单号
	 */
	public E3mallResult createOrder(OrderInfo orderInfo) {
		// 保存订单
		//获取订单对象
		TbOrder orders = orderInfo.getOrders();
		//生成订单号,订单号不能重复
		//1,毫秒+随机数
		//2,redis自动增加
		String orderId = IDUtils.genOrderId()+"";
		orders.setOrderId(orderId);
		//邮费。精确到2位小数;单位:元。如:200.07，表示:200元7分
		orders.setPostFee("0");
		//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orders.setStatus(1);
		//订单创建
		Date date = new Date();
		orders.setCreateTime(date);
		orders.setUpdateTime(date);
		
		//保存
		orderMapper.insertSelective(orders);
		
		// 保存订单明细
		//获取订单明细
		List<TbOrderItem> list = orderInfo.getOrderItems();
		//循环集合,保存集合中每一个对象
		for (TbOrderItem tbOrderItem : list) {
			//补全响应属性值
			//生成订单明细id
			String orderItemId = UUID.randomUUID().toString();
			tbOrderItem.setId(orderItemId);
			//设置外键
			tbOrderItem.setOrderId(orderId);
			//保存
			orderItemMapper.insertSelective(tbOrderItem);
		}
		
		//保存收货人地址
		//获取收货人地址对象
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		orderShipping.setOrderId(orderId);
		orderShipping.setCreated(date);
		orderShipping.setUpdated(date);
		//保存
		orderShippingMapper.insertSelective(orderShipping);
		//反对订单号
		return E3mallResult.ok(orderId);
	}

}
