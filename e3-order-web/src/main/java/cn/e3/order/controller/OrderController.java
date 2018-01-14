package cn.e3.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.cart.service.CartService;
import cn.e3.order.pojo.OrderInfo;
import cn.e3.order.service.OrderService;
import cn.e3.order.utils.CookieUtils;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbUser;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;

@Controller
public class OrderController {
	
	//注入购物车服务对象
	@Autowired
	private CartService cartService;
	
	//注入cookie购物车唯一标识
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	
	//注入订单服务对象
	@Autowired
	private OrderService orderService;
	
	/**
	 * 需求:订单结算
	 * 请求:http://localhost:8092/order/order-cart.html
	 * 返回值:order-cart.jsp
	 */
	@RequestMapping("/order/order-cart")
	public String orderCart(HttpServletRequest request){
		
		//从request中获取用户身份信息
		TbUser user = (TbUser) request.getAttribute("user");
		
		//合并购物车数据
		//查询cookie购物车购物车列表,判断购物车列表是否有数据,如果有数据,直接合并
		List<TbItem> cartList = this.getCookieCartList(request);
		
		//判断cookie购物车是否存在值
		if(!cartList.isEmpty()){
			//合并购物车
			cartService.mergeRedisCart(user.getId(), cartList);
		}
		
		//1,收货地址(查询,修改,新增,默认)		
		//2,支付方式(在线支付,货到付款,银行支付) 货到付款		
		//3,购物清单(查询购物车)
		cartList = cartService.findRedisSortedCart(user.getId());
		//放入request域
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	/**
	 * 抽取: 获取cookie购物车数据
	 * @param request
	 * @return
	 */
	private List<TbItem> getCookieCartList(HttpServletRequest request) {
		// 获取cookie购物车数据
		String cartJson = CookieUtils.getCookieValue(request, COOKIE_CART, true);
		//判断cookie购物车是否存在数据
		if(StringUtils.isBlank(cartJson)){
			//返回空购物车
			return new ArrayList<TbItem>();
		}
		//否则购物车中有数据
		List<TbItem> cartList = JsonUtils.jsonToList(cartJson, TbItem.class);
		
		return cartList;
	}

	
	/**
	 * 需求:提交订单(把订单数据保存到数据库)
	 * 请求:/order/create.html
	 * 参数:OrderInfo
	 * 返回值:success.jsp
	 */
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo,Model model){
		
		//调用订单服务,提交订单
		E3mallResult result = orderService.createOrder(orderInfo);
		//回显订单号
		model.addAttribute("orderId", result.getData().toString());
		//回显支付金额
		model.addAttribute("payment", orderInfo.getOrders().getPayment());
		//预计送达时间: 3天后送达
		DateTime date = new DateTime();
		DateTime today = date.plusDays(3);
		model.addAttribute("date", today);
		return "success";
	}

}
