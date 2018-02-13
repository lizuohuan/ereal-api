package com.magic.ereal.api.controller;


import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.*;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.enums.SystemInfoEnum;
import com.magic.ereal.business.exception.InterfaceCommonException;
import com.magic.ereal.business.push.PushMessageUtil;
import com.magic.ereal.business.service.*;
import com.magic.ereal.business.util.DateTimeHelper;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.business.util.TextMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 *
 * 内部项目 控制器
 * Created by Eric Xie on 2017/5/2 0002.
 */
@Controller
@RequestMapping("/projectInterior")
public class ProjectInteriorController extends BaseController{

    @Resource
    private ProjectInteriorService projectInteriorService;
    @Resource
    private ProjectInteriorWeekAcceptanceService projectInteriorWeekAcceptanceService;
    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;
    @Resource
    private ProjectMajorService projectMajorService;




    /**
     * K值比例分配 提交
     */
    @RequestMapping("/allocationRatio")
    public @ResponseBody ViewData allocationRatio(Integer weekId,String kAllocationJsonArr){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(weekId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
//        if(((User) obj).getRoleId() != RoleEnum.PROJECT_MANAGER.ordinal()){
//            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
//        }
        if(null == weekId || CommonUtil.isEmpty(kAllocationJsonArr)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        try {

//            if(kAllocationJsonArr.indexOf("\""));
//            kAllocationJsonArr = kAllocationJsonArr.substring(1,kAllocationJsonArr.length() - 1);
            JSONArray jsonArray = JSONArray.fromObject(kAllocationJsonArr);
            List<ProjectInteriorWeekKAllocation> allocations = new ArrayList<>();
            // 封装对象
            for (Object arr : jsonArray){
                ProjectInteriorWeekKAllocation allocation = new ProjectInteriorWeekKAllocation();
                JSONObject jsonObject = JSONObject.fromObject(arr);
                allocation.setUserId(jsonObject.getInt("userId"));
                allocation.setRatio(jsonObject.getDouble("ratio"));
                allocation.setWeekId(weekId);
                allocations.add(allocation);
            }
            if(allocations.size() > 0){
                projectInteriorWeekAcceptanceService.allocationRatio(allocations);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }

    /**
     * 获取待分配 K值的 周验收 基础数据 以及员工工时列表
     * @param weekId 内部项目
     * @return
     */
    @RequestMapping("/getAllocationWeekData")
    public @ResponseBody ViewData getWeekData(Integer weekId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(weekId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
//        if(((User) obj).getRoleId() != RoleEnum.PROJECT_MANAGER.ordinal()){
//            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
//        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectInteriorWeekAcceptanceService.queryAcceptanceIncludeUserH(weekId));
    }



    /**
     * 批准内部项目 周验收
     * @param acceptance 内部项目
     * @param isFinish 是否结项 0 否  1 是
     * @return
     */
    @RequestMapping("/approvedWeek")
    public @ResponseBody ViewData approvedWeek(ProjectInteriorWeekAcceptance acceptance,Integer isFinish){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(acceptance.getId())){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(null == acceptance.getProgress()){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        ProjectInterior projectInterior = projectInteriorService.queryBaseInfo(acceptance.getProjectInteriorId());
        if(null == projectInterior){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"项目不存在");
        }

        ProjectInteriorWeekAcceptance weekAcceptance = projectInteriorWeekAcceptanceService.queryAcceptanceById(acceptance.getId());
        if(null == weekAcceptance){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"周数据不存在");
        }
        if(weekAcceptance.getStatus() != 0 ){
            return buildFailureJson(StatusConstant.Fail_CODE,"已经验收过");
        }
        try {
            projectInteriorWeekAcceptanceService.approvedProjectInteriorWeek(acceptance,isFinish);

            User user = userService.queryUserDeviceTypeAndToken(projectInterior.getAllocationUserId()).get(0);

            //推送
            SystemInfo info = new SystemInfo();
            info.setTitle(TextMessage.PROJECT_INTERIOR_APPROVED_WEEK_TITLE);
            info.setContent(MessageFormat.format(TextMessage.PROJEC_INTERIORT_APPROVED_WEEK_CONTENT,projectInterior.getShortName()));
            info.setUserId(user.getId());
            info.setType(SystemInfoEnum.PROJECT_INTERIOR_INFO.ordinal());
            systemInfoService.addSystemInfo(info);
            Map<String,String> extendsParams = new HashMap<>();
            extendsParams.put("type",SystemInfoEnum.PROJECT_INFO.ordinal()+"");
            PushMessageUtil.pushMessages(user,info.getTitle(),extendsParams);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"申请失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }


    /**
     * 申请内部项目 周验收
     * @param projectInteriorId 内部项目 ID
     * @return
     */
    @RequestMapping("/applicationWeek")
    public @ResponseBody ViewData applicationWeek(Integer projectInteriorId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(projectInteriorId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        ProjectInterior projectInterior = projectInteriorService.queryBaseInfo(projectInteriorId);
        if(null == projectInterior){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"项目不存在");
        }
        try {
            ProjectInteriorWeekAcceptance acceptance = new ProjectInteriorWeekAcceptance();
            acceptance.setStatus(0);
            acceptance.setProjectInteriorId(projectInteriorId);
            projectInteriorWeekAcceptanceService.addProjectInteriorWeekAcceptance(acceptance);

            User user = userService.queryUserDeviceTypeAndToken(projectInterior.getDirectReportPersonUserId()).get(0);

            //推送
            SystemInfo info = new SystemInfo();
            info.setTitle(TextMessage.PROJECT_INTERIOR_APPLICATION_WEEK_TITLE);
            info.setContent(MessageFormat.format(TextMessage.PROJECT_INTERIOR_APPLICATION_WEEK_CONTENT,projectInterior.getShortName()));
            info.setUserId(user.getId());
            info.setType(SystemInfoEnum.PROJECT_INTERIOR_INFO.ordinal());
            systemInfoService.addSystemInfo(info);
            Map<String,String> extendsParams = new HashMap<>();
            extendsParams.put("type",SystemInfoEnum.PROJECT_INFO.ordinal()+"");
            PushMessageUtil.pushMessages(user,info.getTitle(),extendsParams);
        } catch (InterfaceCommonException e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"申请失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }



    /**
     * 获取内部项目 详情
     * @param projectInteriorId 内部项目 ID
     * @return
     */
    @RequestMapping("/getProjectInteriorById")
    public @ResponseBody ViewData getProjectInteriorById(Integer projectInteriorId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(projectInteriorId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectInteriorService.queryProjectInteriorById(projectInteriorId));
    }


    /**
     *
     * @param pageNO
     * @param pageSize
     * @param status
     * @param projectMajorId 类别（全部，XX专业）
     * @param departmentId 团队（全部，XX团队）
     * @param isTerminate 状态（全部，进行中，已终止） 是否终止  0 进行中  1 已终止
     * @param sortType 0:默认排序 1:项目已获总K 排序  2：项目进度 排序 3：当周新增K值 排序 4：当月新增K值 排序 5：项目效率 排序
     * @return
     */
    @RequestMapping("/getProjectInterior")
    public @ResponseBody ViewData getProjectInterior(Integer pageNO, Integer pageSize ,Integer status,
                                                     Integer projectMajorId,Integer departmentId,Integer isTerminate,Integer sortType){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        User user = (User)obj;
        Map<String,Object> map = new HashMap<>();
        map.put("projectMajorId",projectMajorId);
        map.put("departmentId",departmentId);
        map.put("isTerminate",isTerminate);
        map.put("sortType",sortType);
        map.put("status",status);
        map.put("userId",user.getId());
        map.put("roleId",user.getRoleId());
        map.put("limit",(pageNO - 1) * pageSize);
        map.put("limitSize",pageSize);

        if (sortType == 3) {
            //获取这个时间的这周星期一的日期 开始时间
            Date startWeekTime = DateTimeHelper.getWeekByDate(new Date(), 1);
            //获取这个时间的这周星期日的日期 结束时间
            Date endWeekTime = DateTimeHelper.getWeekByDate(new Date(), 7);
            startWeekTime = DateTimeHelper.setDateField(startWeekTime, 0, 0, 0);
            endWeekTime = DateTimeHelper.setDateField(endWeekTime, 23, 59, 59);
            map.put("startTime",startWeekTime);
            map.put("endTime",endWeekTime);
        }
        if (sortType == 4) {
            //获取这个时间的这月月初的日期 开始时间
            Date startMonthTime = DateTimeHelper.getMonthByDate(new Date(), "first");
            //获取这个时间的这月月末的日期 结束时间
            Date endMonthTime = DateTimeHelper.getMonthByDate(new Date(), "last");
            startMonthTime = DateTimeHelper.setDateField(startMonthTime, 0, 0, 0);
            endMonthTime = DateTimeHelper.setDateField(endMonthTime, 23, 59, 59);
            map.put("startTime",startMonthTime);
            map.put("endTime",endMonthTime);

        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectInteriorService.queryProjectInteriorByItem(map));
    }


    /**
     * 获取所有的项目专业集合
     * @return
     */
    @RequestMapping("/getAllProjectMajor")
    public @ResponseBody  ViewData getAllProjectMajor(){
        return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                projectMajorService.list());
    }


    /**
     * 传递卡 内部项目筛选
     * @return
     */
    @RequestMapping("/getWorkDiaryProInterior")
    public ViewData getWorkDiaryProInterior() {
        try {
            Object obj = LoginHelper.getCurrentUser();
            if(null == obj){
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            if(!(obj instanceof User)){
                return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
            }
            User user = (User)obj;
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectInteriorService.getWorkDiaryProInterior(user.getId()));
        } catch (Exception e) {
            logger.error("服务器失败，获取内部项目列表失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器失败，获取内部项目列表失败");
        }
    }



}
