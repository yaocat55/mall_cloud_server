package cn.net.mall.pay.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对账批次状态枚举（对应 recon_batch.status）.
 */
@AllArgsConstructor
@Getter
@Schema(description = "对账批次状态枚举", allowableValues = {"1", "2", "3", "4", "5", "6", "7"})
public enum ReconStatusEnum {

    /** 1 已下载：对账单已落 MinIO */
    DOWNLOADED(1, "已下载"),

    /** 2 已解析入库：对账单已解析并写入 recon_temp */
    PARSED(2, "已解析入库"),

    /** 3 对账中 */
    RECONCILING(3, "对账中"),

    /** 4 对账完成：无差异，对平 */
    COMPLETED(4, "对账完成"),

    /** 5 有差异待处理 */
    HAS_DIFF(5, "有差异待处理"),

    /** 6 差异已处理 */
    DIFF_HANDLED(6, "差异已处理"),

    /** 7 失败 */
    FAILED(7, "失败");

    private final Integer value;
    private final String desc;

    public static ReconStatusEnum from(Integer value) {
        if (value == null) {
            return null;
        }
        for (ReconStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
