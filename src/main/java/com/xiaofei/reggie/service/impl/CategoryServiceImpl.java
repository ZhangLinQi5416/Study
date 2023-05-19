package com.xiaofei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaofei.reggie.common.CustomException;
import com.xiaofei.reggie.entity.Category;
import com.xiaofei.reggie.entity.Dish;
import com.xiaofei.reggie.entity.Setmeal;
import com.xiaofei.reggie.mapper.CategoryMapper;
import com.xiaofei.reggie.service.CategoryService;
import com.xiaofei.reggie.service.DishService;
import com.xiaofei.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，如果该分类已关联菜品或套餐，则抛出异常
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联菜品，若有则抛出异常
        //添加查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        if(count1 > 0){
            //抛出异常
            throw new CustomException("当前分类已关联菜品，无法删除");

        }

        //查询当前分类是否关联套餐，若有则抛出异常
        //添加查询条件
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            //抛出异常
            throw new CustomException("当前分类已关联套餐，无法删除");
        }

        //无关联，正常删除
        super.removeById(id);
    }
}
