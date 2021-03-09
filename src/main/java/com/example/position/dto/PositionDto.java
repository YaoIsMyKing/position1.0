package com.example.position.dto;

import lombok.Data;

@Data
public class PositionDto extends Page{

    private Integer userId;
    private String name;//股票名称
    private String code1;
    private String code2;
}
