package cn.net.mall.message.entity;

import cn.net.mall.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonNotifyEntity extends BaseEntity {
    private Integer type;
    private String title;
    private String content;
    private String linkUrl;
    private Integer readStatus;
    private Long toUserId;
    private Integer isPush;
}
