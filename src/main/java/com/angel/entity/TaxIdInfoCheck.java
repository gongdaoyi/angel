package com.angel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

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

    @Override
    public String toString() {
        return "TaxIdInfoCheck{" +
                "nationality='" + nationality + '\'' +
                ", nationalityName='" + nationalityName + '\'' +
                ", isIndividual='" + isIndividual + '\'' +
                ", isAutoAssign='" + isAutoAssign + '\'' +
                ", enCheckBits='" + enCheckBits + '\'' +
                '}';
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationalityName() {
        return nationalityName;
    }

    public void setNationalityName(String nationalityName) {
        this.nationalityName = nationalityName;
    }

    public String getIsIndividual() {
        return isIndividual;
    }

    public void setIsIndividual(String isIndividual) {
        this.isIndividual = isIndividual;
    }

    public String getIsAutoAssign() {
        return isAutoAssign;
    }

    public void setIsAutoAssign(String isAutoAssign) {
        this.isAutoAssign = isAutoAssign;
    }

    public String getEnCheckBits() {
        return enCheckBits;
    }

    public void setEnCheckBits(String enCheckBits) {
        this.enCheckBits = enCheckBits;
    }
}
