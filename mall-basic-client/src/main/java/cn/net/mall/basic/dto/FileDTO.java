package cn.net.mall.basic.dto;

import lombok.Data;

/**
 * 文件对象
 *
 * @date 2024/5/5 下午5:21
 */
@Data
public class FileDTO {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 下载地址
     */
    private String downloadUrl;
}
