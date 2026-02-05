package com.angel.controller;

import com.angel.entity.Roles;
import com.angel.service.IRolesService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/xlsx")
public class XlsxController {

    @Autowired
    IRolesService rolesService;

    @PostMapping("/checkRights")
    public void checkRights() {
        String oldFile = "C:\\A-Work\\NBOP\\testDir\\场景3生产差异数据.xlsx";
        String newFile = "C:\\A-Work\\NBOP\\testDir\\场景3生产差异数据(对比后).xlsx";

        try (FileInputStream fis = new FileInputStream(oldFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);  // 获取第一个工作表

            // 获取第一行，以便检查列标题（可选）
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                System.out.println("列标题: " +
                        headerRow.getCell(0).getStringCellValue() + ", " +  // 第一列标题
                        headerRow.getCell(1).getStringCellValue() + ", " +  // 第二列标题
                        headerRow.getCell(2).getStringCellValue() + ", " +  // 第三列标题
                        headerRow.getCell(3).getStringCellValue());         // 第四列标题
            }

            // 遍历每一行，从第二行开始（跳过标题行）
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();  // 跳过第一行（标题行）

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell menuIdCell = row.getCell(0);
                Cell serviceIdCell = row.getCell(3);
                Cell menuRightsCell = row.getCell(4);
                Cell serviceRightsCell = row.getCell(5);
                Cell remarkCell = row.getCell(8);

                if (menuRightsCell != null && serviceRightsCell != null) {
                    double menuId = menuIdCell.getNumericCellValue();
                    double serviceId = serviceIdCell.getNumericCellValue();
                    String menuRights = menuRightsCell.getStringCellValue();
                    String serviceRights = serviceRightsCell.getStringCellValue();

                    List<Integer> roleIds = compareStrings(menuRights, serviceRights);

                    StringBuilder remark = new StringBuilder();
                    remark.append("菜单").append((int) menuId);
                    remark.append("比功能号").append((int) serviceId);
                    remark.append("多了角色：");

                    List<String> rolesList = new ArrayList<>();
                    for (Integer roleId : roleIds) {
                        QueryWrapper<Roles> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("role_id", roleId);
                        Roles roles = rolesService.getOne(queryWrapper);
                        if (roles != null) {
                            rolesList.add(roleId + roles.getRoleName());
                        } else {
                            rolesList.add(roleId.toString());
                        }
                    }

                    rolesList.forEach(role -> remark.append(role).append("、"));

                    if (remarkCell == null) {
                        remarkCell = row.createCell(8);  // 如果单元格为空，创建该单元格
                    }
                    remarkCell.setCellValue(remark.toString());

                    if (remarkCell == null) {
                        remarkCell = row.createCell(8);  // 如果单元格为空，创建该单元格
                    }
                    remarkCell.setCellValue(remark.toString());
                }
            }

            // 保存修改后的工作簿到输出文件
            try (FileOutputStream fos = new FileOutputStream(newFile)) {
                workbook.write(fos);
            }

            System.out.println("处理完成，结果保存到 " + newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> compareStrings(String a, String b) {
        List<Integer> diffIndexes = new ArrayList<>();

        // 检查两个字符串的长度是否一致
        int length = Math.min(a.length(), b.length());

        // 比较每一位
        for (int i = 0; i < length; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                diffIndexes.add(i + 1);  // 记录不同的位置
            }
        }

        return diffIndexes;
    }
}
