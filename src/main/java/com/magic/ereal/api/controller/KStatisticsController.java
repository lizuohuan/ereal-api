package com.magic.ereal.api.controller;

import com.magic.ereal.business.entity.Department;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.exception.InterfaceCommonException;
import com.magic.ereal.business.service.*;
import com.magic.ereal.business.util.DateTimeHelper;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.api.util.ViewData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * K 值统计 -- 控制器
 * @author lzh
 * @create 2017/5/17 15:53
 */
@RestController
@RequestMapping("/kStatistics")
public class KStatisticsController extends BaseController {

    @Resource
    private KStatisticsService kStatisticsService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private StatisticsService statisticsService;
    @Resource
    private UserService userService;
    @Resource
    private UsersStatisticsService usersStatisticsService;
    /**
     * 查询时间段的k值
     * @param time 时间
     * @param userId 用户id
     * @param timeType 查询时间阶段类型 1:周 2：月 3：年
     * @return
     */
    @RequestMapping("/getByTimePersonage")
    public ViewData getByTimePersonage(Long time ,Integer timeType ,Integer userId) {
        try {
            if (null == timeType) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
            }
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user ) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            if (null == userId) {
                userId = user.getId();
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功 ",
                    kStatisticsService.getByTimePersonage(time , userId ,timeType  ,user));
        } catch (InterfaceCommonException e) {
            logger.error(e.getMessage(),e.getErrorCode());
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("服务器超时，获取k统计失败 ",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取k统计失败");
        }
    }

    /**
     * 查询时间段的k值 个人（团队） 统计图（柱状）
     * @param time 时间
     * @param timeType 查询时间阶段类型 1:周 2：月 3：年
     * @return
     */
    @RequestMapping("/getByTimePersonageMap")
    public ViewData getByTimePersonageMap(Long time ,Integer timeType ,Integer userId) {
        try {

            if (null == timeType) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
            }

            User user = (User) LoginHelper.getCurrentUser();
            if (null != userId) {
                user = userService.getUserById(userId);
                if (null == user ) {
                    return buildFailureJson(StatusConstant.USER_DOES_NOT_EXIST,"用户不存在");
                }
            } else {
                if (null == user ) {
                    return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
                }
            }



            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功 ",
                    kStatisticsService.getByTimePersonageMap(time , user.getDepartmentId() ,timeType ,user.getId()));
        } catch (InterfaceCommonException e) {
            logger.error(e.getMessage(),e.getErrorCode());
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error("服务器超时，获取k统计失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取k统计失败");
        }
    }

    /**
     * 首页统计 K值
     * @return
     */
    @RequestMapping("/homeKStatistics")
    public ViewData homeKStatistics() throws ParseException {

        User user = (User) LoginHelper.getCurrentUser();
        if (null == user ) {
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(null == user.getCompanyId()){
            // 查询用户的分公司ID
            Department info = departmentService.info(user.getDepartmentId());
            user.setCompanyId(info.getCompanyId());
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,
                "获取成功",statisticsService.statisticsHomeK(user, new Date()));
    }

    /**
     * 查询时间段的k值 部门 公司
     * @param time 时间
     * @param departmentId 部门id
     * @param companyId 公司id
     * @param type  1:部门 2：公司
     * @param timeType 查询时间阶段类型 1:周 2：月 3：年
     * @return
     */
    @RequestMapping("/getByTimeDeCom")
    public ViewData getByTimeDeCom(Long time ,Integer timeType,Integer type ,Integer departmentId ,Integer companyId){

         User user = (User) LoginHelper.getCurrentUser();
        if (null == user ) {
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if (null == type || null == timeType) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if (null == departmentId) {
            departmentId = user.getDepartmentId();
        }
        if (null == companyId) {
            companyId = user.getCompanyId();
        }
        try {
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,
                    "获取成功",kStatisticsService.getByTimeDeCom(time,departmentId,companyId,timeType,type));
        } catch (Exception e) {
            logger.error("服务器超时，获取失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取失败");
        }


    }



    /**
     * 查询时间段的k值 统计图使用 部门 公司
     * @param time 时间
     * @param departmentId 团队id
     * @param companyId 公司id
     * @param type  1:部门 2：公司
     * @param timeType 查询时间阶段类型 1:周 2：月 3：年
     * @return
     */
    @RequestMapping("/getCarToGramDapCom")
    public ViewData getCarToGramDapCom(Long time ,Integer timeType,Integer type ,
                                                     Integer departmentId ,Integer companyId){

        User user = (User) LoginHelper.getCurrentUser();
        if (null == user ) {
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }

        if (null == type || null == timeType) {
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }

        if (null == departmentId) {
            departmentId = user.getDepartmentId();
        }
        if (null == companyId) {
            companyId = user.getCompanyId();
        }
        try {
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,
                    "获取成功",kStatisticsService.getCarToGramDapCom(time,type,timeType,departmentId,companyId));
        } catch (Exception e) {
            logger.error("服务器超时，获取失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取失败");
        }


    }

    /**
     * 获取三维统计
     * @param userId 用户id
     * @param departmentId 部门id
     * @param companyId 公司id
     * @param time 时间
     * @param type 1：个人  2：部门  3：公司 必传
     * @param timeType 1：周  2： 月 必传
     * @return
     */
    @RequestMapping("/getStatistics")
    public ViewData getStatistics(Integer userId ,Integer departmentId ,Integer companyId,
                                  Long time ,Integer type ,Integer timeType) {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user ) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }

            if (null == type || null == timeType) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
            }

            if (null == userId && null == departmentId && null == companyId) {
                userId = user.getId();

                companyId = user.getCompanyId();
            }
            if(null == companyId){
                companyId = user.getCompanyId();
            }
            if(null == departmentId){
                departmentId = user.getDepartmentId();
            }
            Map<String, Object> statistics = usersStatisticsService.getStatistics(userId, departmentId, companyId, time, type, timeType);
            Set<String> keys = statistics.keySet();
            if(keys.size() == 0){
                return buildFailureJson(StatusConstant.Fail_CODE,"暂无数据");
            }
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,
                    "获取成功", statistics);
        } catch (Exception e) {
            logger.error("服务器超时，获取失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取失败");
        }
    }




}
