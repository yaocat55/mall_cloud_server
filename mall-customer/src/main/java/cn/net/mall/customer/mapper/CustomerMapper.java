package cn.net.mall.customer.mapper;

import cn.net.mall.customer.entity.CustomerEntity;
import org.apache.ibatis.annotations.Param;

public interface CustomerMapper {
    CustomerEntity findById(Long id);
    CustomerEntity findByPhone(@Param("phone") String phone);
    int insert(CustomerEntity entity);
    int update(CustomerEntity entity);
}
