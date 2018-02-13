package com.magic.ereal.api.task;

import com.magic.ereal.api.util.DateUtil;
import com.magic.ereal.business.entity.Company;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生日提醒 定时任务
 * Created by Eric Xie on 2017/6/13 0013.
 */
@Component
public class BirthdayTask {

    @Resource
    private UserService userService;
    @Resource
    private SystemInfoService systemInfoService;

    /**
     * 每天早上9点统计一次
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void birthdayInfo() {

        int days = 1;
        Date date = DateUtil.setTime(new Date(), days); // 提前1天
        List<Company> companyList = userService.staticsBirthday(date);
        for (Company company : companyList) {
            if (null != company.getUsers() && company.getUsers().size() > 0) {
                String userStr = "员工：";
                for (User user : company.getUsers()) {
                    userStr += user.getName() + "、";
                }
                userStr = userStr.substring(0, userStr.length() - 1);
                if (null != company.getPerformanceEmployees() && company.getPerformanceEmployees().size() > 0) {
                    List<SystemInfo> infos = new ArrayList<>();
                    for (User user : company.getPerformanceEmployees()) {
                        SystemInfo info = new SystemInfo();
                        info.setUserId(user.getId());
                        info.setType(SystemInfoEnum.OTHER_INFO.ordinal());
                        info.setTitle(TextMessage.BIRTHDAY_NEW_TITLE);
                        info.setContent(MessageFormat.format(TextMessage.BIRTHDAY_NEW_CONTENT,
                                Math.abs(days), userStr));
                        infos.add(info);
                        PushMessageUtil.pushMessages(user, info.getTitle(), null);
                    }
                    if (infos.size() > 0) {
                        systemInfoService.addSystemInfo(infos);
                    }

                }
            }
        }
    }

    /**
     * 每天下午14点统计一次
     */
    @Scheduled(cron = "0 0 14 * * ?")
    public void birthdayInfo_() {
        int days = 1;
        Date date = DateUtil.setTime(new Date(), days); // 提前1天
        List<Company> companyList = userService.staticsBirthday(date);
        for (Company company : companyList) {
            if (null != company.getUsers() && company.getUsers().size() > 0) {
                String userStr = "员工：";
                for (User user : company.getUsers()) {
                    userStr += user.getName() + "、";
                }
                userStr = userStr.substring(0, userStr.length() - 1);
                if (null != company.getPerformanceEmployees() && company.getPerformanceEmployees().size() > 0) {
                    List<SystemInfo> infos = new ArrayList<>();
                    for (User user : company.getPerformanceEmployees()) {
                        SystemInfo info = new SystemInfo();
                        info.setUserId(user.getId());
                        info.setType(SystemInfoEnum.OTHER_INFO.ordinal());
                        info.setTitle(TextMessage.BIRTHDAY_NEW_TITLE);
                        info.setContent(MessageFormat.format(TextMessage.BIRTHDAY_NEW_CONTENT,
                                Math.abs(days), userStr));
                        infos.add(info);
                        PushMessageUtil.pushMessages(user, info.getTitle(), null);
                    }
                    if (infos.size() > 0) {
                        systemInfoService.addSystemInfo(infos);
                    }

                }
            }
        }
    }


}
