package com.example.position.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.position.dao.OperaterInfoDao;
import com.example.position.dto.AddOperaterInfoDto;
import com.example.position.dto.OperaterInfoDto;
import com.example.position.entity.TbOperaterInfo;
import com.example.position.entity.TbPosition;
import com.example.position.service.OperaterInfoService;
import com.example.position.service.PositionService;
import com.example.position.utils.DateUtils;
import com.example.position.vo.BoException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service("operaterInfoService")
public class OperaterInfoServiceImpl extends ServiceImpl<OperaterInfoDao, TbOperaterInfo> implements OperaterInfoService {

    private Logger logger = LoggerFactory.getLogger(OperaterInfoServiceImpl.class);

    public static final BigDecimal fit = new BigDecimal(2.5).divide(new BigDecimal(10000));

    @Autowired
    private PositionService positionService;
    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;
    @Override
    public Page<TbOperaterInfo> listPage(OperaterInfoDto dto) {
        Wrapper wrapper = new QueryWrapper<TbOperaterInfo>().lambda()
                .eq(TbOperaterInfo::getUserId, dto.getUserId())
                .eq(StringUtils.isNotBlank(dto.getCode2()), TbOperaterInfo::getCode2, dto.getCode2())
                .eq(dto.getOperater() != null, TbOperaterInfo::getOperater, dto.getOperater())
                .eq(dto.getStatus() != null, TbOperaterInfo::getStatus, dto.getStatus())
                .ge(StringUtils.isNotBlank(dto.getStartDate()),TbOperaterInfo::getDateTime,dto.getStartDate())
                .le(StringUtils.isNotBlank(dto.getEndDate()),TbOperaterInfo::getDateTime,dto.getEndDate())
                .eq(TbOperaterInfo::getFlag,1)
                .orderByDesc(TbOperaterInfo::getDateTime);
        Page<TbOperaterInfo> page = new Page(dto.getPage(),dto.getLimit());
        Page<TbOperaterInfo> resPage = this.page(page, wrapper);
        return resPage;
    }

    @Override
    public List<TbOperaterInfo> list(OperaterInfoDto dto) throws Exception {
        Wrapper wrapper = new QueryWrapper<TbOperaterInfo>().lambda()
                .eq(TbOperaterInfo::getUserId, dto.getUserId())
                .eq(dto.getCode2() != null, TbOperaterInfo::getCode2, dto.getCode2())
                .eq(dto.getOperater() != null, TbOperaterInfo::getOperater, dto.getOperater())
                .eq(dto.getStatus() != null, TbOperaterInfo::getStatus, dto.getStatus())
                .eq(TbOperaterInfo::getFlag,1);
        List<TbOperaterInfo> list = this.list( wrapper);
        return list;
    }

    @Override
    public Boolean add(AddOperaterInfoDto dto) throws Exception {
        TbOperaterInfo entity = new TbOperaterInfo();
        BeanUtils.copyProperties(dto,entity);
        if(dto.getOperater() == 2){
            sale(entity);
        }else if(dto.getOperater() == 1){
            bus(entity);
        }else {
            throw new BoException(-1,"????????????");
        }
        return true;
    }

