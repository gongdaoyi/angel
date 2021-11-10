package com.angel.controller;

import com.angel.entity.TaxIdInfoCheck;
import com.angel.model.ExcelDto;
import com.angel.service.ITaxIdInfoCheckService;
import com.angel.utils.EasyPoiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/import")
public class TaxIdInfoCheckController {

    private static final Logger logger = LoggerFactory.getLogger(TaxIdInfoCheckController.class);

    @Autowired
    ITaxIdInfoCheckService taxIdInfoCheckService;

    @PostMapping("/excel")
    public String importExcel(@RequestParam(value = "file") MultipartFile file) {

        List<ExcelDto> excelDtoList = EasyPoiUtils.importExcel(file, 0, 1, ExcelDto.class);
        logger.info("导入数据{}行", excelDtoList.size());

        List<TaxIdInfoCheck> saves = new ArrayList<>();

        excelDtoList.forEach(v -> {
            TaxIdInfoCheck taxIdInfoCheckUser = new TaxIdInfoCheck();
            taxIdInfoCheckUser.setNationality(v.getNationality());
            taxIdInfoCheckUser.setNationalityName(v.getNationalityName());
            taxIdInfoCheckUser.setIsIndividual("1");
            taxIdInfoCheckUser.setIsAutoAssign(v.getUser());
            taxIdInfoCheckUser.setEnCheckBits(v.getUserSize());
            saves.add(taxIdInfoCheckUser);

            TaxIdInfoCheck taxIdInfoCheckOrg = new TaxIdInfoCheck();
            taxIdInfoCheckOrg.setNationality(v.getNationality());
            taxIdInfoCheckOrg.setNationalityName(v.getNationalityName());
            taxIdInfoCheckOrg.setIsIndividual("0");
            taxIdInfoCheckOrg.setIsAutoAssign(v.getOrg());
            taxIdInfoCheckOrg.setEnCheckBits(v.getOrgSize());
            saves.add(taxIdInfoCheckOrg);
        });

        logger.info("插入数据{}行", saves.size());
        try {
            taxIdInfoCheckService.saveBatch(saves);
        } catch (Exception e) {
            logger.error("批量插入异常：{}", e);
        }

        return "succeed";
    }
}
