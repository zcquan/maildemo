package cn.e3.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.cart.service.CartService;
import cn.e3.cart.utils.CookieUtils;
import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbUser;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;

@Controller
public class CartController {
	
	//注入cookie购物车唯一标识
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	
	//注入购物车服务对象
	@Autowired
	private CartService cartService;
	
	
	//注入商品服务对象
	@Autowired
	private ItemService itemService;
	
	//注入cookie购物车过期时间
	@Value("${COOKIE_CART_EXPIRE_TIME}")
	private Integer COOKIE_CART_EXPIRE_TIME;

	/**
	 * 需求:添加购物车
	 * 请求:http://localhost:8090/cart/add/${item.id}.html?num="+$("#buy-num").val();
	 * 参数:Long itemId,Integer num
	 * 返回值:cartSuccess
	 * 业务:
	 * 判断用户是否处于登录状态(初始化)
	 * 1,登录(redis购物车)
	 * > 判断redis购物中是否存在相同商品
	 * > 如果存在,商品数量相加
	 * > 如果不存在,直接添加到redis购物车即可
	 * 2,未登录(cookie购物车)
	 * > 先获取cookie购物车列表
	 * > 判断cookie购物车是否存在相同商品
	 * > 存在,商品数量相加
	 * > 否则,直接添加即可
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,
			Integer num,
			HttpServletRequest request,
			HttpServletResponse response){
		//判断用户是否处于登录状态(初始化)
		//从request获取用户数据,如果用户不为空,表示用户处于登录状态
		TbUser user = (TbUser) request.getAttribute("user");
		//判断用户是否为空
		if(user!=null){
			//登录状态
			E3mallResult result = cartService.addRedisCart(user.getId(),itemId,num);			
			
			return "cartSuccess";	
		}
		
		//未登录
		//获取cookie购物车列表数据
		List<TbItem> cartList = this.getCookieCartList(request);
		
		//定义一个标识,判断是否存在相同商品
		boolean flag = false;
		
		//判断cookie购物车中是否存在相同商品
		for (TbItem tbItem : cartList) {
			//如果添加的商品和cookie购物车列表中某个商品id相等,说明cookie购物车存在相同商品
			if(tbItem.getId()==itemId.longValue()){
				//存在相同商品,数量相加
				tbItem.setNum(tbItem.getNum()+num);
				
				flag = true;
				
				//跳出
				break;
			}
			
		}
		
		//否则没有相同商品
		if(!flag){
			//购买(查询数据库商品,添加到购物车)
			TbItem item = itemService.findItemByID(itemId);
			//设置购买数量
			item.setNum(num);
			
			//添加到购物车列表
			cartList.add(item);
		}
		
		//把购物车列表写回cookie购物车
		CookieUtils.setCookie(request,
				response, 
				COOKIE_CART, 
				JsonUtils.objectToJson(cartList), 
				COOKIE_CART_EXPIRE_TIME, 
				true);
		
		return "cartSuccess";
	}
	
	
	/**
	 * 需求:结算页面
	 * 请求:/cart/cart.html
	 * 返回值:cart.jsp
	 * 业务:
	 * 判断用户是否处于登录状态(初始化)
	 * 1,登录(查询redis购物车)
	 * > 如果cookie购物车存在值,合并购物车(把cookie合并到redis购物车)
	 * > 查询redis购物车
	 * 2,未登录(查询cookie)
	 * > 查询cookie购物车列表
	 * 
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,HttpServletResponse response){
		
		//判断用户是否处于登录状态(初始化)
		//从request中获取用户登录数据
		TbUser user = (TbUser) request.getAttribute("user");
		
		//获取cookie购物车列表
		List<TbItem> cartList = this.getCookieCartList(request);
		
		//判断用户是否处于登录
		if(user!=null){
			//登录
			//判断cookie购物车是否有数据
			if(!cartList.isEmpty()){
				//cookie购物车数据不为空,合并购物车,把cookie购物车数据合并到redis购物车
				E3mallResult result = cartService.mergeRedisCart(user.getId(),cartList);
				//清空cookie购物车
				CookieUtils.setCookie(request, response, COOKIE_CART, "", 0, true);
			}
			
			//查询redis购物车,去到购物车结算页面
			cartList = cartService.findRedisSortedCart(user.getId());
		}
		
		//把购物车列表放入request
		request.setAttribute("cartList", cartList);
		
		return "cart";
		
	}
	
	
	/**
	 * 需求:删除购物车数据
	 * 请求:/cart/delete/{itemId}
	 * 参数:Long itemId
	 * 返回值:redirect:/cart/cart.html
	 * 业务:
	 * 1,登录(删除redis购物车)
	 * 2,未登录(删除cookie购物车)
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCart(@PathVariable Long itemId,
			HttpServletResponse response,
			HttpServletRequest request){
		//判断用户是否处于登录状态(初始化)
		//从request中获取用户登录数据
		TbUser user = (TbUser) request.getAttribute("user");
		
		//判断用户是否处于登录
		if(user!=null){
			//用户处于登录状态
			E3mallResult result = cartService.deleteRedisCart(user.getId(),itemId);
			
			
			return "redirect:/cart/cart.html";
		}
		
		//未登录
		//首先获取cookie购物车列表
		List<TbItem> cartList = this.getCookieCartList(request);
		//判断删除的是那个商品
		for (TbItem tbItem : cartList) {
			//如果需求删除商品id和cookie购物车集合中商品id相等,表示此商品需要被删除
			if(tbItem.getId()==itemId.longValue()){
				cartList.remove(tbItem);
				break;
			}
			
		}
		//写回cookie购物车
		CookieUtils.setCookie(request,
				response, 
				COOKIE_CART, 
				JsonUtils.objectToJson(cartList), 
				COOKIE_CART_EXPIRE_TIME, 
				true);
		
		return "redirect:/cart/cart.html";
	}
	
	
	
	
	/**
	 * 需求:更改购物车商品购买数量,价格必须随之而改变
	 * 请求:/cart/update/num/{itemId}/{num}
	 * 参数:Long itemId,Integer num
	 * 返回值:E3mallResult
	 * 业务:
	 * 1,登录 (修改redis购物车)
	 * 2,未登录(修改是cookie购物车)
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3mallResult updateCart(@PathVariable Long itemId,
			@PathVariable Integer num,
			HttpServletRequest request,
			HttpServletResponse response){
		//从request获取用户身份信息
		TbUser user = (TbUser) request.getAttribute("user");
		//判断用户是否登录状态
		if(user!=null){
			//此时用户处于登录状态
			E3mallResult result = cartService.updateRedisCart(user.getId(),itemId,num);
			
			return result;
		}
		
		//未登录
		//获取购物车列表数据
		List<TbItem> cartList = this.getCookieCartList(request);
		//判断购物车列表中修改那个商品
		for (TbItem tbItem : cartList) {
			//如果待修改的商品id和购物车列表中商品id相等,表示此商品数量需要被修改
			if(tbItem.getId()==itemId.longValue()){
				tbItem.setNum(num);
				break;
			}
			
		}
		
		//写回cookie购物车
		CookieUtils.setCookie(request,
				response, 
				COOKIE_CART, 
				JsonUtils.objectToJson(cartList),
				COOKIE_CART_EXPIRE_TIME,
				true);
		
		return E3mallResult.ok();
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

}
