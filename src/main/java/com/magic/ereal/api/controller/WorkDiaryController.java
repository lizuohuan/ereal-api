package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.DateUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Department;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.entity.WorkDiary;
import com.magic.ereal.business.entity.WorkDiarySub;
import com.magic.ereal.business.enums.RoleEnum;
import com.magic.ereal.business.enums.TransactionType;
import com.magic.ereal.business.exception.InterfaceCommonException;
import com.magic.ereal.business.service.*;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.business.util.Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 工作日志 / 传递卡 控制器
 * Created by Eric Xie on 2017/4/24 0024.
 */
@RequestMapping("/workDiary")
@Controller
public class WorkDiaryController extends BaseController {

    @Resource
    private JobTypeService jobTypeService;
    @Resource
    private WorkDiaryService workDiaryService;
    @Resource
    private WorkDiarySubService workDiarySubService;
    @Resource
    private TransactionSubService transactionSubService;
    @Resource
    private DepartmentService departmentService;


    /**
     * 根据ID 获取
     * @param workId
     * @return
     */
    @RequestMapping("/queryWorkDiaryById")
    public @ResponseBody ViewData queryWorkDiary(Integer workId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == workId){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryWorkDiaryById(workId));
    }

    /**
     * 删除 工作日志
     * @param workDiarySubId
     * @return
     */
    @RequestMapping("/delWorkDiarySub")
    public @ResponseBody ViewData delWorkDiarySub(Integer workDiarySubId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == workDiarySubId){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        try {
            WorkDiarySub workDiarySub = workDiarySubService.info(workDiarySubId);
            if(null == workDiarySub){
                return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
            }
            workDiarySubService.delWorkDiarySub(workDiarySubId,workDiarySub.getWorkDiaryId());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"删除失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");
    }

    /**
     * 根据时间查询 日志
     * @param timeL
     * @return
     */
    @RequestMapping("/queryWorkDiaryByDate")
    public @ResponseBody ViewData queryWorkDiaryByDate(Long timeL){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == timeL){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",workDiaryService.queryWorkDiaryByDate(
                DateUtil.dateFortimestamp(timeL,"yyyy-MM-dd",null),((User) obj).getId()
        ));
    }

    /**
     *  获取用户最新一天的草稿日志
     * @return
     */
    @RequestMapping("/getNearWorkDiary")
    public @ResponseBody ViewData getNearWorkDiary(){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryNearWorkDiaryByUser(((User) obj).getId()));
    }

    /**
     * 更新 工作日志子类
     * @param workDiarySub
     * @return
     */
    @RequestMapping("/updateWorkDiarySub")
    public @ResponseBody ViewData updateWorkDiarySub(WorkDiarySub workDiarySub){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(null == workDiarySub || null == workDiarySub.getId() || null == workDiarySub.getWorkDiaryId()){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        WorkDiary workDiary = workDiaryService.queryWorkDiaryById(workDiarySub.getWorkDiaryId(),null);
        if(null == workDiary){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
        }
//        if(StatusConstant.WORKDIARY_STATUS_APPROVED.equals(workDiary.getStatus())){
//            return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"状态异常");
//        }
        try {
            // 格式化时间
            workDiarySub.setStartTime(DateUtil.dateFortimestamp(workDiarySub.getStartTimeL(),"yyyy-MM-dd HH:mm:ss",null));
            workDiarySub.setEndTime(DateUtil.dateFortimestamp(workDiarySub.getEndTimeL(),"yyyy-MM-dd HH:mm:ss",null));

            WorkDiarySub diarySub = workDiarySubService.info(workDiarySub.getId());
            if(null == diarySub){
                return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
            }

            workDiarySubService.batchUpdateWorkDiarySub(workDiarySub,workDiary.getWorkTime(),workDiary,user);
        }catch (InterfaceCommonException e){
            return buildFailureJson(StatusConstant.Fail_CODE,e.getMessage());
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"更新失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"更新成功");
    }


    /**
     * 统计日志 工作时长等
     *  userId、companyId、departmentId 不能同时存在 只能单选
     * @param userId 用户ID
     * @param companyId 分公司ID
     * @param departmentId 部门ID
     * @param time 时间 年月日 / 年月 当 flag:0 时，统计年月日 flag:1 统计 年月 不能为空
     * @param flag 当 flag:0 时，统计年月日 flag:1 统计 年月
     * @return
     */
    @RequestMapping("/countWorkDiary")
    public @ResponseBody ViewData countWorkDiary(Integer userId,Integer departmentId,Integer companyId,Integer flag,Long time){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(CommonUtil.isEmpty(flag)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(null == userId && departmentId == null && null == companyId){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(null == time || time == 0){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(flag != 0 && flag != 1){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        try {
            Date date = null;
            if(flag == 0){
                date = DateUtil.dateFortimestamp(time,"yyyy-MM-dd",null);
            }
            if(flag == 1){
                date = DateUtil.dateFortimestamp(time,"yyyy-MM",null);
            }
            if(null != userId && 0 == userId){
                userId = null;
            }
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                    workDiaryService.countWorkDiary(userId,companyId,departmentId,flag,date));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"统计失败");
        }
    }

    /**
     * 工作日志 状态更新
     * @param flag 0:提交 1:经理审核拒绝  2:经理审核通过 3：综合部审核拒绝  4：综合部审核通过
     * @param workDiaryId 工作日志ID
     * @param notes 备注 可为空
     * @param userIds 抄送者用户ID 逗号隔开
     * @return
     */
    @RequestMapping("/updateWorkDiaryStatus")
    public @ResponseBody ViewData updateWorkDiaryStatus(Integer flag,Integer workDiaryId,String notes,String userIds){
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
        if(CommonUtil.isEmpty(flag,workDiaryId)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        WorkDiary workDiary = workDiaryService.queryWorkDiaryById(workDiaryId,null);
        if(null == workDiary){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"对象不存在");
        }
        if(flag == 0){
//            if(!StatusConstant.WORKDIARY_STATUS_DRAFT.equals(workDiary.getStatus())
//                    && !StatusConstant.WORKDIARY_STATUS_REJECT.equals(workDiary.getStatus())
//                    && !StatusConstant.WORKDIARY_SYNTHESIZE_TATUS_REJECT.equals(workDiary.getStatus())
//                    && !StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus())){
//                return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"日志状态异常");
//            }
            workDiary.setStatus(StatusConstant.WORKDIARY_STATUS_PENDING);
        }else if(flag == 1){
//            if(!StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus())){
//                return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"日志状态异常");
//            }
            workDiary.setStatus(StatusConstant.WORKDIARY_STATUS_REJECT);
        }else if(flag == 2){
//            if(!StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus())
//                    ){
//                return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"日志状态异常");
//            }
            workDiary.setStatus(StatusConstant.WORKDIARY_STATUS_APPROVED);
        }
        else if(flag == 3){
            // 综合部审核拒绝
            if(!StatusConstant.WORKDIARY_STATUS_APPROVED.equals(workDiary.getStatus())
             && !StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus())
            ){
                return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"日志状态异常");
            }
            workDiary.setStatus(StatusConstant.WORKDIARY_SYNTHESIZE_TATUS_REJECT);
        }else if (flag == 4){
            // 综合部审核通过
            if(!StatusConstant.WORKDIARY_STATUS_APPROVED.equals(workDiary.getStatus())
            && !StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus())
                    ){
                return buildFailureJson(StatusConstant.ORDER_STATUS_ABNORMITY,"日志状态异常");
            }
            workDiary.setStatus(StatusConstant.WORKDIARY_SYNTHESIZE_STATUS_APPROVED);
        }
        else {
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        try {
            workDiaryService.updateWorkDiaryStatus(workDiary,notes,user,userIds,user.getId());
        }catch (InterfaceCommonException e){
            return buildFailureJson(e.getErrorCode(),e.getMessage());
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,e.getMessage());
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"更新成功");
    }


    /**
     *  查询 工作日志
     * @param isSelf 0:全部 1:我的 2:我的团队
     * @param status 日志状态
     * @param pageNO 起始页
     * @param pageSize 截至页
     * @param  orderBy 排序方式 0：默认排序  1：总工时 降序排列 2：总工时 升序排列
     * @param departmentId 部门ID  默认为当前用户的
     * @return
     */
    @RequestMapping("/queryWorkDiaryByItem")
    public @ResponseBody ViewData queryWorkDiaryByItem(Integer isSelf,Integer status,String content,Integer pageNO,Integer pageSize,
                                                       Integer orderBy,Integer departmentId){
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
        orderBy = orderBy == null ? 0 : orderBy;
        User user = (User)obj;
        Integer userId = null;
        if(null == isSelf){
            userId = null;
        }
        else if(isSelf == 1){
            userId = user.getId();
        }
        else if(isSelf == 2){
            departmentId = user.getDepartmentId();
        }
        Department department = departmentService.info(user.getDepartmentId());
        if(null == department){
            return buildFailureJson(StatusConstant.Fail_CODE,"部门不存在");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryWorkDiaryByItemForAPI(userId,status,department.getCompanyId(),departmentId,null,content,
                        pageNO,pageSize,user,orderBy));
    }

    /**
     * 通过ID 查询日志 所有信息
     * @param id
     * @return
     */
    @RequestMapping("/getWorkDiaryById")
    public @ResponseBody ViewData getWorkDiaryById(Integer id){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == id){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryWorkDiaryById(id, (User) obj));
    }

    /**
     *  保存工作日志草稿
     *  flag  固定参数 0 表示当天的23点59分59秒，需要格式化结束时间为第二天 00 点 00 分
     * @param workDiarySub
     * @return
     */
    @RequestMapping("/saveWorkDiary")
    public @ResponseBody ViewData saveWorkDiary(Long workTime, WorkDiarySub workDiarySub,Integer workDiaryId,
                                                Integer flag){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == workTime || workTime == 0){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(null != flag && flag != 0){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        User user = (User)obj;
        if(CommonUtil.isEmpty(workDiarySub.getStartTimeL(),workDiarySub.getEndTimeL())){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(null != flag){
            // 格式化时间
            workDiarySub.setEndTime(DateUtil.setTime(workDiarySub.getEndTimeL()));
        }else{
            workDiarySub.setEndTime(new Date(workDiarySub.getEndTimeL()));
        }

        workDiarySub.setStartTime(new Date(workDiarySub.getStartTimeL()));
        Integer workId = null;
        Integer subId = null;
        Map<String,Integer> dataMap = new HashMap<>();
        try {
            Date date = DateUtil.dateFortimestamp(workTime, "yyyy-MM-dd", null);

            if(null != workDiaryId){

                WorkDiary workDiary = workDiaryService.queryWorkDiaryById(workDiaryId);
//                if(null == workDiary || (!StatusConstant.WORKDIARY_STATUS_DRAFT.equals(workDiary.getStatus())
//                        && !StatusConstant.WORKDIARY_STATUS_REJECT.equals(workDiary.getStatus())
//                        && !StatusConstant.WORKDIARY_SYNTHESIZE_TATUS_REJECT.equals(workDiary.getStatus())
//                        && !StatusConstant.WORKDIARY_STATUS_PENDING.equals(workDiary.getStatus()))){
//                    return buildFailureJson(StatusConstant.Fail_CODE,"日志状态异常");
//                }
                // 时间验重 并添加日志
                workDiarySub.setWorkDiaryId(workDiary.getId());
                subId = workDiarySubService.addWorkDiarySub(workDiarySub,date,user.getId());
                workId = workDiary.getId();
                dataMap.put("subId",subId);
                dataMap.put("workId",workId);
            }else{

                WorkDiary data = new WorkDiary();
                // 创建数据
                data.setStatus(StatusConstant.WORKDIARY_STATUS_PENDING);
                data.setUserId(user.getId());
                data.setWorkTime(date);
                dataMap =  workDiaryService.addWorkDiary(data,workDiarySub);
            }
        } catch (InterfaceCommonException e){
            return buildFailureJson(StatusConstant.OBJECT_EXIST,"时间段已存在日志");
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"操作失败");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"操作成功",dataMap);
    }


    /**
     *  获取 工作类型 通过事务类型
     * @param transactionTypeId
     * @return
     */
    @RequestMapping("/getJobType")
    public @ResponseBody ViewData getJobType(Integer transactionTypeId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        if(null == transactionTypeId){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                jobTypeService.queryJobTypeByTransactionForAPI(transactionTypeId,((User)obj).getId()));
    }

    /**
     * 获取事务类型集合
     * @return
     */
    @RequestMapping("/getTransactionType")
    public @ResponseBody ViewData getTransactionType(){
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                transactionSubService.queryTransactionSubByItem(1));
    }

    /**
     * 获取事务类型集合
     * @return
     */
    @RequestMapping("/queryAllTransactionSub")
    public @ResponseBody ViewData queryAllTransactionSub(Integer userId,Integer roleId){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        userId = userId == null ? user.getId() : userId;
        roleId = roleId == null ? user.getRoleId() : roleId;
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                transactionSubService.queryAllTransactionSub(userId,roleId));
    }


    /**
     * 获取 用户 关联的 工作日志
     * @return
     */
    @RequestMapping("/getWorkDiaryByUser")
    public @ResponseBody ViewData getWorkDiaryByUser(Integer pageNO,Integer pageSize){

        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"已离职，帐号不可能用");
        }
        if(CommonUtil.isEmpty(pageNO,pageSize)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryWorkDiaryCCByUser(user.getId(),pageNO,pageSize));
    }



    /**
     *  批量审核通过
     * @return
     */
    @RequestMapping("/batchApproved")
    public @ResponseBody ViewData batchApproved(String ids){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"已离职，帐号不可能用");
        }
        if (CommonUtil.isEmpty(ids)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        Integer status = StatusConstant.WORKDIARY_STATUS_APPROVED;
        if(RoleEnum.COMPREHENSIVE_MANAGER.ordinal() == user.getRoleId()){
            status = StatusConstant.WORKDIARY_SYNTHESIZE_STATUS_APPROVED;
        }
        try {
            String[] idsStr = ids.split(",");
            List<WorkDiary> workDiaries = new ArrayList<>();
            for (int i = 0; i < idsStr.length; i++){
                WorkDiary workDiary = new WorkDiary();
                workDiary.setId(Integer.parseInt(idsStr[i]));
                workDiary.setStatus(status);
                workDiaries.add(workDiary);
            }
            if(workDiaries.size() > 0){
                workDiaryService.updateListStatus(workDiaries, user);
                return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"更新成功");
            }
        } catch (Exception e) {
            return buildSuccessCodeJson(StatusConstant.Fail_CODE,"更新失败");
        }
        return buildSuccessCodeJson(StatusConstant.Fail_CODE,"更新失败");

    }


    /**
     *  查询 待批量审核的日志列表
     * @return
     */
    @RequestMapping("/queryCheckpending")
    public @ResponseBody ViewData queryCheckpending(){
        Object obj = LoginHelper.getCurrentUser();
        if(null == obj){
            return buildFailureJson(StatusConstant.NOTLOGIN,"未登录");
        }
        if(!(obj instanceof User)){
            return buildFailureJson(StatusConstant.NOT_AGREE,"没有权限");
        }
        User user = (User)obj;
        if(StatusConstant.USER_STATUE_DIMISSION.equals(user.getIncumbency())){
            return buildFailureJson(StatusConstant.ACCOUNT_FROZEN,"已离职，帐号不可能用");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                workDiaryService.queryCheckpending(user));
    }












}
