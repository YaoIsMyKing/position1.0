package com.example.position.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddOperaterInfoDto  {
    private Integer userId;
    private Integer positionId;     //持仓表id
    private Integer parentId;       //关联父id
    private String name;
    private String code1;
    private String code2;
    private Integer operater;//操作  1买入 2卖出
    private BigDecimal operaterPrice;//操作价格
    private Integer count;//数量
    private BigDecimal profit;//买卖利润
    private String dateTime;//操作时间

}
