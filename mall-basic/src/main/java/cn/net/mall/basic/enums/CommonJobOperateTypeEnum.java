package cn.net.mall.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 动态JOB操作类型枚举
 *
 * @date 2024/8/4 下午3:39
 */
@AllArgsConstructor
@Getter
public enum CommonJobOperateTypeEnum {

    /**
     * 新增
     */
    NEW(1, "新增"),

    /**
     * 修改
     */
    UPDATE(2, "修改"),

    /**
     * 修改
     */
    DELETE(3, "删除"),

    /**
     * 立即执行
     */
    RUN_NOW(4, "立即执行"),

    /**
     * 暂停
     */
    PAUSE(5, "暂停"),

    /**
     * 恢复
     */
    RESUME(6, "恢复");

    private Integer value;

    private String desc;
}