    private void bus(TbOperaterInfo entity) throws Exception {
        //????????????
        TbPosition position = positionService.getByCode2AndUserId(entity.getUserId(),entity.getCode2());
        String now_date = DateUtils.getCurrDateStr();
        if(position == null){
            position = new TbPosition();
            BeanUtils.copyProperties(entity,position);
            position.setCreateTime(now_date);
        }else {
            //????????????
            //position.setMarketVal(position.getMarketVal().add(entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount()))));
        }
        //??????????????????
        BigDecimal makeMoney = entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount())) ;
        //??????????????? ????????????????????????????????????????????????
        BigDecimal myFit = makeMoney.multiply(fit);
        //makeMoney = makeMoney.add(myFit);
        entity.setMakeMoney(makeMoney);
        entity.setFit(myFit);
        //????????????????????????
        entity.setSurplusCount(entity.getCount());
        position.setUpdateTime(DateUtils.getCurrDateStr());

        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try{
            positionService.saveOrUpdate(position);
            this.save(entity);
            dataSourceTransactionManager.commit(transactionStatus);//??????
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//???????????????catch ??????,??????????????????????????????????????????????????????
            throw e;
        }
    }

    private void sale(TbOperaterInfo entity) throws Exception {
        if(entity.getParentId() == null){
            throw new BoException(-1,"??????????????????????????????");
        }
        //??????????????????
        TbOperaterInfo info = this.getById(entity.getParentId());
        if(info == null){
            throw new BoException(-1,"?????????????????????");
        }
        if(info.getSurplusCount() <= 0){
            throw new BoException(-1,"?????????????????????????????????");
        }
        //????????????
        TbPosition position = positionService.getByCode2AndUserId(entity.getUserId(),entity.getCode2());
        if(null == position){
            throw new BoException(-1,"??????????????????");
        }
        //????????????????????????
        Integer count = info.getSurplusCount() - entity.getCount() ;
        //????????????????????????????????? ??????????????????
        if(count > 0){
            info.setStatus(2);
            entity.setStatus(2);
        }else if(count == 0){
            info.setStatus(1);
            entity.setStatus(1);
        }else{
            throw new BoException(-1,"?????????????????????????????????");
        }
        //????????????????????????????????? ???????????????
        info.setSurplusCount(count);
        //???????????????????????? ???????????????  - ???????????????
        //???????????????
        BigDecimal salePrice = entity.getOperaterPrice() .multiply( new BigDecimal(entity.getCount()));
        //??????????????????????????????
        //salePrice = salePrice.subtract(salePrice.multiply(fit));//???????????????
        //??????????????????
        BigDecimal busPrice = info.getOperaterPrice() .multiply( new BigDecimal(entity.getCount()));
        //??????????????????????????????
        //busPrice = busPrice.add(busPrice.multiply(fit));//???????????????

        BigDecimal profit = salePrice.subtract(busPrice);

        //????????????????????????????????? ??????
        info.setProfit(info.getProfit().add(profit));   //?????????????????????????????????
        entity.setProfit(profit); //????????????????????????????????????
        //??????????????????
        BigDecimal makeMoney = entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount())) ;
        //???????????????
        BigDecimal myFit = makeMoney.multiply(fit);
        //??????????????????????????????
        //makeMoney = makeMoney.subtract(myFit);
        entity.setMakeMoney(makeMoney);
        entity.setFit(myFit);
        //??????????????????????????????
        Date now = new Date();
        String now_date = DateUtil.format(now,"yyyy-MM-dd HH:mm:ss");
        entity.setCreateTime(now_date);
        entity.setUpdateTime(now_date);

        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try{
            positionService.updateById(position);
            this.updateById(info);
            this.save(entity);
            dataSourceTransactionManager.commit(transactionStatus);//??????
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//???????????????catch ??????,??????????????????????????????????????????????????????
            throw e;
        }

    }

    @Override
    public Boolean del(Integer id) throws Exception {
        TbOperaterInfo entity = this.getById(id);
        if(entity == null){
            throw new BoException(-1,"??????????????????");
        }
        if(entity.getOperater() == 1){
            //???????????????  ?????????????????????????????????????????????
            delBus(entity);
        }else if(entity.getOperater() == 2){
            delSale(entity);
        }else{
            throw new BoException(-1,"???????????????");
        }
        return true;
    }

    private void delBus(TbOperaterInfo entity)throws Exception{
        Wrapper updateWrapper = new UpdateWrapper<TbOperaterInfo>().lambda()
                .set(TbOperaterInfo::getFlag , 0)
                .eq(TbOperaterInfo::getId,entity.getId())
                .or()
                .eq(TbOperaterInfo::getParentId,entity.getId());
        this.update(updateWrapper);
    }

    private void delSale(TbOperaterInfo entity)throws Exception{
        //?????????????????????
        TbOperaterInfo info = this.getById(entity.getParentId());
        if(info != null){
            //????????????????????????
            Integer count = entity.getCount() + info.getSurplusCount();
            //????????????????????????????????? ??????????????????
            if(count >= info.getCount()){
                info.setStatus(0);
            }else{
                info.setStatus(2);
            }
            //????????????????????????????????? ???????????????
            info.setSurplusCount(count);
            //????????????????????????
            info.setProfit(info.getProfit().subtract(entity.getProfit()));
            info.setUpdateTime(DateUtils.getCurrDateStr());
        }

        entity.setUpdateTime(DateUtils.getCurrDateStr());
        entity.setFlag(0);
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try{
            this.updateById(entity);
            if(info != null){
                this.updateById(info);
            }
            dataSourceTransactionManager.commit(transactionStatus);//??????
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//???????????????catch ??????,??????????????????????????????????????????????????????
            throw e;
        }
    }

    @Override
    public Integer countByUserIdAndCode(Integer userId,String code2){
        Wrapper wrapper = new QueryWrapper<TbOperaterInfo>().lambda()
                .eq(TbOperaterInfo::getUserId,userId)
                .eq(TbOperaterInfo::getCode2,code2)
                .eq(TbOperaterInfo::getFlag,1);
        List<TbOperaterInfo> list = this.list(wrapper);
        Integer count = 0;
        for (TbOperaterInfo tbOperaterInfo : list) {
            if(tbOperaterInfo.getOperater() == 1){
                count = count+tbOperaterInfo.getSurplusCount();
            }
        }
        return count;
    }

}