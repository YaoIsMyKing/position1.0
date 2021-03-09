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
            throw new BoException(-1,"未知操作");
        }
        return true;
    }

    private void bus(TbOperaterInfo entity) throws Exception {
        //持仓记录
        TbPosition position = positionService.getByCode2AndUserId(entity.getUserId(),entity.getCode2());
        String now_date = DateUtils.getCurrDateStr();
        if(position == null){
            position = new TbPosition();
            BeanUtils.copyProperties(entity,position);
            position.setCreateTime(now_date);
        }else {
            //计算市值
            //position.setMarketVal(position.getMarketVal().add(entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount()))));
        }
        //设置成交金额
        BigDecimal makeMoney = entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount())) ;
        //增加手续费 手续费不加入成交金额里面好看一点
        BigDecimal myFit = makeMoney.multiply(fit);
        //makeMoney = makeMoney.add(myFit);
        entity.setMakeMoney(makeMoney);
        entity.setFit(myFit);
        //设置剩余股票数量
        entity.setSurplusCount(entity.getCount());
        position.setUpdateTime(DateUtils.getCurrDateStr());

        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try{
            positionService.saveOrUpdate(position);
            this.save(entity);
            dataSourceTransactionManager.commit(transactionStatus);//提交
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
            throw e;
        }
    }

    private void sale(TbOperaterInfo entity) throws Exception {
        if(entity.getParentId() == null){
            throw new BoException(-1,"卖出必须关联买入操作");
        }
        //查询关联记录
        TbOperaterInfo info = this.getById(entity.getParentId());
        if(info == null){
            throw new BoException(-1,"关联记录不存在");
        }
        if(info.getSurplusCount() <= 0){
            throw new BoException(-1,"剩余可操作股票数量不足");
        }
        //持仓记录
        TbPosition position = positionService.getByCode2AndUserId(entity.getUserId(),entity.getCode2());
        if(null == position){
            throw new BoException(-1,"未查询到持仓");
        }
        //计算剩余股票数量
        Integer count = info.getSurplusCount() - entity.getCount() ;
        //设置关联记录与当前记录 股票数量状态
        if(count > 0){
            info.setStatus(2);
            entity.setStatus(2);
        }else if(count == 0){
            info.setStatus(1);
            entity.setStatus(1);
        }else{
            throw new BoException(-1,"剩余可操作股票数量不足");
        }
        //设置关联记录与当前记录 剩余股数数
        info.setSurplusCount(count);
        //计算本次卖出盈利 卖出总金额  - 买入总金额
        //卖出总金额
        BigDecimal salePrice = entity.getOperaterPrice() .multiply( new BigDecimal(entity.getCount()));
        //手续费不计入成交金额
        //salePrice = salePrice.subtract(salePrice.multiply(fit));//扣除手续费
        //买入的总金额
        BigDecimal busPrice = info.getOperaterPrice() .multiply( new BigDecimal(entity.getCount()));
        //手续费不计入成交金额
        //busPrice = busPrice.add(busPrice.multiply(fit));//加上手续费

        BigDecimal profit = salePrice.subtract(busPrice);

        //设置关联记录与当前记录 盈利
        info.setProfit(info.getProfit().add(profit));   //关联的买入记录需要累加
        entity.setProfit(profit); //当前卖出记录直接设置就好
        //设置成交金额
        BigDecimal makeMoney = entity.getOperaterPrice().multiply(new BigDecimal(entity.getCount())) ;
        //扣除手续费
        BigDecimal myFit = makeMoney.multiply(fit);
        //手续费不计入成交金额
        //makeMoney = makeMoney.subtract(myFit);
        entity.setMakeMoney(makeMoney);
        entity.setFit(myFit);
        //设置当前记录操作时间
        Date now = new Date();
        String now_date = DateUtil.format(now,"yyyy-MM-dd HH:mm:ss");
        entity.setCreateTime(now_date);
        entity.setUpdateTime(now_date);

        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try{
            positionService.updateById(position);
            this.updateById(info);
            this.save(entity);
            dataSourceTransactionManager.commit(transactionStatus);//提交
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
            throw e;
        }

    }

    @Override
    public Boolean del(Integer id) throws Exception {
        TbOperaterInfo entity = this.getById(id);
        if(entity == null){
            throw new BoException(-1,"不存在的记录");
        }
        if(entity.getOperater() == 1){
            //如果是买入  把关联的卖出记录也全都是删除掉
            delBus(entity);
        }else if(entity.getOperater() == 2){
            delSale(entity);
        }else{
            throw new BoException(-1,"异常的记录");
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
        //关联的买入记录
        TbOperaterInfo info = this.getById(entity.getParentId());
        if(info != null){
            //还原剩余股票数量
            Integer count = entity.getCount() + info.getSurplusCount();
            //设置关联记录与当前记录 股票数量状态
            if(count >= info.getCount()){
                info.setStatus(0);
            }else{
                info.setStatus(2);
            }
            //设置关联记录与当前记录 剩余股数数
            info.setSurplusCount(count);
            //还原本次卖出盈利
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
            dataSourceTransactionManager.commit(transactionStatus);//提交
        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);//最好是放在catch 里面,防止程序异常而事务一直卡在哪里未提交
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