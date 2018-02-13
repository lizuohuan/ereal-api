package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.service.ProjectTypeService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 课题类型 控制器
 * Created by Eric Xie on 2017/5/5 0005.
 */
@Controller
@RequestMapping("/projectType")
public class ProjectTypeController extends BaseController {


    @Resource
    private ProjectTypeService projectTypeService;



    /**
     *  查询 外部项目下 符合条件的所有的课题类型
     * @return
     */
    @RequestMapping("/getProjectType")
    public @ResponseBody ViewData getProjectType(){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"帐号无效");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectTypeService.queryAllType());
    }

}
