package com.magic.ereal.api.util;

import com.magic.ereal.api.filter.ParameterRequestWrapper;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by Eric Xie on 2017/1/11 0011.
 *
 *
 * 解析请求参数 工具
 */
public class AnalysisParam {


    /**
     *  拆分 参数
     * @param paramStr user=admin&pwd=abc
     * @return JSONObject 对象
     */
    public static JSONObject analysis(String paramStr) throws Exception{
        JSONObject jsonObject = new JSONObject();
        if(null == paramStr || paramStr.trim().length() == 0){
            return jsonObject;
        }
        String[] strs = paramStr.split("&");
        for (int i = 0; i < strs.length; i++){
            String[] params = strs[i].split("=");
            jsonObject.put(params[0],params[1]);
        }
        return jsonObject;
    }


    public static HttpServletRequest setParamToRequest(HttpServletRequest request,JSONObject jsonObject){
        HashMap map = new HashMap(request.getParameterMap());
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()){
            String key = iterator.next().toString();
            Object value = jsonObject.get(key);
            map.put(key,value);
        }
        ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(request,map);
        return parameterRequestWrapper;
    }

}
