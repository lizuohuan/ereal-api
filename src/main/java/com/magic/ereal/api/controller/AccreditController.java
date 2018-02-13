package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Accredit;
import com.magic.ereal.business.entity.SystemInfo;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.enums.SystemInfoEnum;
import com.magic.ereal.business.push.PushMessageUtil;
import com.magic.ereal.business.service.AccreditService;
import com.magic.ereal.business.service.SystemInfoService;
import com.magic.ereal.business.service.UserService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.business.util.TextMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * 授权用户 控制器
 * Created by Eric Xie on 2017/5/23 0023.
 */
@Controller
@RequestMapping("/accredit")
public class AccreditController extends BaseController {

    @Resource
    private AccreditService accreditService;
    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;



    /**
     * 获取 授权人 列表
     * @param type
     * @return
     */
    @RequestMapping("/queryAccreditByToUser")
    public @ResponseBody ViewData queryAccreditByToUser(Integer type){
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
        if(CommonUtil.isEmpty(type)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }

        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                accreditService.queryAccreditByToUser(user.getId(),type));
    }


    /**
     * 获取被授权者 目录
     * @param type
     * @return
     */
    @RequestMapping("/queryAccredit")
    public @ResponseBody ViewData queryAccredit(Integer type){
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
        List<Accredit> data = accreditService.queryAccredit(user.getId(), type);
        Map<String,Object> result = new HashMap<>();
        List<Accredit> works = new ArrayList<>();
        List<Accredit> projects = new ArrayList<>();
        for (Accredit accredit :data){
            if(accredit.getType() == 1){
                projects.add(accredit);
            }else {
                works.add(accredit);
            }
        }
        result.put("works",works);
        result.put("projects",projects);
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                result);
    }

    /**
     * 新增 或者 更新授权目录
     * @return
     */
    @RequestMapping("/addAccredit")
    public @ResponseBody ViewData addAccredit(String toUserIds, Integer type){

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
        try {

            // 判断当前人 是否被授权，授权后不能继续授权
            List<Accredit> accredits1 = accreditService.queryAccredit(user.getId(), type);
            List<Integer> waitPushIds = new ArrayList<>();
            if(null != accredits1 && accredits1.size() > 0){
                for (Accredit accredit : accredits1) {
                    waitPushIds.add(accredit.getToUserId());
                }
            }
            List<Integer> toUserIds_ = new ArrayList<>();
            if(!CommonUtil.isEmpty(toUserIds)){
                String[] split = toUserIds.split(",");
                for (String id : split){
                    toUserIds_.add(Integer.parseInt(id));
                }
                int i = accreditService.countAccreditByToUser(toUserIds_, type, waitPushIds.size() == 0 ? null : waitPushIds);
                if(i != 0){
                    return buildFailureJson(StatusConstant.Fail_CODE,"列表中存在已经被授权人");
                }
            }
            if(type != null && CommonUtil.isEmpty(toUserIds)){
                accreditService.delAccredit(user.getId(),type);
            }
            List<Accredit> accredits = new ArrayList<>();
            if(!CommonUtil.isEmpty(toUserIds)){
                String[] split = toUserIds.split(",");
                for (String id : split){
                    Accredit accredit = getAccredit(type, user, id);
                    accredits.add(accredit);
                }
            }
            if(accredits.size() > 0){
                accreditService.addAccredit(accredits,user.getId(),type);
            }

            for (Integer waitPushId : waitPushIds) {
                for (Accredit accredit : accredits) {
                    if(waitPushId.equals(accredit.getToUserId())){
                        accredits.remove(accredit);
                        break;
                    }
                }
            }

            // 查询需要推送的用户
            waitPushIds = new ArrayList<>();
            for (Accredit accredit : accredits) {
                waitPushIds.add(accredit.getToUserId());
            }
            List<User> users = userService.queryUserDeviceTypeAndToken(waitPushIds);
            // 推送
            List<SystemInfo> infos = new ArrayList<>();
            Map<String,String> params = new HashMap<>();
            params.put("type",SystemInfoEnum.OTHER_INFO.ordinal()+"");
            if(null != users){
                for (User user1 : users) {
                    SystemInfo info = new SystemInfo();
                    info.setUserId(user1.getId());
                    info.setType(SystemInfoEnum.OTHER_INFO.ordinal());
                    info.setTitle(TextMessage.ACCREDIT_TITLE);
                    info.setContent(MessageFormat.format(TextMessage.ACCREDIT_CONTENT,user.getName(),null != type && type == 1  ? "项目" : "日志"));
                    infos.add(info);
                    PushMessageUtil.pushMessages(user1,info.getTitle(),params);
                }
            }
            if(infos.size() > 0){
                systemInfoService.addSystemInfo(infos);
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"设置失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"添加成功");
    }



    private Accredit getAccredit(Integer type, User user, String id) {
        Accredit accredit = new Accredit();
        accredit.setFromUserId(user.getId());
        accredit.setType(type);
        accredit.setToUserId(Integer.parseInt(id));
        return accredit;
    }


}
