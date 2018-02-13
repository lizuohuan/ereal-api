package com.magic.ereal.api.task;

import com.magic.ereal.api.util.DateUtil;
import com.magic.ereal.business.entity.SystemInfo;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.enums.SystemInfoEnum;
import com.magic.ereal.business.push.PushMessageUtil;
import com.magic.ereal.business.service.SystemInfoService;
import com.magic.ereal.business.service.UserService;
import com.magic.ereal.business.util.TextMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  提交 工作日志 提醒推送
 * Created by Eric Xie on 2017/6/12 0012.
 */

@Component
public class SubmitWorkDiaryTask {


    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;


    /**
     * 每天早上10点 针对没有 提交工作日志的 推送
     */
    @Scheduled(cron = "0 0 10 * * ?")
//    @Scheduled(cron = "0 0/3 * * * ?")
    public void workDiaryTask(){
        List<User> users = userService.staticsNOSubmitWorkdiary(DateUtil.setTime(new Date(),-1));
        List<SystemInfo> infos = new ArrayList<>();
        for (User user : users) {
            SystemInfo info = new SystemInfo();
            info.setUserId(user.getId());
            info.setType(SystemInfoEnum.WORK_DIARY_INFO.ordinal());
            info.setTitle(TextMessage.WORKDIARY_NON_SUMMIT_TITLE);
            info.setContent(TextMessage.WORKDIARY_NON_SUMMIT_CONTENT);
            info.setCreateTime(new Date());
            infos.add(info);
            // 推送
            PushMessageUtil.pushMessages(user,info.getTitle(),null);
        }
        if(infos.size() > 0){
            systemInfoService.addSystemInfo(infos);
        }
    }



}
