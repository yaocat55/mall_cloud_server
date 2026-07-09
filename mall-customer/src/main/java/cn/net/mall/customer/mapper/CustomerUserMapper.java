package cn.net.mall.customer.mapper;

import cn.net.mall.customer.entity.CustomerUserEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerUserMapper {
    CustomerUserEntity findById(Long id);
    CustomerUserEntity findByPhone(@Param("phone") String phone);
    List<CustomerUserEntity> findByIds(@Param("ids") List<Long> ids);
    int insert(CustomerUserEntity entity);
    int update(CustomerUserEntity entity);
}
