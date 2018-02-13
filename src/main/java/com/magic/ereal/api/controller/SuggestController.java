package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Suggest;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.service.SuggestService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by Eric Xie on 2017/5/10 0010.
 */
@Controller
@RequestMapping("/suggest")
public class SuggestController extends BaseController {

    @Resource
    private SuggestService suggestService;

    /**
     * 修改密码 接口
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @RequestMapping("/add")
    public @ResponseBody ViewData addSuggest(Suggest suggest) {
        Object obj = LoginHelper.getCurrentUser();
        if (null == obj) {
            return buildFailureJson(StatusConstant.NOTLOGIN, "未登录");
        }
        if (!(obj instanceof User)) {
            return buildFailureJson(StatusConstant.NOT_AGREE, "没有权限");
        }
        User user = (User) obj;
        if (StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())) {
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN, "已离职，帐号不可能用");
        }
        if (CommonUtil.isEmpty(suggest.getContent())) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL, "字段不能为空");
        }
        try {
            suggest.setUserId(user.getId());
            suggestService.addSuggest(suggest);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return buildFailureJson(StatusConstant.Fail_CODE, "提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE, "提交成功");

    }


}
