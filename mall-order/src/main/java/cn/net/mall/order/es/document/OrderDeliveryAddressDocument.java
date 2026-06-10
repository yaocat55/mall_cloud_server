package cn.net.mall.order.es.document;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class OrderDeliveryAddressDocument {

    @Field(type = FieldType.Long)
    private Long orderId;

    @Field(type = FieldType.Keyword)
    private String receiverName;

    @Field(type = FieldType.Keyword)
    private String receiverPhone;

    @Field(type = FieldType.Keyword)
    private String receiverProvince;

    @Field(type = FieldType.Keyword)
    private String receiverCity;

    @Field(type = FieldType.Keyword)
    private String receiverRegion;

    @Field(type = FieldType.Keyword)
    private String receiverDetailAddress;

    @Field(type = FieldType.Keyword)
    private String postCode;
}
