package com.example.position.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.position.dto.Page;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionBaseInfoVo  {
    private Integer userId;
    private String name;//股票名称
    private String code;//代码
    private BigDecimal maxPrice;//买入最高价
    private BigDecimal minPrice;//买入最低价
    private BigDecimal profit;//买卖利润
    private BigDecimal curr_price;//现价
    private Integer totalCount;//总可操作股票数量
    private BigDecimal marketVal;//市值
    private BigDecimal proportion;//占比
}
