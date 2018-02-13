package com.magic.ereal.api.controller;

import com.magic.ereal.api.config.DESConfig;
import com.magic.ereal.api.util.DESUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.util.StatusConstant;
import org.slf4j.LoggerFactory;


/**
 * Created by flyong86 on 2016/5/4.
 */
public class BaseController {

    protected org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    protected ViewData buildViewData(ViewData.FlagEnum flag, int code, String message, Object data) {
        ViewData viewData = new ViewData();
        viewData.setFlag(flag.ordinal());
        viewData.setCode(Integer.valueOf(code));
        viewData.setMsg(message);
        viewData.setData(data);
        return viewData;
    }


    protected ViewData buildSuccessJson(int code, String msg, Object data) {

//        if(null != data && DESConfig.getIsEnable()){
//            // 需要加密返回参数
//            try {
//                data = DESUtil.encryptDES(data.toString(),DESConfig.getKey());
//            } catch (Exception e) {
//                logger.error("加密失败");
//                return buildFailureJson(StatusConstant.Fail_CODE,"加密失败");
//            }
//        }

        return buildViewData(ViewData.FlagEnum.NORMAL, code, msg, data);
    }

    public ViewData buildSuccessCodeJson(int code, String msg) {
        return buildSuccessJson(code, msg, (Object)null);
    }

    protected ViewData buildSuccessViewData(int code, String msg, Object data) {
        return buildViewData(ViewData.FlagEnum.NORMAL, code, msg, data);
    }

    protected ViewData buildSuccessCodeViewData(int code, String msg) {
        return buildSuccessViewData(code, msg, (Object) null);
    }

    protected ViewData buildFailureJson(ViewData.FlagEnum flag, int code, String msg) {
        return buildViewData(flag, code, msg, (Object)null);
    }
    protected ViewData buildFailureJson(int code, String msg) {
        return buildViewData(ViewData.FlagEnum.NORMAL, code, msg, (Object)null);
    }

    protected ViewData buildFailureMessage(String msg) {
        return buildFailureJson(202, msg);
    }



    
    
}
