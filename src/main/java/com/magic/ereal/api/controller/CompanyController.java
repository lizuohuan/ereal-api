package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.service.CompanyService;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by Eric Xie on 2017/5/15 0015.
 */
@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    @Resource
    private CompanyService companyService;


    @RequestMapping("/getCompanyList")
    public @ResponseBody ViewData getCompanyList(){
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                companyService.queryAllCompany());
    }



    @RequestMapping("/queryBaseCompany")
    public @ResponseBody ViewData queryBaseCompany(){
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                companyService.queryBaseCompany());
    }






}
