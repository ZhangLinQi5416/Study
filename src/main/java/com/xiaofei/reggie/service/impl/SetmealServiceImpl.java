package com.xiaofei.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaofei.reggie.common.CustomException;
import com.xiaofei.reggie.dto.SetmealDto;
import com.xiaofei.reggie.entity.Setmeal;
import com.xiaofei.reggie.entity.SetmealDish;
import com.xiaofei.reggie.mapper.SetmealMapper;
import com.xiaofei.reggie.service.SetmealDishService;
import com.xiaofei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息,操作setmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) ->{
            Long id = setmealDto.getId();
            item.setSetmealId(id);

            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品关联信息，操作setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询是否为停售状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        //如果有在售状态的套餐，则抛出异常
        if(count > 0){
            throw new CustomException("套餐正在售卖，不能删除");
        }
        //删除套餐
        this.removeByIds(ids);

        //删除套餐相关联的菜品
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(dishLambdaQueryWrapper);
    }

    /**
     * 回显数据
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getDto(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //条件构造器
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    /**
     * 提交修改的数据
     * @param setmealDto
     */
    @Override
    @Transactional
    public void UpdateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);
        Long id = setmealDto.getId();

        log.info("id===========>{}" , id);

        //清除菜品表的数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(wrapper);

        //提交菜品数据
//        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//        setmealDishService.saveBatch(setmealDishes);
        //上面错了,因为setmealId为非空，而因为在setmealDto里面它是id，所以无法识别，因此报错

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }
}
