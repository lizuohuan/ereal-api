package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Banner;
import com.magic.ereal.business.entity.PageArgs;
import com.magic.ereal.business.entity.PageList;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.service.BannerService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * banner消息 -- 控制器
 * @author lzh
 * @create 2017/5/24 16:58
 */
@RestController
@RequestMapping("/banner")
public class BannerController extends BaseController {


    @Resource
    private BannerService bannerService;


    /**
     * 通过 类型获取 banner列表
     * @param type 除 三维数据外的 banner
     */
    @RequestMapping("/queryBannerByType")
    public ViewData queryBannerByType(Integer type){
        if(null == type){
            buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(type == 2){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                bannerService.queryBannerByType_(type,((User)LoginHelper.getCurrentUser()).getCompanyId()));
    }

    /**
     * app端首页banner轮播
     * @return
     */
    @RequestMapping("/listForApp")
    public ViewData listForApp() {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",bannerService.listForApp(user.getCompanyId()));
        } catch (Exception e) {
            logger.error("服务器超时，获取banner详情失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取banner详情失败");
        }
    }

    /**
     * 根据三维类型 获取列表 app端
     * @param dimensionType  0：一维  1:二维 2：三维
     * @param pageNO
     * @param pageSize
     * @return
     */
    @RequestMapping("/getByDimensionType")
    public ViewData getByDimensionType( Integer dimensionType, Integer pageNO,Integer pageSize) {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            } else {
                if (!user.getRoleId().equals(RoleEnum.GENERAL_MANAGER_ON_DUTY.ordinal())){
                    return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
                }
            }
            if (null == dimensionType) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",bannerService.
                    getByDimensionType(dimensionType, pageNO, pageSize));
        } catch (Exception e) {
            logger.error("服务器超时，获取banner详情失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取失败");
        }
    }

    /**
     * 三维审核 更改状态
     * @param status
     * @param id
     * @return
     */
    @RequestMapping("/updateStatus")
    public ViewData updateStatus(Integer status ,Integer id) {
        try {
            if (null == status || null == id) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
            }
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            Banner info = bannerService.info(id);
            if(null == info){
                return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"消息不存在");
            }
            if(status == 1 || status == 2){
                // 审核通过
                if(info.getStatus() != 0){
                    return buildFailureJson(StatusConstant.Fail_CODE,"消息状态已被修改,请刷新操作");
                }
            }
            Banner banner = new Banner();
            banner.setId(id);
            banner.setStatus(status);
            if (status == 1) {
                banner.setIsShow(1);
            }
            banner.setAuditUserId(user.getId());
            bannerService.update(banner);
            return buildFailureJson(StatusConstant.SUCCESS_CODE,"操作成功");
        } catch (Exception e) {
            logger.error("服务器超时，更新banner信息失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，更新banner信息失败");
        }
    }


    /**
     * 详情
     * @param id
     * @return
     */
    @RequestMapping("/info")
    public ViewData info(Integer id,Integer source) {
        try {
            if (null == id || null == source) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",bannerService.infoApp(id,source));
        } catch (Exception e) {
            logger.error("服务器超时，获取banner详情失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取banner详情失败");
        }
    }





}
