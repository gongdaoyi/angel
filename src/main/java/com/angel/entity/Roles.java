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
 * <p>
 * 字段说明：
 * <ul>
 *   <li>roleId：角色ID，主键</li>
 *   <li>roleKind：角色种类</li>
 *   <li>roleName：角色名称</li>
 * </ul>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@TableName("roles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Roles extends Model<Roles> {

    /**
     * 数据库表名
     */
    public static final String TABLE_NAME = "roles";

    /**
     * 角色ID字段名
     */
    public static final String FIELD_ROLE_ID = "role_id";

    /**
     * 角色种类字段名
     */
    public static final String FIELD_ROLE_KIND = "role_kind";

    /**
     * 角色名称字段名
     */
    public static final String FIELD_ROLE_NAME = "role_name";

    /**
     * 角色ID
     * 对应数据库字段：role_id
     */
    @TableField(FIELD_ROLE_ID)
    private String roleId;

    /**
     * 角色种类
     * 对应数据库字段：role_kind
     */
    @TableField(FIELD_ROLE_KIND)
    private String roleKind;

    /**
     * 角色名称
     * 对应数据库字段：role_name
     */
    @TableField(FIELD_ROLE_NAME)
    private String roleName;

    @Override
    public String toString() {
        return "Roles{" +
                "roleId='" + roleId + '\'' +
                ", roleKind='" + roleKind + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleKind() {
        return roleKind;
    }

    public void setRoleKind(String roleKind) {
        this.roleKind = roleKind;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
