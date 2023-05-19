package com.xiaofei.reggie.dto;

import com.xiaofei.reggie.entity.Setmeal;
import com.xiaofei.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
