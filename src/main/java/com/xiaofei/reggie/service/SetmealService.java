package com.xiaofei.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaofei.reggie.dto.SetmealDto;
import com.xiaofei.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);

    public SetmealDto getDto(Long id);

    public void UpdateWithDish(SetmealDto setmealDto);
}
