package com.example.position.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.position.dto.AddOperaterInfoDto;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.service.OperaterInfoService;
import com.example.position.vo.BoException;
import com.example.position.vo.PageVo;
import com.example.position.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 详细交易记录
 */
@RestController
@RequestMapping("/stock/operaterinfo")
@Slf4j
public class OperaterInfoController {

    @Autowired
    private OperaterInfoService operaterInfoService;


    @GetMapping("/list")
    public PageVo list(HttpServletRequest request, OperaterInfoDto dto){
        try{
            Page<TbOperaterInfo> list = operaterInfoService.listPage(dto);
            return PageVo.success(list.getRecords(),(int)list.getTotal());
        }catch (Exception e){
            log.error("list error data:"+dto,e);
            return PageVo.error();
        }
    }

    @PostMapping("/add")
    public ResultVo add(HttpServletRequest request, AddOperaterInfoDto dto){
        try{
            Boolean boo =  operaterInfoService.add(dto);
            if(boo){
                return ResultVo.success();
            }else{
                return ResultVo.error("未知原因，新增失败");
            }
        }catch (BoException e){
            return ResultVo.error(e.getMessage());
        }catch (Exception e){
            log.error("add error data:"+dto,e);
            return ResultVo.error(e.getMessage());
        }
    }

    @PostMapping("/del")
    public ResultVo del(HttpServletRequest request,Integer id){
        try{
            Boolean boo =  operaterInfoService.del(id);
            if(boo){
                return ResultVo.success();
            }else{
                return ResultVo.error("未知原因，删除失败");
            }
        }catch (BoException e){
            return ResultVo.error(e.getMessage());
        }catch (Exception e){
            log.error("del error data:"+id,e);
            return ResultVo.error("系统异常");
        }
    }

}
