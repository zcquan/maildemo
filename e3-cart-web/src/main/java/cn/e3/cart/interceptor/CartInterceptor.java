package cn.e3.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.cart.utils.CookieUtils;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
/**
 * 判断用户是否处于登录状态
 * 1,cookie中存在token
 * 2,redis用户身份信息没有过期
 * @author Administrator
 * 特点:
 * 用户未登录,登录都可以添加购物车.
 * 用户未登录,登录都必须放行.
 */
public class CartInterceptor implements HandlerInterceptor{

	//注入用户登录保存在cookie中身份信息唯一标识
	@Value("${E3_TOKEN}")
	private String E3_TOKEN;
	
	//注入单点登录服务
	@Autowired
	private UserService userService;
	
	//return false: 拦截  return true:放行
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 从cookie获取用户登录身份信息唯一标识token
		String token = CookieUtils.getCookieValue(request, E3_TOKEN, true);
		// 判断token是否存在
		if(StringUtils.isNotBlank(token)){
			//如果token存在
			//根据token查询redis中用户身份信息,判断用户redis中身份信息是否存在
			E3mallResult result = userService.findUserByToken(token);
			//判断用户redis中身份信息是否存在
			if(result.getStatus()==200){
				//表示用户身份信息存在
				//把用户身份放入request
				request.setAttribute("user", result.getData());
			}
		}
		//用户身份信息不存在
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
