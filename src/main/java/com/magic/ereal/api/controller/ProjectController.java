package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.*;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.enums.SystemInfoEnum;
import com.magic.ereal.business.push.PushMessageUtil;
import com.magic.ereal.business.service.*;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.business.util.TextMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外部项目 控制器
 * Created by Eric Xie on 2017/4/28 0028.
 */
@RequestMapping("/project")
@Controller
public class ProjectController extends BaseController {

    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectAcceptanceRecordService projectAcceptanceRecordService;
    @Resource
    private ProjectWeekAcceptanceService projectWeekAcceptanceService;
    @Resource
    private ProjectWeekKAllocationService projectWeekKAllocationService;
    @Resource
    private UserService userService;
    @Resource
    private ProjectTypeSectionService projectTypeSectionService;
    @Resource
    private SystemInfoService systemInfoService;





    /**
     * 通过项目ID  查询参与项目的课题类型的阶段 集合
     * @param projectId 项目ID
     * @return
     */
    @RequestMapping("/getProjectType")
    public @ResponseBody ViewData getProjectType(Integer projectId){
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
        if(CommonUtil.isEmpty(projectId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectTypeSectionService.getByProjectId(projectId));
    }



    /**
     * 通过项目ID  查询参与项目的用户集合
     * @param projectId 项目ID
     * @return
     */
    @RequestMapping("/getProjectUser")
    public @ResponseBody ViewData getProjectUser(Integer projectId){
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
        if(CommonUtil.isEmpty(projectId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                userService.queryWeekUserHByProject(projectId));
    }


    /**
     *  外部项目 分配周验收阶段比例
     * @param ratioJsonArr
     */
    @RequestMapping("/allocationRatio")
    public @ResponseBody ViewData allocationRatio(String ratioJsonArr,Integer weekId){
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
//        if(RoleEnum.PROJECT_MANAGER.ordinal() != user.getRoleId()){
//            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
//        }
        if(CommonUtil.isEmpty(weekId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(CommonUtil.isEmpty(ratioJsonArr)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        ProjectWeekAcceptance weekAcceptance = projectWeekAcceptanceService.queryProjectWeekAcceptanceById(weekId);
        if(null == weekAcceptance){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
        }
        List<ProjectWeekKAllocation> list = projectWeekKAllocationService.queryKAllocation(weekId);
        if(null != list && list.size() > 0){
            return buildFailureJson(StatusConstant.Fail_CODE,"已经分配过K值");
        }
//        ProjectWeekAcceptance pre = projectWeekAcceptanceService.queryProjectPreWeek(weekAcceptance.getProjectId());
        try {

            // id:用户Id、ratio:分配的比例、k:阶段K值 sectionId:阶段ID
            JSONArray jsonArray = JSONArray.fromObject(ratioJsonArr);
            List<ProjectWeekKAllocation> allocations = new ArrayList<>();
            for (Object arr : jsonArray){
                ProjectWeekKAllocation allocation = new ProjectWeekKAllocation();
                JSONObject json = JSONObject.fromObject(arr);
                allocation.setCreateUserId(user.getId());
                allocation.setUserId(json.getInt("id"));
                allocation.setProjectTypeSectionId(json.getInt("sectionId"));
                allocation.setProjectWeekAcceptanceId(weekId);
                allocation.setRatio(json.getDouble("ratio"));
                allocation.setSectionSumK(weekAcceptance.getIsAdd() == 0 ? -json.getDouble("k") : json.getDouble("k"));
                allocations.add(allocation);
            }
            if(allocations.size() > 0){
                projectWeekKAllocationService.save(allocations,weekId);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }

    /**
     *  外部项目 提交审核周验收 接口
     * @param projectId 项目ID
     * @return
     */
    @RequestMapping("/approvedWeek")
    public @ResponseBody ViewData approvedWeek(Integer projectId,String sectionJsonArr,Integer weekId,String remark){
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
//        if(RoleEnum.PROJECT_MANAGER.ordinal() != user.getRoleId()){
//            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
//        }
        if(CommonUtil.isEmpty(projectId,weekId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(CommonUtil.isEmpty(sectionJsonArr)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        Project project = projectService.queryBaseProjectById(projectId);
        if(null == project){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
        }
        try {
            ProjectWeekAcceptance acceptance = new ProjectWeekAcceptance();
            acceptance.setStatus(1); // 提交
            acceptance.setProjectId(projectId);
            acceptance.setSectionDetail(sectionJsonArr);
            acceptance.setId(weekId);
            acceptance.setRemark(remark);
            projectWeekAcceptanceService.update(acceptance);
            // 周验收通过后 推送给A导师
            User aTeahcher = userService.queryUserDeviceTypeAndToken(project.getaTeacher()).get(0);
            SystemInfo info = new SystemInfo();
            info.setTitle(TextMessage.PROJECT_APPROVED_WEEK_TITLE);
            info.setContent(MessageFormat.format(TextMessage.PROJECT_APPROVED_WEEK_CONTENT,project.getProjectNameShort()));
            info.setUserId(aTeahcher.getId());
            info.setType(SystemInfoEnum.PROJECT_INFO.ordinal());
            systemInfoService.addSystemInfo(info);

            Map<String,String> extendsParams = new HashMap<>();
            extendsParams.put("type",info.getType().toString());
            PushMessageUtil.pushMessages(aTeahcher,info.getTitle(),extendsParams);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }




    /**
     *  外部项目 申请周验收 接口
     * @param projectId 项目ID
     * @return
     */
    @RequestMapping("/applicationWeek")
    public @ResponseBody ViewData applicationWeek(Integer projectId){
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
//        if(RoleEnum.PROJECT_MANAGER.ordinal() != user.getRoleId()){
//            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
//        }
        if(CommonUtil.isEmpty(projectId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        Project project = projectService.queryProjectById(projectId);
        if(null == project){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
        }
        // 判断该项目下 是否还有未完成的周验收， 如果有未完成的周验收， 则不允许
        if(null != project.getAcceptances() || project.getAcceptances().size() > 0 && project.getaTeacher().equals(user.getId())){
            for (ProjectWeekAcceptance acceptance : project.getAcceptances()){
                if(acceptance.getStatus() != 2){
                    return buildFailureJson(StatusConstant.Fail_CODE,"还有没有验收完成的周验收");
                }
            }
        }
        try {
            ProjectWeekAcceptance acceptance = new ProjectWeekAcceptance();
            acceptance.setStatus(0); // 提交
            acceptance.setProjectId(projectId);
            projectWeekAcceptanceService.save(acceptance);
            // 申请周验收后 推送给B导师
            User bTeahcher = userService.queryUserDeviceTypeAndToken(project.getbTeacherId()).get(0);
            SystemInfo info = new SystemInfo();
            info.setTitle(TextMessage.PROJECT_APPLICATION_WEEK_TITLE);
            info.setContent(MessageFormat.format(TextMessage.PROJECT_APPLICATION_WEEK_CONTENT,project.getProjectNameShort()));
            info.setUserId(bTeahcher.getId());
            info.setType(SystemInfoEnum.PROJECT_INFO.ordinal());
            systemInfoService.addSystemInfo(info);
            Map<String,String> extendsParams = new HashMap<>();
            extendsParams.put("type",info.getType().toString());
            PushMessageUtil.pushMessages(bTeahcher,info.getTitle(),extendsParams);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"提交失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"提交成功");
    }



    /**
     * 获取外部项目列表
     * @param pageNO
     * @param pageSize
     * @param  sortType 0:默认排序 1:项目已获总K 排序  2：项目进度 排序 3：当周新增K值 排序 4：当月新增K值 排序 5：项目效率 排序
     * @return
     */
    @RequestMapping("/getProjectList")
    public @ResponseBody ViewData getProjectList(Integer pageNO,Integer pageSize,Integer status,Integer departmentId,
                                                 Integer projectType,Integer sortType){
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
        if(CommonUtil.isEmpty(pageNO,pageSize,sortType)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectService.queryProjectByItem(user.getId(),user.getRoleId(),pageNO,pageSize,
                        status,departmentId,projectType,sortType));
    }

    /**
     *  通过项目ID 获取项目详情
     * @param projectId
     * @return
     */
    @RequestMapping("/getProject")
    public @ResponseBody ViewData getProject(Integer projectId){
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
        if(CommonUtil.isEmpty(projectId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectService.queryProjectById(projectId));
    }


    /**
     *  查询项目的进展状态显示 列表
     * @param projectId
     * @return
     */
    @RequestMapping("/acceptanceList")
    public @ResponseBody ViewData acceptanceList(Integer projectId){
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
        if(CommonUtil.isEmpty(projectId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectAcceptanceRecordService.acceptanceList(projectId));
    }

    /**
     * 结项记录集合
     * @param projectId 外部项目id
     * @param type 验收类型 0：破题  1：内部  2：外部
     * @return
     */
    @RequestMapping("/getAcceptanceRecord")
    public @ResponseBody ViewData getAcceptanceRecord(Integer projectId,Integer type){
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
        if(CommonUtil.isEmpty(projectId,type)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                projectAcceptanceRecordService.list(projectId,type));
    }



    /**
     *  查询项目 的周验收列表详情
     * @param projectId 项目ID
     * @return 周验收集合
     */
    @RequestMapping("/acceptanceWeekList")
    @ResponseBody
    public ViewData acceptanceWeekList(Integer projectId) {
        try {
            if (null == projectId) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",projectWeekAcceptanceService.list(projectId));
        } catch (Exception e) {
            logger.error("服务器超时，获取周验收列表失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取周验收列表失败");
        }
    }

    /**
     * 批量内部结项审核通过
     * @param projectIds 项目ids 以逗号隔开
     * @return
     */
    @RequestMapping("/saveUpdateList")
    @ResponseBody
    public ViewData saveUpdateList(String projectIds) {
        try {
            if (null == projectIds) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空" );
            }
            User user = (User) LoginHelper.getCurrentUser();
            projectService.saveUpdateList(projectIds,user.getId());
            // 推送
            return buildFailureJson(StatusConstant.SUCCESS_CODE,"操作成功 " );
        } catch (Exception e) {
            logger.error("服务器超时，批量通过内部审核失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，批量通过内部审核失败");
        }
    }
    /**
     * 获取需要可审核的项目
     * @param pageNO
     * @param pageSize
     * @return
     */
    @RequestMapping("/getAuditProject")
    @ResponseBody
    public ViewData getAuditProject(Integer pageNO,Integer pageSize,Integer departmentId,
                                    Integer projectType,Integer sortType) {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
//            if (!user.getRoleId().equals(RoleEnum.C_TEACHER.ordinal()) && !user.getRoleId().equals(RoleEnum.PROJECT_MANAGER.ordinal())
//                    && !user.getRoleId().equals(RoleEnum.PERFORMANCE_EMPLOYEE.ordinal())) {
//                return buildFailureJson(StatusConstant.NOT_AGREE,"你不是C导师，没有权限");
//            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectService.getAuditProject(pageNO,pageSize,user.getId(),user.getRoleId(),departmentId,projectType,sortType));
        } catch (Exception e) {
            logger.error("服务器超时，获取需要可审核的项目失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取需要可审核的项目失败");
        }
    }

    /**
     * 根据时间段获取破题统计 （统计图）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping("/getByTimeStatistics")
    @ResponseBody
    public ViewData getByTimeStatistics(Long startTime , Long endTime) {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            if (null == startTime || null == endTime ) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }

            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectService.getByTimeStatistics(startTime,endTime,user.getDepartmentId(),user.getRoleId()));
        } catch (Exception e) {
            logger.error("服务器超时，获取需要可审核的项目失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取需要可审核的项目失败");
        }
    }
    /**
     * 根据时间段获取破题统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @RequestMapping("/getByTimeStatisticsData")
    @ResponseBody
    public ViewData getByTimeStatisticsData(Long startTime , Long endTime) {
        try {
            Object obj = LoginHelper.getCurrentUser();
            if(null == obj){
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            if(!(obj instanceof User)){
                return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
            }
            User user = (User)obj;
            if (null == startTime || null == endTime ) {
                return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空 ");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectService.getByTimeStatisticsData(startTime,endTime,user.getDepartmentId(),user.getRoleId()));
        } catch (Exception e) {
            logger.error("服务器超时，获取需要可审核的项目失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取需要可审核的项目失败");
        }
    }

    /**
     * 传递卡 外部项目筛选
     * @return
     */
    @RequestMapping("/getWorkDiaryPro")
    @ResponseBody
    public ViewData getWorkDiaryPro() {
        try {
            User user = (User) LoginHelper.getCurrentUser();
            if (null == user) {
                return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
            }
            return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                    projectService.getWorkDiaryPro(user.getId(),user.getRoleId()));
        } catch (Exception e) {
            logger.error("服务器超时，获取项目列表失败",e);
            return buildFailureJson(StatusConstant.Fail_CODE,"服务器超时，获取项目列表失败");
        }
    }
}
