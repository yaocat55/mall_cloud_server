package cn.net.mall.order.es.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单ES文档
 */
@Data
@Document(indexName = "order")
public class OrderDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String code;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String userName;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date orderTime;

    @Field(type = FieldType.Integer)
    private Integer orderStatus;

    @Field(type = FieldType.Integer)
    private Integer payStatus;

    @Field(type = FieldType.Double)
    private BigDecimal totalAmount;

    @Field(type = FieldType.Double)
    private BigDecimal paymentAmount;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String remark;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createTime;

    @Field(type = FieldType.Nested)
    private List<OrderItemDocument> orderItemList;

    @Field(type = FieldType.Object)
    private OrderDeliveryAddressDocument orderDeliveryAddress;
}
