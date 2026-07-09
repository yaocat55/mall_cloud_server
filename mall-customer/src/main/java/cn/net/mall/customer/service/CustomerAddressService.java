package cn.net.mall.customer.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.customer.dto.AddressDTO;
import cn.net.mall.customer.dto.AddressDefaultDTO;
import cn.net.mall.customer.entity.CustomerAddressEntity;
import cn.net.mall.customer.mapper.CustomerAddressMapper;
import cn.net.mall.entity.auth.JwtUserEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerAddressService {

    private final CustomerAddressMapper customerAddressMapper;
    private final IdGenerateHelper idGenerateHelper;

    public CustomerAddressService(CustomerAddressMapper customerAddressMapper,
                                   IdGenerateHelper idGenerateHelper) {
        this.customerAddressMapper = customerAddressMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    private Long getCurrentCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserEntity jwtUser) {
            return jwtUser.getId();
        }
        throw new BusinessException("未登录");
    }

    public List<AddressDTO> getUserAddressList() {
        Long customerId = getCurrentCustomerId();
        List<CustomerAddressEntity> list = customerAddressMapper.findByCustomerId(customerId);
        return list.stream()
                .map(e -> BeanUtil.copyProperties(e, AddressDTO.class))
                .collect(Collectors.toList());
    }

    public AddressDTO getDetail(Long id) {
        CustomerAddressEntity entity = customerAddressMapper.findById(id);
        if (entity == null) return null;
        return BeanUtil.copyProperties(entity, AddressDTO.class);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void save(AddressDTO dto) {
        if (dto.getId() != null) {
            // 更新
            CustomerAddressEntity exist = customerAddressMapper.findById(dto.getId());
            AssertUtil.notNull(exist, "收货地址不存在");
            FillUserUtil.fillUpdateUserInfo(exist);
            BeanUtil.copyProperties(dto, exist);
            customerAddressMapper.update(exist);
        } else {
            // 新增
            Long customerId = getCurrentCustomerId();
            CustomerAddressEntity entity = BeanUtil.copyProperties(dto, CustomerAddressEntity.class);
            entity.setId(idGenerateHelper.nextId());
            entity.setCustomerId(customerId);
            FillUserUtil.fillCreateUserInfo(entity);
            entity.setIsDefault(dto.getIsDefault() != null && dto.getIsDefault());

            // 如果是默认地址，清除旧默认
            if (Boolean.TRUE.equals(entity.getIsDefault())) {
                clearDefault(customerId);
            }
            customerAddressMapper.insert(entity);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public int deleteByIds(List<Long> ids) {
        return customerAddressMapper.deleteByIds(ids);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void setDefaultAddress(AddressDefaultDTO dto) {
        CustomerAddressEntity entity = customerAddressMapper.findById(dto.getId());
        AssertUtil.notNull(entity, "收货地址不存在");

        Long customerId = getCurrentCustomerId();
        if (!customerId.equals(entity.getCustomerId())) {
            throw new BusinessException("无权操作该地址");
        }

        clearDefault(customerId);
        entity.setIsDefault(true);
        FillUserUtil.fillUpdateUserInfo(entity);
        customerAddressMapper.update(entity);
    }

    private void clearDefault(Long customerId) {
        List<CustomerAddressEntity> list = customerAddressMapper.findByCustomerId(customerId);
        for (CustomerAddressEntity e : list) {
            if (Boolean.TRUE.equals(e.getIsDefault())) {
                e.setIsDefault(false);
                FillUserUtil.fillUpdateUserInfo(e);
                customerAddressMapper.update(e);
            }
        }
    }
}
