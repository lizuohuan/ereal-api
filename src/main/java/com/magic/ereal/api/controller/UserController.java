package com.magic.ereal.api.controller;

import com.magic.ereal.api.util.CommonUtil;
import com.magic.ereal.api.util.ViewData;
import com.magic.ereal.business.entity.Banner;
import com.magic.ereal.business.entity.Department;
import com.magic.ereal.business.entity.Email;
import com.magic.ereal.business.entity.User;
import com.magic.ereal.business.mail.Config;
import com.magic.ereal.business.mail.EmailUtil;
import com.magic.ereal.business.mail.SendEmail;
import com.magic.ereal.business.mail.SendEmailThread;
import com.magic.ereal.business.push.PushMessageUtil;
import com.magic.ereal.business.service.BannerService;
import com.magic.ereal.business.service.DepartmentService;
import com.magic.ereal.business.service.SpiritAwardsService;
import com.magic.ereal.business.service.UserService;
import com.magic.ereal.business.util.LoginHelper;
import com.magic.ereal.business.util.StatusConstant;
import com.magic.ereal.business.util.TextMessage;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Controller;
import org.springframework.util.CompositeIterator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 控制器
 * @author Qimou Xie
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private SpiritAwardsService spiritAwardsService;
    @Resource
    private BannerService bannerService;



    @RequestMapping("/isCTeacher")
    public @ResponseBody ViewData isCTeacher(){
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
        return buildSuccessViewData(StatusConstant.SUCCESS_CODE,"获取成功",
                userService.isCTeacher(user.getId()));
    }


    /**
     *  上榜接口
     * @return
     */
    @RequestMapping("/awards")
    public @ResponseBody ViewData awards(){
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
                spiritAwardsService.awards(user.getId()));

    }




    /**
     * 统计月度K王
     * @return
     */
    @RequestMapping("/staticsMaxK")
    public @ResponseBody ViewData staticsMaxK(){
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
        if(null == user.getCompanyId()){
            Department info = departmentService.info(user.getDepartmentId());
            user.setCompanyId(info.getCompanyId());
        }
        List<User> data = null;
        try {
            data = userService.staticsMaxK(user.getCompanyId(), new Date());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",
                data);

    }

    /**
     * 第一次登录 修改密码
     * @param newPwd 密码
     * @return
     */
    @RequestMapping("/editPwdFirst")
    public @ResponseBody ViewData editPwdFirst(String newPwd){
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
        if(CommonUtil.isEmpty(newPwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        if(user.getIsFirst() != 1){
            // 如果不是 第一次登录
            return buildFailureJson(StatusConstant.Fail_CODE,"已经登录过");
        }
        User sqlUser = userService.queryUserByItem(user.getAccount());
        if (null == sqlUser){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"用户不存在");
        }
        if(newPwd.equals(sqlUser.getPassword())){
            return buildFailureJson(StatusConstant.Fail_CODE,"密码不能和原密码相同");
        }
        try {
            User wait = new User();
            wait.setId(user.getId());
            wait.setPassword(newPwd);
            wait.setIsFirst(0);
            userService.updateUser(wait);
            // 更新缓存
            user.setIsFirst(0);
            LoginHelper.replaceToken(user.getToken(),user);
            // 发送邮件
            Email email = new Email(user.getEmail(), TextMessage.EMAIL_EDIT_PWD_TITLE,TextMessage.EMAIL_EDIT_PWD_CONTENT);
            EmailUtil.sendEmail(email);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"更新失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"修改成功");
    }

    /**
     * 找回密码 修改密码
     * @param currentTime 发送时间的时间戳
     * @param newPwd 新密码
     * @param email 邮箱
     * @return
     */
    @RequestMapping("/forgetPwd")
    public @ResponseBody ViewData forgetPwd(Long currentTime,String newPwd,String email){
        if(CommonUtil.isEmpty(newPwd,email) || null == currentTime){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        long l = System.currentTimeMillis() - currentTime;
        if (l / 60000   > 5) {
            return buildFailureJson(StatusConstant.Fail_CODE,"验证码失效");
        }
        User user = userService.queryUserByItem(email);
        if(null == user){
            return buildFailureJson(StatusConstant.Fail_CODE,"用户不存在");
        }
        try {
            User wait = new User();
            wait.setId(user.getId());
            wait.setPassword(newPwd);
            userService.updateUser(wait);
            // 发送邮件
            EmailUtil.sendEmail(new Email(user.getEmail(), TextMessage.EMAIL_EDIT_PWD_TITLE,TextMessage.EMAIL_EDIT_PWD_CONTENT));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"找回密码失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"操作成功");

    }

    /**
     * 忘记密码  发送验证码 到邮箱
     * @return
     */
    @RequestMapping("/sendMail")
    public @ResponseBody ViewData sendMail(String account){
        if(CommonUtil.isEmpty(account)){
            return buildFailureJson(StatusConstant.Fail_CODE,"没有输入帐号或者邮箱");
        }
        User user = userService.queryUserByItem(account);
        if(null == user || null == user.getEmail()){
            return buildFailureJson(StatusConstant.Fail_CODE,"帐号或者邮箱不存在");
        }
        String code = Config.getRandom(6);
        String content = MessageFormat.format(Config.content,code,5);
        try {
            Email email = new Email(user.getEmail(),"一真OA找回密码",content);
            EmailUtil.sendEmail(email);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"验证码发送失败");
        }
        Map<String,Object> data = new HashMap<>();
        data.put("code",code);
        data.put("currentTime",System.currentTimeMillis());
        data.put("email",user.getEmail());
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"发送成功",data);
    }


    /**
     *  修改密码 接口
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @RequestMapping("/editPassword")
    public @ResponseBody ViewData editPassword(String oldPwd,String newPwd){
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
        if(CommonUtil.isEmpty(oldPwd,newPwd)){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        User baseUser = userService.queryBaseInfo(user.getId());
        if(null == baseUser){
            return buildFailureJson(StatusConstant.OBJECT_NOT_EXIST,"用户不存在");
        }
        if(!oldPwd.equals(baseUser.getPassword())){
            return buildFailureJson(StatusConstant.Fail_CODE,"旧密码不正确");
        }
        User wait = new User();
        try {
            wait.setId(user.getId());
            wait.setPassword(newPwd);
            userService.updateUser(wait);
            // 发送邮件
            EmailUtil.sendEmail(new Email(baseUser.getEmail(), TextMessage.EMAIL_EDIT_PWD_TITLE,TextMessage.EMAIL_EDIT_PWD_CONTENT));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE, "更新失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE, "修改成功");

    }


    @RequestMapping("/updateUser")
    public @ResponseBody ViewData updateUser(String avatar,String name,String phoneNumber,Integer sex,
                                             Long birthday){
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
        if(CommonUtil.isEmpty(avatar) && CommonUtil.isEmpty(name) && CommonUtil.isEmpty(phoneNumber)
                && CommonUtil.isEmpty(sex) && CommonUtil.isEmpty(birthday)){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        User wait = new User();
        wait.setId(user.getId());
        if(!CommonUtil.isEmpty(avatar)){wait.setAvatar(avatar);user.setAvatar(avatar);}
        if(!CommonUtil.isEmpty(name)){wait.setName(name);user.setName(name);}
        if(!CommonUtil.isEmpty(phoneNumber)){
            //判断修改的手机号是否存在
            if(!user.getPhone().equals(phoneNumber)){
                User user1 = userService.queryUserByPhone(phoneNumber);
                if(null != user1){
                    return buildFailureJson(StatusConstant.OBJECT_EXIST,"该手机号已经存在");
                }
            }
            wait.setPhone(phoneNumber);
            user.setPhone(phoneNumber);}
        if(!CommonUtil.isEmpty(sex)){wait.setSex(sex);user.setSex(sex);}
        if(!CommonUtil.isEmpty(birthday)){wait.setBirthday(new Date(birthday));user.setBirthday(new Date(birthday));}

        try {
            userService.updateUser(wait);
            // 更新缓存数据
            LoginHelper.replaceToken(user.getToken(),user);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE, "更新失败");
        }
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"更新成功");
    }

    /**
     * 通过 token 获取用户信息
     * @return
     */
    @RequestMapping("/getInfo")
    public @ResponseBody ViewData getInfo(){
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
            User temp = new User();
            temp.setId(user.getId());
            temp.setLastLoginTime(new Date());
            userService.updateUser(temp);
            // 判断今天是否是生日
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if(simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(user.getBirthday()))){
                List<Banner> banners = bannerService.queryBannerByType(3);
                if(null != banners && banners.size() > 0){
                    user.setBanner(banners.get(0));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return buildFailureJson(StatusConstant.Fail_CODE,"获取失败");
        }
        return buildSuccessJson(StatusConstant.SUCCESS_CODE,"获取成功",obj);
    }

    /**
     *  用户登录接口
     * @param account 帐号|手机号
     * @param password 密码 MD5
     * @param deviceToken 设备token
     * @param deviceType 设备类型 0:android 1:ios
     * @return
     */
    @RequestMapping("/login")
    public @ResponseBody ViewData login(String account,String password,String deviceToken,Integer deviceType){

        if(CommonUtil.isEmpty(account,password,deviceToken) || null ==deviceType){
            return buildFailureJson(StatusConstant.FIELD_NOT_NULL,"字段不能为空");
        }
        User user = userService.queryUserByItem(account);
        if(null == user){
            return buildFailureJson(StatusConstant.Fail_CODE,"用户名不存在");
        }
        if(null == user.getRoleId()){
            return buildFailureJson(StatusConstant.Fail_CODE,"用户不可用");
        }
        if(null == user.getPassword() || !user.getPassword().equals(password)){
            return buildFailureJson(StatusConstant.Fail_CODE,"密码错误");
        }
        try {
            // 之前缓存
            if(!CommonUtil.isEmpty(user.getToken())){
                LoginHelper.delObject(user.getToken());
            }
            User temp = new User();
            temp.setId(user.getId());
            temp.setDeviceType(deviceType);
            temp.setDeviceToken(deviceToken);
            temp.setLastLoginTime(new Date());
            temp.setPassword(null);

            user.setDeviceType(deviceType);
            user.setDeviceToken(deviceToken);
            user.setLastLoginTime(new Date());
            user.setPassword(null);
            // 设置缓存
            String token = LoginHelper.addToken(user);
            temp.setToken(token);
            user.setToken(token);
            // 更新用户信息
            userService.updateUser(temp);
            // 判断今天是否是生日
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if(simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(user.getBirthday()))){
                List<Banner> banners = bannerService.queryBannerByType(3);
                if(null != banners && banners.size() > 0){
                    banners.get(0).setSource(0);
                    user.setBanner(banners.get(0));
                }
            }
            return buildSuccessJson(StatusConstant.SUCCESS_CODE,"登录成功",user);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            if(CommonUtil.isEmpty(user.getToken())){
                LoginHelper.delObject(user.getToken());
            }
        }
        return buildFailureJson(StatusConstant.Fail_CODE,"登录失败");
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping("/logout")
    public @ResponseBody ViewData logout(HttpServletRequest request){
        String token = request.getHeader(LoginHelper.TOKEN);
        if(CommonUtil.isEmpty(token)){
            return buildFailureJson(StatusConstant.ARGUMENTS_EXCEPTION,"参数异常");
        }
        User user = (User)LoginHelper.getCurrentUser();
        if(null != user){
            userService.setDeviceNull(user.getId());
        }
        LoginHelper.clearToken();
        return buildSuccessCodeJson(StatusConstant.SUCCESS_CODE,"退出成功");
    }


}


