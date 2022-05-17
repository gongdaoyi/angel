package com.angel.service.impl;

import com.angel.entity.TaxIdInfoCheck;
import com.angel.mapper.TaxIdInfoCheckMapper;
import com.angel.service.ITaxIdInfoCheckService;
import com.angel.utils.RedisUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaxIdInfoCheckServiceImpl extends ServiceImpl<TaxIdInfoCheckMapper, TaxIdInfoCheck> implements ITaxIdInfoCheckService {

    private static final Logger log = LoggerFactory.getLogger(TaxIdInfoCheckServiceImpl.class);

    @Autowired
    RedisUtils redisUtils;

    @Transactional
    public boolean updateTaxIdInfoCheck(String nationalityName) {
        try {
            this.update(Wrappers.<TaxIdInfoCheck>lambdaUpdate()
                    .set(TaxIdInfoCheck::getNationalityName, nationalityName)
                    .eq(TaxIdInfoCheck::getNationality, "CHN")
                    .eq(TaxIdInfoCheck::getIsIndividual, 1));

            String[] split = "".split("/");
            System.out.println(split[2]);
        } catch (Exception e) {
            String message = e.getMessage();
            log.error(message);

            // 更新失败 - fallBack
            throw new RuntimeException(e);
        }

        return true;
    }
}
