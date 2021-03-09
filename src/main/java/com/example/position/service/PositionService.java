package com.example.position.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.dto.PositionDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.entity.TbPosition;
import com.example.position.vo.PositionBaseInfoVo;
import com.example.position.vo.PositionVo;

import java.util.List;

public interface PositionService extends IService<TbPosition> {

    List<PositionVo> getList(PositionDto dto)throws Exception;

    List<TbPosition> getListName(Integer userId)throws Exception;

    TbPosition getByCode2AndUserId(Integer userId,String code2)throws Exception;

    Boolean del(Integer id)throws Exception;

    Boolean add(PositionDto dto)throws Exception;
}