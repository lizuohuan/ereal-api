package com.magic.ereal.api.filter;

import com.magic.ereal.api.config.DESConfig;
import com.magic.ereal.api.config.RSAConfig;
import com.magic.ereal.api.controller.BaseController;
import com.magic.ereal.api.util.AnalysisParam;
import com.magic.ereal.api.util.DESUtil;
import com.magic.ereal.business.util.StatusConstant;
import net.sf.json.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Eric Xie on 2017/3/29 0029.
 */
@WebFilter("/*")
public class APIParamFilter extends BaseController implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {}

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(!DESConfig.getIsEnable()){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        // 请求H5页面 放行
        String url = request.getRequestURI();
        if(url.indexOf("/api/page") != -1){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        if(url.indexOf("updateAPK") != -1){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        String param = request.getParameter(RSAConfig.PARAM_NAME);
        // 验证参数
        if(null == param || param.trim().length() == 0 ){
            servletResponse.getWriter().print(JSONObject.fromObject(buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"Arguments Exception")));
            return;
        }
        try {
            // rsa解密
//            RSAPrivateKey privateKey = RSAEncrypt.loadPrivateKeyByStr(RSAConfig.getPrivateKey());
//            String paramUrl = new String(RSAEncrypt.decrypt(privateKey, Base64.decode(param)),"UTF-8");

            // DES解密
            String paramUrl = DESUtil.decryptDES(param,DESConfig.getKey());
            JSONObject json = AnalysisParam.analysis(paramUrl);
            // 验证 请求时间 5分钟以内 超出5分钟则不能继续请求 时间戳 需要除1000
            long timestamp =  json.getLong("timestamp");
            if(System.currentTimeMillis() / 1000 - timestamp > 5 * 60){
                servletResponse.getWriter().print(JSONObject.fromObject(buildFailureJson(StatusConstant.Fail_CODE,"ERROR REQUEST")));
                return;
            }
            // 验证请求类型 当前为APP 请求类型 其他类型 不能请求
            String type = json.getString("requestType");
            if(!"APP".equals(type)){
                servletResponse.getWriter().print(JSONObject.fromObject(buildFailureJson(StatusConstant.Fail_CODE,"ERROR REQUEST")));
                return;
            }
            // 合法请求
            filterChain.doFilter(AnalysisParam.setParamToRequest(request,json),servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            servletResponse.getWriter().print(JSONObject.fromObject(buildFailureJson(StatusConstant.Fail_CODE,"ERROR PARAMS")));
        }

    }

    public void destroy() {}
}
