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
 *  审核日志 定时任务 综合部经理
 * Created by Eric Xie on 2017/6/13 0013.
 */
@Component
public class ApplicationWorkDiaryForComprehensiveManagerTask {

    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;

    /**
     * 每天 早上 10点30分执行一次
     */
    @Scheduled(cron = "0 30 10 * * ?")
    public void applicationWorkDiary(){

        List<User> users = userService.staticsComprehensiveManager(new Date());
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
