package com.example.position.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TbPosition {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("name")
    private String name;//股票名称
    @TableField("code1")
    private String code1;//代码
    @TableField("code2")
    private String code2;//代码
    @TableField("create_time")
    private String createTime;//
    @TableField("update_time")
    private String updateTime;//
    @TableField("flag")
    private Integer flag;//0已删除  1正常
}
