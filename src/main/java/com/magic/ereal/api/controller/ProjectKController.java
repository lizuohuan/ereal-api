package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.exception.InterfaceCommonException;
import com.magic.ereal.business.service.ProjectKService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 外部项目 内部结项 & 外部结项   K值比例分配 -- 控制器
 * @author lzh
 * @create 2017/5/9 18:33
 */
@RestController
@RequestMapping("/projectK")
public class ProjectKController extends BaseController {

    @Resource
    private ProjectKService projectKService;

    /**
     * 批量新增 分配结果
     * @param projectKs json
     * @return
     */
    @RequestMapping("/save")
    public ViewData save(String projectKs) {
        try {
            if (null == projectKs) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }
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
            projectKService.batchAddProjectK(projectKs,user.getId());
            return buildFailureJson(StatusConstant.SUCCESS_CODE,"分配成功" );
        } catch (InterfaceCommonException e) {
            logger.error(e.getMessage(),e.getErrorCode());
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("服务器超时，分配失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，分配失败");
        }
    }

    /**
     * 获取外部项目 k值分配 和 耗时
     * @param projectRecordId 记录ID
     * @param type 0：内部  1：外部
     * @return
     */
    @RequestMapping("/getKAndUser")
    public ViewData getKAndUser(Integer projectRecordId ,Integer type) {
        try {
            if (null == projectRecordId || null == type) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectKService.getKAndUser(projectRecordId,type));
        } catch (InterfaceCommonException e) {
            logger.error(e.getMessage(),e.getErrorCode());
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("获取失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取失败");
        }
    }

}
