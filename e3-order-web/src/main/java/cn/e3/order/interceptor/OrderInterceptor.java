package cn.e3.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.user.UserSessionRegistry;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.order.utils.CookieUtils;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
/**
 * 需求:订单提交去到订单结算页面之前用户必须处于登录状态
 * @author Administrator
 * 判断用户是否登录:
 * 1,cookie中token必须存在
 * 2,redis中用户身份信息必须存在
 * 结算页面流程:
 * 1,从cookie中获取用户身份信息唯一标识token
 * 2,如果token不存在,说明(1,cookie被清空,2,用户没有登录)
 * 3,跳转到登录页面去登录
 * 4,登录成功后,回到历史操作页面(不是回到首页)
 * 5,如果token存在,根据token查询redis服务器用户身份信息
 * 6,如果redis用户身份信息不存在
 * 7,跳转到登录页面去登录
 * 8,登录成功后,回到历史操作页面(不是回到首页)
 * 9,直接跳转订单结算页面即可
 * 
 *
 */
public class OrderInterceptor implements HandlerInterceptor{
	
	//注入cookie中用户身份信息唯一标识
	@Value("${E3_TOKEN}")
	private String E3_TOKEN;
	
	
	//注入单点登录服务器地址
	@Value("${SSO_URL}")
	private String SSO_URL;

	//注入单点登录服务对象
	@Autowired
	private UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 1,从cookie中获取用户身份信息唯一标识token
		String token = CookieUtils.getCookieValue(request, E3_TOKEN, true);
		// 2,判断token是否存在
		if(StringUtils.isBlank(token)){
			//3,如果token不存在,说明(1,cookie被清空,2,用户没有登录)
			
			//跳转之前获取历史操作页面地址
			//通过request对象获取历史操作地址
			String historyURL = request.getRequestURL().toString();
			
			//跳转到登录页面去登录
			//4,登录成功后,回到历史操作页面(不是回到首页)
			//SSO_URL=http://localhost:8088/page/login?h=url
			response.sendRedirect(SSO_URL+"?h="+historyURL);
			
			//拦截
			return false;
		}
		
		//5,如果token存在,根据token查询redis服务器用户身份信息
		E3mallResult result = userService.findUserByToken(token);
		//6,判断用户身份信息是否存在
		if(result.getStatus()!=200){
			//如果redis用户身份信息不存在
			//跳转之前获取历史操作页面地址
			//通过request对象获取历史操作地址
			String historyURL = request.getRequestURL().toString();
			
			//跳转到登录页面去登录
			//登录成功后,回到历史操作页面(不是回到首页)
			//SSO_URL=http://localhost:8088/page/login?h=url
			response.sendRedirect(SSO_URL+"?h="+historyURL);
			
			//拦截
			return false;
		}
		
		//把用户数据放入request
		request.setAttribute("user", result.getData());
		
		//登录状态
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
