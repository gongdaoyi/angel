package com.angel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.angel.mapper.DictMapper;
import com.angel.service.IDictService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@Log4j2
@Service
public class DictServiceImpl implements IDictService {

    @Autowired
    DictMapper dictMapper;

    @Override
    public void generateDict() {
        // Excel文件路径
        String excelPath = "C:\\A-Work\\智能分拣-不匹配原因-图片标题_识别结果类-202604.xlsx";

        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();

            log.info("开始处理Excel文件，共 {} 行数据", rowCount);

            // 从第2行开始遍历（跳过表头）
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                // D列(索引3) -> 查询字典 -> E列(索引4)
                String dArchNo = getCellValue(row.getCell(3));
                if (dArchNo != null && !dArchNo.isEmpty()) {
                    JSONObject dictInfo = dictMapper.qryDict(dArchNo.trim());
                    if (dictInfo != null && dictInfo.getString("dict_prompt") != null) {
                        createOrUpdateCell(row, 4, dictInfo.getString("dict_prompt"));
                        log.debug("第{}行D列[{}]查询结果: {}", i + 1, dArchNo, dictInfo.getString("dict_prompt"));
                    }
                }

                // F列(索引5) -> 查询字典 -> G列(索引6)
                String fArchNo = getCellValue(row.getCell(5));
                if (fArchNo != null && !fArchNo.isEmpty()) {
                    JSONObject dictInfo = dictMapper.qryDict(fArchNo.trim());
                    if (dictInfo != null && dictInfo.getString("dict_prompt") != null) {
                        createOrUpdateCell(row, 6, dictInfo.getString("dict_prompt"));
                        log.debug("第{}行F列[{}]查询结果: {}", i + 1, fArchNo, dictInfo.getString("dict_prompt"));
                    }
                }
            }

            // 保存修改后的文件
            try (FileOutputStream fos = new FileOutputStream(excelPath)) {
                workbook.write(fos);
            }

            log.info("Excel文件处理完成，已保存到: {}", excelPath);

        } catch (Exception e) {
            log.error("处理Excel文件失败", e);
            throw new RuntimeException("处理Excel文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取单元格字符串值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 创建或更新单元格
     */
    private void createOrUpdateCell(Row row, int cellIndex, String value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        cell.setCellValue(value);
    }

}
