package com.example.position.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.position.dao.PositionDao;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.dto.PositionDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.entity.TbPosition;
import com.example.position.service.OperaterInfoService;
import com.example.position.service.PositionService;
import com.example.position.utils.DateUtils;
import com.example.position.vo.BoException;
import com.example.position.vo.PositionVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service("positionService")
public class PositionServiceImpl extends ServiceImpl<PositionDao, TbPosition> implements PositionService {

    private Logger logger = LoggerFactory.getLogger(PositionServiceImpl.class);

    @Autowired
    private OperaterInfoService operaterInfoService;

    private List<TbPosition> list(PositionDto dto)throws Exception{
        Wrapper wrapper = new QueryWrapper<TbPosition>().lambda()
                .eq(TbPosition::getUserId, dto.getUserId())
                .eq(dto.getCode2() != null, TbPosition::getCode2, dto.getCode2())
                .eq(TbPosition::getFlag,1);
        List<TbPosition>  list = this.list(wrapper);
        return list;
    }

    @Override
    public List<PositionVo> getList(PositionDto dto) throws Exception {
        List<TbPosition> list = list(dto);
        List<PositionVo> listVo = new ArrayList<>();
        for (TbPosition tbPosition : list) {
            PositionVo vo = new PositionVo();
            BeanUtils.copyProperties(tbPosition,vo);
            OperaterInfoDto operaterInfoDto = new OperaterInfoDto();
            operaterInfoDto.setUserId(dto.getUserId());
            operaterInfoDto.setCode2(tbPosition.getCode2());
            List<TbOperaterInfo> operaterInfoList = operaterInfoService.list(operaterInfoDto);
            BigDecimal allPut = new BigDecimal(0);//买入
            BigDecimal allOut = new BigDecimal(0);//卖出
            Integer stockCount = 0; //剩余股票数量
            BigDecimal profit = new BigDecimal(0);       //总买卖利润
            BigDecimal fit = new BigDecimal(0);       //总手续费
            BigDecimal maxPrice = new BigDecimal(0);    //最高买入价格
            BigDecimal minPrice = new BigDecimal(0);;    //最低买入价格
            for (TbOperaterInfo en : operaterInfoList) {
                BigDecimal makeMoney = en.getMakeMoney()==null ? BigDecimal.ZERO:en.getMakeMoney();
                if(en.getOperater() == 1){
                    allPut = allPut.add(makeMoney);
                    stockCount = stockCount+en.getSurplusCount();
                    BigDecimal operaterPrice = en.getOperaterPrice();
                    if(operaterPrice.compareTo(maxPrice) > 0){
                        maxPrice = operaterPrice;
                    }
                    if(minPrice.compareTo(BigDecimal.ZERO) == 0 || operaterPrice.compareTo(minPrice) < 0){
                        minPrice = operaterPrice;
                    }
                }else  if(en.getOperater() == 2){
                    allOut = allOut.add(makeMoney);
                    //买卖利润双方都一样的  只统计一边就可以了
                    profit = profit.add(en.getProfit());
                }
                fit.add(en.getFit());
            }
            //计算平均成本  投入的资金减去回收的资金+手续费，得出的资金量再除以手中剩下的股票数量，就是每一股的成本价了。
            BigDecimal avgCost = new BigDecimal(0);
            if(allPut.compareTo(BigDecimal.ZERO)>0 && allOut.compareTo(BigDecimal.ZERO)>0 && stockCount!=0){
                avgCost = (allPut.subtract(allOut)).divide(new BigDecimal(stockCount),4);
            }
            vo.setAvgCost(avgCost);
            //计算账面盈亏   (平均成本*剩余股数) - (投入资金-卖出资金)
            BigDecimal paperProfit = (avgCost.multiply(new BigDecimal(stockCount))).subtract(allPut.subtract(allOut));
            vo.setPaperProfit(paperProfit);
            //计算总剩余股票数量
            vo.setCount(stockCount);
            //计算总利润
            vo.setProfit(profit);
            //计算总手续费
            vo.setFit(fit);
            vo.setMaxPrice(maxPrice);
            vo.setMinPrice(minPrice);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public Boolean del(Integer id) throws Exception {
        TbPosition entity = this.getById(id);

        Integer count = operaterInfoService.countByUserIdAndCode(entity.getUserId(),entity.getCode2());
        if(count >0){
            throw new BoException(-1,"该产品未清仓，无法删除");
        }
        Wrapper wrapper = new UpdateWrapper<TbPosition>().lambda()
                .set(TbPosition::getFlag,0)
                .eq(TbPosition::getId,id);
        Boolean update = this.update(wrapper);
        return update;
    }

    @Override
    public List<TbPosition> getListName(Integer userId) throws Exception {
        PositionDto dto = new PositionDto();
        dto.setUserId(userId);
        List<TbPosition> list = list(dto);
        return list;
    }

    @Override
    public TbPosition getByCode2AndUserId(Integer userId, String code2) throws Exception {
        Wrapper wrapper = new QueryWrapper<TbPosition>().lambda()
                .eq(TbPosition::getUserId, userId)
                .eq( TbPosition::getCode2, code2)
                .eq(TbPosition::getFlag,1)
                .last("limit 1");
        return this.getOne(wrapper,false);
    }

    @Override
    public Boolean add(PositionDto dto) throws Exception {
        TbPosition entity = new TbPosition();
        BeanUtils.copyProperties(dto,entity);
        String date = DateUtils.getCurrDateStr();
        TbPosition info = getByUserIdAndCode(entity.getUserId(), entity.getCode1(), entity.getCode2());
        if(info != null ){
            if(info.getFlag() == 0){
                info.setFlag(1);
                info.setUpdateTime(date);
                this.updateById(info);
                return true;
            }else{
                throw new BoException(-1,"记录已存在");
            }
        }
        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        this.save(entity);
        return true;
    }

    private TbPosition getByUserIdAndCode(Integer userId,String code1,String code2){
        Wrapper wrapper = new QueryWrapper<TbPosition>().lambda()
                .eq(TbPosition::getUserId,userId)
                .eq(StringUtils.isNotBlank(code1),TbPosition::getCode1,code1)
                .eq(TbPosition::getCode2,code2);
        return this.getOne(wrapper);
    }
}