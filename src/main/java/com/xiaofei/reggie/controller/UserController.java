package com.xiaofei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaofei.reggie.common.R;
import com.xiaofei.reggie.entity.User;
import com.xiaofei.reggie.service.UserService;
import com.xiaofei.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info("发送信息请求=========>{}" , user.toString());

        //获取手机号
        String phone = user.getPhone().toString();

        if(phone != null){
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("验证码====>{}" , code);

            //调用阿里云API发送短信
            //SMSUtils.sendMessage("瑞吉外卖", "SMS_460750644",phone,code);

            //将生成的验证码存到session
            session.setAttribute(phone,code);

            return R.success("短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info("用户登录==========>{}" , map.toString());

        //获取手机号与验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从session中获取验证码
        Object codeFromSession = session.getAttribute(phone);

        //进行验证码匹配
        if(codeFromSession != null && codeFromSession.equals(code)){
            //匹配成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

//            //如果是新用户则注册
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            return R.success(user);
        }
        return R.error("登陆失败");
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");

        return R.success("退出登录成功");
    }

}
