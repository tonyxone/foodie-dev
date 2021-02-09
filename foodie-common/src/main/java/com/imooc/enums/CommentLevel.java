package com.imooc.enums;

/**
 * @Desc: 商品评价等级 枚举
 */
public enum CommentLevel {
    GOOD(1, "否"),
    NORMAL(2, "是"),
    BAD(3,"差评");

    public final Integer type;
    public final String value;

    CommentLevel(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
