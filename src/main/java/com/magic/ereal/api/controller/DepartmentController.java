package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.service.DepartmentService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 部门控制器
 * Created by Eric Xie on 2017/5/5 0005.
 */
@Controller
@RequestMapping("/department")
public class DepartmentController extends BaseController {

    @Resource
    private DepartmentService departmentService;


    /**
     *  查询该分公司下所有的 团队
     * @return
     */
    @RequestMapping("/getDepartment")
    public @ResponseBody ViewData getDepartment(){
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
        Integer companyId = user.getCompanyId();
        if(RoleEnum.C_TEACHER.ordinal() == user.getRoleId()){
            companyId = null;
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                departmentService.queryDepartmentByUser(companyId));
    }



    /**
     *  查询该分公司下所有的 部门
     * @return
     */
    @RequestMapping("/getDepartmentByCompany")
    public @ResponseBody ViewData getAllDepartment(){
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
                departmentService.queryDepartment(user.getId()));
    }

}
