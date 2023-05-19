package com.xiaofei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaofei.reggie.common.R;
import com.xiaofei.reggie.entity.Orders;
import com.xiaofei.reggie.service.OrdersService;
import com.xiaofei.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private UserService userService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);

        return R.success("提交成功");
    }

    /**
     * 手机端分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page , int pageSize){
        //分页构造器
        Page<Orders> ordersPage = new Page<>(page , pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersPage,wrapper);
        System.out.println(R.success(ordersPage));
        return R.success(ordersPage);
    }

    /**
     * 后端订单查看
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> backPage(int page , int pageSize , String number , String beginTime , String endTime){
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page , pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(number != null , Orders::getNumber , number);

        //进行时间转换
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(beginTime != null){
            LocalDateTime begin = LocalDateTime.parse(beginTime , formatter);
            wrapper.ge(Orders::getOrderTime , begin);
        }
        if(endTime != null){
            LocalDateTime end = LocalDateTime.parse(endTime , formatter);
            wrapper.le(Orders::getOrderTime , end);
        }

        ordersService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @return
     */
    @PutMapping
    public R<String> status(@RequestBody Orders orders){
        ordersService.updateById(orders);

        return R.success("修改订单状态成功");
    }
}
