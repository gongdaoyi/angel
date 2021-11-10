package com.angel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("taxidinfocheck")
public class TaxIdInfoCheck extends Model<TaxIdInfoCheck> {

    @TableField("nationality")
    private String nationality;

    @TableField("nationality_name")
    private String nationalityName;

    @TableField("is_individual")
    private String isIndividual;

    @TableField("is_auto_assign")
    private String isAutoAssign;

    @TableField("en_check_bits")
    private String enCheckBits;
}
