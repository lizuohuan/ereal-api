package com.magic.ereal.api.interceptor;

import com.magic.ereal.business.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 用户登录拦截
 *
 */
@Component
public class UserCookieLogin implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(UserCookieLogin.class);

	@Autowired
	private UserService userService;

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();

		if (requestURI.startsWith("/api")) {

			//web和app用户
//			User userSession = (User)request.getSession().getAttribute(Constant.SESSION_USER);
//			if(userSession == null){
//				String userId = null;
//				Cookie[] cookies = request.getCookies();
//				if(cookies !=null && cookies.length > 0){
//					
//					for(Cookie cookie : cookies){
//						if(Constant.USER_COOKIE_ID.equals(cookie.getName())){
//							userId = cookie.getValue();
//							break;
//						}
//					}
//					
//					if(userId != null && !"".equals(userId)){
//						System.out.println("userId:" + userId);
//						
//						//解密
//						userId = SecretUtils.decryptMode(SecretUtils.getKeyByte(Constant.DES_KEY), SecretUtils.hex2byte(userId.getBytes()));
//						
//						User user = userService.getUserById(userId);
//						
//						if (user != null) {
//							userSession = user;
//							
//							//判断用户是否用cookie登录成功,如果成功 则放到session里,不成功清除cookie
//							request.getSession().setAttribute(Constant.SESSION_USER, userSession);
//						}
//						else {
//							logger.error("登录失败:" + userId);
//						}
//					}
//				}
//				
//				
//				if (userSession == null) {
//					FunctionData.clearCookie(request, response, Constant.USER_COOKIE_ID);
//				}
//				else {
//					//加密
//					Cookie cookie = new Cookie(Constant.USER_COOKIE_ID, SecretUtils.encryptMode(Constant.DES_KEY, String.valueOf(userSession.getId())));
//			        cookie.setPath("/");
//					response.addCookie(cookie);
//				}
//			}
		}

		return true;
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}





