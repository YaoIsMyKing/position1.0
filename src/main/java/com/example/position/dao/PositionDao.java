package com.example.position.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.entity.TbPosition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PositionDao extends BaseMapper<TbPosition> {

}
