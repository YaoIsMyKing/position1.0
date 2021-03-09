package com.example.position.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.dto.PositionDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.entity.TbPosition;
import com.example.position.service.OperaterInfoService;
import com.example.position.service.PositionService;
import com.example.position.vo.BoException;
import com.example.position.vo.PageVo;
import com.example.position.vo.PositionVo;
import com.example.position.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 持仓
 */
@RestController
@RequestMapping("/stock/position")
@Slf4j
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/list")
    public ResultVo list(HttpServletRequest request, PositionDto dto){
        try{
            List<PositionVo> list = positionService.getList(dto);
            return ResultVo.success(list);
        }catch (Exception e){
            log.error("list error data:"+dto,e);
            return ResultVo.error();
        }
    }

    @GetMapping("/baseList")
    public ResultVo baseList(HttpServletRequest request, Integer userId){
        try{
            List<TbPosition> list = positionService.getListName(userId);
            return ResultVo.success(list);
        }catch (Exception e){
            log.error("listName error data:"+userId,e);
            return ResultVo.error();
        }
    }

    @PostMapping("/del")
    public ResultVo del(HttpServletRequest request, Integer id){
        try{
            Boolean boo = positionService.del(id);
            return ResultVo.success();
        }catch (BoException e){
            return ResultVo.error(e.getMessage());
        }catch (Exception e){
            log.error("del error data:"+id,e);
            return ResultVo.error();
        }
    }

    @PostMapping("/add")
    public ResultVo add(HttpServletRequest request, PositionDto dto){
        try{
            Boolean boo = positionService.add(dto);
            return ResultVo.success();
        }catch (BoException e){
            return ResultVo.error(e.getMessage());
        }catch (Exception e){
            log.error("add error data:"+dto,e);
            return ResultVo.error("系统异常");
        }
    }



}
