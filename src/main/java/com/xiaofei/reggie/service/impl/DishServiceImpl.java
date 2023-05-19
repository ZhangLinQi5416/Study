package com.xiaofei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaofei.reggie.dto.DishDto;
import com.xiaofei.reggie.entity.Dish;
import com.xiaofei.reggie.entity.DishFlavor;
import com.xiaofei.reggie.mapper.DishMapper;
import com.xiaofei.reggie.service.DishFlavorService;
import com.xiaofei.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增菜品，同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);

        //保存口味到dish_flavor表中
        //取出菜品dishid
        Long id = dishDto.getId();

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //设置dishId(foreach版）
//        List<DishFlavor> dishFlavors = new ArrayList<>();
//        for(DishFlavor flavor : flavors){
//            flavor.setDishId(id);
//            dishFlavors.add(flavor);
//        }
//
//        dishFlavorService.saveBatch(dishFlavors);

        //设置dishId(lambda版）
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 通过id查询菜品信息，并返回口味数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        //将值拷贝给dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前页面提交的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

//    /**
//     * 修改状态
//     * @param status
//     * @param ids
//     */
//    @Override
//    public void updateStatus(int status, List<Long> ids) {
//        //设置用于批量更新的条件构造器
//        UpdateWrapper<Dish> wrapper = new UpdateWrapper<>();
//        List<Map<String, Object>> updateList = new ArrayList<>();
//
//        for(Long id : ids){
//            Map<String,Object> map = new HashMap<>();
//            map.put("id",id);
//            map.put("status",status);
//            updateList.add(map);
//        }
//
//
//    }
}
