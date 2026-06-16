package cn.net.mall.marketing.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValidStatusEnum {

    VALID(1, "有效"),
    INVALID(0, "无效");

    private Integer value;
    private String desc;
}
