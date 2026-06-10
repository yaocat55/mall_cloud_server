package cn.net.mall.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OSS类型枚举
 *
 * @date 2024/8/4 下午3:39
 */
@AllArgsConstructor
@Getter
public enum OssTypeEnum {

    /**
     * Minio
     */
    MINIO(1, "Minio"),

    /**
     * 七牛云
     */
    QINIU(2, "七牛云");


    private Integer value;

    private String desc;
}
