package com.example.position.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TbOperaterInfo extends Model<TbOperaterInfo> {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("user_id")
    private Integer userId;
    @TableField("position_id")
    private Integer positionId;
    @TableField("parent_id")
    private Integer parentId;
    @TableField("name")
    private String name;//股票名称
    @TableField("code1")
    private String code1;
    @TableField("code2")
    private String code2;
    @TableField("operater")
    private Integer operater;//操作  1买入 2卖出
    @TableField("status")
    private Integer status;//状态  0未售出  1已售出  2部分售出
    @TableField("operater_price")
    private BigDecimal operaterPrice;//操作价格
    @TableField("make_money")
    private BigDecimal makeMoney;//成交金额  已计算手续费
    @TableField("profit")
    private BigDecimal profit;//买卖利润
    @TableField("fit")
    private BigDecimal fit;//手续费
    @TableField("count")
    private Integer count;//总股票数量
    @TableField("surplus_count")
    private Integer surplusCount;//剩余可操作股票
    @TableField("date_time")
    private String dateTime;//操作时间
    @TableField("create_time")
    private String createTime;
    @TableField("update_time")
    private String updateTime;
    @TableField("flag")
    private Integer flag;//1 正常  0已删除

}
