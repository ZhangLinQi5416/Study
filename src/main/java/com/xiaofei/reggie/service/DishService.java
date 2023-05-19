package com.xiaofei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaofei.reggie.dto.DishDto;
import com.xiaofei.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    //public void updateStatus(int status, List<Long> ids);
}
