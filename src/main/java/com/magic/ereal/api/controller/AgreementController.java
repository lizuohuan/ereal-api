package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Agreement;
import com.magic.ereal.business.service.AgreementService;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by Eric Xie on 2017/5/24 0024.
 */
@Controller
@RequestMapping("/agreement")
public class AgreementController extends BaseController {

    @Resource
    private AgreementService agreementService;


    @RequestMapping("/getAgreement")
    public @ResponseBody ViewData getAgreement(){
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                agreementService.queryAgreement());
    }

}
