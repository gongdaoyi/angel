package com.angel.dict;

public enum ActionType {
    /**
     * 新增操作
     */
    Insert(1, "新增"),

    /**
     * 修改操作
     */
    Update(2, "修改"),

    /**
     * 删除操作
     */
    Delete(3, "删除");

    private final int code;
    private final String note;

    ActionType(int code, String note) {
        this.code = code;
        this.note = note;
    }

    public int getCode() {
        return code;
    }

    public String getNote() {
        return note;
    }
}
