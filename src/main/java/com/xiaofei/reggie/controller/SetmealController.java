package com.xiaofei.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaofei.reggie.common.R;
import com.xiaofei.reggie.dto.SetmealDto;
import com.xiaofei.reggie.entity.Category;
import com.xiaofei.reggie.entity.Setmeal;
import com.xiaofei.reggie.entity.SetmealDish;
import com.xiaofei.reggie.service.CategoryService;
import com.xiaofei.reggie.service.SetmealDishService;
import com.xiaofei.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐信息======>{}" , setmealDto.toString());

        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){
        log.info("套餐分页查询=======> page:{} , pageSize:{} , name:{}" , page ,pageSize ,name);
        //分页构造器
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null , Setmeal::getName , name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        setmealService.page(setmealPage,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = new ArrayList<>();

        list = records.stream().map((item) -> {
           SetmealDto setmealDto = new SetmealDto();

           BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delect(@RequestParam List<Long> ids){
        log.info("批量删除======> {}" , ids);

        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,@RequestParam List<Long> ids){
        List<Setmeal> setmealList = new ArrayList<>();

        for(Long id:ids){
            Setmeal setmeal = setmealService.getById(id);

            setmeal.setStatus(status);
            setmealList.add(setmeal);
        }

        setmealService.updateBatchById(setmealList);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId,int status){
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null , Setmeal::getCategoryId , categoryId);
        queryWrapper.eq( Setmeal::getStatus , 1);

        List<Setmeal> setmeals = setmealService.list(queryWrapper);

        return R.success(setmeals);
    }

    /**
     * 修改菜品页面，回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info("回显数据=========>{}" , id);

        SetmealDto dto = setmealService.getDto(id);

        return R.success(dto);
    }

    /**
     * 提交修改菜品数据
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updataWithDish(@RequestBody SetmealDto setmealDto){
        log.info("提交修改菜品数据=====>{}" , setmealDto);

        setmealService.UpdateWithDish(setmealDto);

        return R.success("修改成功");
    }
}
