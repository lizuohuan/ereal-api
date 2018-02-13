package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.service.CompanyService;
import com.magic.ereal.business.service.DepartmentService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 联系人 控制器
 * Created by Eric Xie on 2017/5/3 0003.
 */
@Controller
@RequestMapping("/contacts")
public class ContactsController extends BaseController {

    @Resource
    private DepartmentService departmentService;
    @Resource
    private CompanyService companyService;

    @RequestMapping("/getContacts")
    public @ResponseBody ViewData getContacts(Integer companyId,Integer accreditType){
        if(null == companyId){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        User obj = (User)LoginHelper.getCurrentUser();
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                companyService.queryCompanyById(companyId,accreditType,null == obj ? null : obj.getId()));
    }

}
