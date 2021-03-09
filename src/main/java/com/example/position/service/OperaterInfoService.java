package com.example.position.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.position.dto.AddOperaterInfoDto;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.vo.PositionBaseInfoVo;

import java.util.List;

public interface OperaterInfoService extends IService<TbOperaterInfo> {

    Page<TbOperaterInfo> listPage(OperaterInfoDto dto)throws Exception;

    List<TbOperaterInfo> list(OperaterInfoDto dto)throws Exception;

    /**
     * 查询关联股票持仓
     * @return
     * @throws Exception
     */
    Integer countByUserIdAndCode(Integer userId,String code2)throws Exception;

    Boolean add(AddOperaterInfoDto dto)throws Exception;

    Boolean del(Integer  id)throws Exception;


}