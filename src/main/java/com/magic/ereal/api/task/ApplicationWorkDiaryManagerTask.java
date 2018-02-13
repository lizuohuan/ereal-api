package com.magic.ereal.api.task;

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
 * 统计当天时刻 为审核工作日志的 部门经理  任务调度
 * Created by Eric Xie on 2017/6/13 0013.
 */
@Component
public class ApplicationWorkDiaryManagerTask {




    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;

    /**
     * 每天 早上 12点执行一次
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void applicationWorkDiary(){

        List<User> users = userService.staticsManager(new Date());
        List<SystemInfo> infos = new ArrayList<>();
        for (User user : users) {
            SystemInfo info = new SystemInfo();
            info.setUserId(user.getId());
            info.setTitle(TextMessage.WORKDIARY_PENGDING_TITLE);
            info.setContent(TextMessage.WORKDIARY_PENGDING_CONTENT);
            info.setType(SystemInfoEnum.WORK_DIARY_INFO.ordinal());
            infos.add(info);
            // 推送
            PushMessageUtil.pushMessages(user,info.getTitle(),null);
        }
        if(infos.size() > 0){
            systemInfoService.addSystemInfo(infos);
        }
    }


}
