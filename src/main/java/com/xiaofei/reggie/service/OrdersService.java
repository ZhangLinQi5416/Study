package com.xiaofei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaofei.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 提交订单
     * @param orders
     */
    public void submit(Orders orders);
}
