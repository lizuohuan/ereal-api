package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.service.SystemInfoService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 系统消息 控制器
 * Created by Eric Xie on 2017/5/10 0010.
 */
@Controller
@RequestMapping("/systemInfo")
public class SystemInfoController extends BaseController {

    @Resource
    private SystemInfoService systemInfoService;




    /**
     *  根据消息类型 获取 消息列表
     * @return
     */
    @RequestMapping("/queryInfoByType")
    public @ResponseBody ViewData queryInfoByType(Integer type,Integer pageNO,Integer pageSize){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"已离职，帐号不可能用");
        }
        if(CommonUtil.isEmpty(type,pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                systemInfoService.queryInfoByType(user.getId(),type,pageNO,pageSize));
    }



    /**
     *  获取各个消息类型 最新一条数据
     * @return
     */
    @RequestMapping("/getNewInfo")
    public @ResponseBody ViewData getNewInfo(){

        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"已离职，帐号不可能用");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                systemInfoService.queryNewInfoByType(user.getId()));
    }

}
