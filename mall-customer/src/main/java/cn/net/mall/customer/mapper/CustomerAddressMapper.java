package cn.net.mall.customer.mapper;

import cn.net.mall.customer.entity.CustomerAddressEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerAddressMapper {
    CustomerAddressEntity findById(Long id);
    List<CustomerAddressEntity> findByCustomerId(@Param("customerId") Long customerId);
    List<CustomerAddressEntity> findByIds(@Param("ids") List<Long> ids);
    int insert(CustomerAddressEntity entity);
    int update(CustomerAddressEntity entity);
    int deleteByIds(@Param("ids") List<Long> ids);
}
