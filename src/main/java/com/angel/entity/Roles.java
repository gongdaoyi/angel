package com.angel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * <p>映射数据库roles表，存储系统角色信息</p>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@TableName("roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Roles extends Model<Roles> {

    public static final String TABLE_NAME = "roles";
    public static final String FIELD_ROLE_ID = "role_id";
    public static final String FIELD_ROLE_KIND = "role_kind";
    public static final String FIELD_ROLE_NAME = "role_name";

    @TableField(FIELD_ROLE_ID)
    private String roleId;

    @TableField(FIELD_ROLE_KIND)
    private String roleKind;

    @TableField(FIELD_ROLE_NAME)
    private String roleName;
}
