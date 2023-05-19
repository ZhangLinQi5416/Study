package com.xiaofei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaofei.reggie.common.BaseContext;
import com.xiaofei.reggie.common.R;
import com.xiaofei.reggie.entity.ShoppingCart;
import com.xiaofei.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppinfCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车======>{}" , shoppingCart.toString());

        //设置用户ID,指定当前用户的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //查询当前添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            //添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //添加的是套餐
            Long setmealId = shoppingCart.getSetmealId();
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }

        //如果已经存在，则数量增加一
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart != null){
            Integer number = cart.getNumber();
            number++;
            cart.setNumber(number);
            shoppingCartService.updateById(cart);

            return R.success(cart);
        }
        //如果没有存在，则添加到购物车中，数量为一
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);

        return R.success(shoppingCart);
    }

    /**
     * 减少购物车数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //获取当前操作用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //接收数据,看看是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();

        if(dishId != null){
            //是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //获取购物车数据
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        Integer number = cart.getNumber();

        //如果number大于1
        if(number > 1){
            cart.setNumber(number - 1);
            //减少购物车数量
            shoppingCartService.updateById(cart);
        }
        else{
            //如果number等于1，直接删除该条购物车数据
            shoppingCartService.removeById(cart);
        }

        return R.success(cart);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("获取购物车数据");

        //获取当前操作用户ID
        Long currentId = BaseContext.getCurrentId();

        //查询该用户的购物车数据
        //条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取当前用户ID
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //批量删除
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
