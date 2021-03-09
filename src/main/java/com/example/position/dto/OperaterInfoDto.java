package com.example.position.dto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OperaterInfoDto extends Page{

    private Integer userId;
    private String name;//股票名称
    private String code1;
    private String code2;
    private Integer operater;//操作  1买入 2卖出
    private Integer status;//状态  0未售出  1已售出  2部分售出
    private String startDate;//操作时间
    private String endDate;//操作时间
}
