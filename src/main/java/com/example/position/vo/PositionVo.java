package com.example.position.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionVo {
    private Integer id;
    private Integer userId;
    private String name;//股票名称
    private String code1;//代码
    private String code2;//代码
    private BigDecimal avgCost;//平均价格   -  查询时计算
    private BigDecimal maxPrice;//买入最高价
    private BigDecimal minPrice;//买入最低价
    private BigDecimal paperProfit;//账面盈亏    -  查询时计算
    private BigDecimal currPrice;//现价        -  查询时计算
    private Integer count;//总可操作股票数量
    private BigDecimal marketVal;//市值
    private BigDecimal proportion;//占比   -  查询时计算
    private BigDecimal profit;//买卖利润
    private BigDecimal fit;//手续费    -查询时计算
    private String createTime;//
    private String updateTime;//
}
