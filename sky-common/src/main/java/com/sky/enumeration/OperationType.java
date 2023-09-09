package com.sky.enumeration;

/**
 * 需要统一填充功能字段的数据库操作类型
 *  只有更新 插入需要填充
 */
public enum OperationType {

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT

}
