package cn.net.mall.marketing.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.entity.seckill.ESSeckillProductEntity;
import cn.net.mall.entity.seckill.SeckillProductDetailEntity;
import cn.net.mall.enums.PhotoTypeEnum;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.marketing.config.BusinessConfig;
import cn.net.mall.marketing.entity.SeckillProductConditionEntity;
import cn.net.mall.marketing.entity.SeckillProductEntity;
import cn.net.mall.marketing.es.EsTemplate;
import cn.net.mall.marketing.mapper.SeckillProductMapper;
import cn.net.mall.product.client.ProductFeignClient;
import cn.net.mall.product.dto.ProductDTO;
import cn.net.mall.product.dto.ProductDetailDTO;
import cn.net.mall.product.dto.ProductPhotoDTO;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.net.mall.constant.KeyConstant.SECKILL_PRODUCT_DETAIL_PFREFIX;
import static cn.net.mall.constant.KeyConstant.SECKILL_PRODUCT_STOCK_PREFIX;

/**
 * 秒杀商品 服务层
 *
 * @date 2024-07-08 10:57:31
 */
@AllArgsConstructor
@Service
public class SeckillProductService extends BaseService<SeckillProductEntity, SeckillProductConditionEntity> {


    private final SeckillProductMapper seckillProductMapper;
    private final EsTemplate esTemplate;
    private final BusinessConfig businessConfig;
    private final TransactionTemplate transactionTemplate;
    private final RedisUtil redisUtil;
    private final ProductFeignClient productFeignClient;

    /**
     * 查询秒杀商品信息
     *
     * @param id 秒杀商品ID
     * @return 秒杀商品信息
     */
    public SeckillProductEntity findById(Long id) {
        return seckillProductMapper.findById(id);
    }

    /**
     * 根据条件分页查询秒杀商品列表
     *
     * @param ESSeckillProductConditionEntity 秒杀商品信息
     * @return 秒杀商品集合
     */
    public ResponsePageEntity<SeckillProductEntity> searchByPage(SeckillProductConditionEntity ESSeckillProductConditionEntity) {
        ResponsePageEntity<SeckillProductEntity> responsePageEntity = super.searchByPage(ESSeckillProductConditionEntity);
        fillProduct(ESSeckillProductConditionEntity, responsePageEntity.getData());
        return responsePageEntity;
    }

    private void fillProduct(SeckillProductConditionEntity ESSeckillProductConditionEntity, List<SeckillProductEntity> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<Long> productIdList = list.stream().map(SeckillProductEntity::getProductId).distinct().collect(Collectors.toList());
        List<ProductDTO> productList = productFeignClient.findByIds(productIdList);
        if (CollectionUtils.isNotEmpty(productList)) {
            for (SeckillProductEntity seckillProductEntity : list) {
                Optional<ProductDTO> optional = productList
                        .stream().filter(x -> x.getId().equals(seckillProductEntity.getProductId())).findAny();
                if (optional.isPresent()) {
                    ProductDTO productEntity = optional.get();
                    seckillProductEntity.setName(productEntity.getName());
                    seckillProductEntity.setModel(productEntity.getModel());
                    seckillProductEntity.setCategoryName(productEntity.getCategoryName());
                    seckillProductEntity.setBrandName(productEntity.getBrandName());
                    seckillProductEntity.setUnitName(productEntity.getUnitName());
                    seckillProductEntity.setCostPrice(productEntity.getPrice());
                }
            }
        }

    }

    /**
     * 新增秒杀商品
     *
     * @param seckillProductEntity 秒杀商品信息
     * @return 结果
     */
    public void insert(SeckillProductEntity seckillProductEntity) {
        checkParam(seckillProductEntity);
        seckillProductMapper.insert(seckillProductEntity);
        syncToESAndRedis(seckillProductEntity);
    }


    private void syncToESAndRedis(SeckillProductEntity seckillProductEntity) {
        List<ProductPhotoDTO> productPhotoEntities = productFeignClient.findByProductIds(Lists.newArrayList(seckillProductEntity.getProductId()));
        ESSeckillProductEntity esSeckillProductEntity = convertESSeckillProductEntity(seckillProductEntity);

        if (CollectionUtils.isNotEmpty(productPhotoEntities)) {
            Optional<ProductPhotoDTO> photoEntityOptional = productPhotoEntities.stream()
                    .filter(x -> (PhotoTypeEnum.COVER.getValue().equals(x.getType()))).findAny();
            if (photoEntityOptional.isPresent()) {
                esSeckillProductEntity.setCover(photoEntityOptional.get().getUrl());
            }
        }
        esTemplate.insertOrUpdate(businessConfig.getSeckillProductEsIndexName(), esSeckillProductEntity);
        syscProductStockToRedis(esSeckillProductEntity);
        syncProductDetailToRedis(productPhotoEntities, esSeckillProductEntity);
    }

    private void syncProductDetailToRedis(List<ProductPhotoDTO> productPhotoEntities, ESSeckillProductEntity esSeckillProductEntity) {
        SeckillProductDetailEntity seckillProductDetailEntity = new SeckillProductDetailEntity();
        BeanUtil.copyProperties(esSeckillProductEntity, seckillProductDetailEntity, false);

        ProductDetailDTO productDetailDTO = productFeignClient.findDetailById(esSeckillProductEntity.getProductId());
        if (Objects.nonNull(productDetailDTO)) {
            seckillProductDetailEntity.setDetail(productDetailDTO.getDetail());
        }

        if (CollectionUtils.isNotEmpty(productPhotoEntities)) {
            List<String> swiper = productPhotoEntities.stream()
                    .filter(x -> (PhotoTypeEnum.SWIPER.getValue().equals(x.getType())))
                    .map(ProductPhotoDTO::getUrl).collect(Collectors.toList());
            seckillProductDetailEntity.setSwiper(swiper);
        }

        redisUtil.set(getSeckillProductDetailKey(esSeckillProductEntity.getId()), JSON.toJSONString(seckillProductDetailEntity));
    }

    private void syscProductStockToRedis(ESSeckillProductEntity esSeckillProductEntity) {
        redisUtil.increment(getSeckillProductStockKey(esSeckillProductEntity.getId()), esSeckillProductEntity.getWithHoldQuantity());
    }


    private String getSeckillProductDetailKey(String id) {
        return String.format("%s%s", SECKILL_PRODUCT_DETAIL_PFREFIX, id);
    }

    private String getSeckillProductStockKey(String id) {
        return String.format("%s%s", SECKILL_PRODUCT_STOCK_PREFIX, id);
    }

    private ESSeckillProductEntity convertESSeckillProductEntity(SeckillProductEntity seckillProductEntity) {
        ESSeckillProductEntity esSeckillProductEntity = new ESSeckillProductEntity();
        esSeckillProductEntity.setId(seckillProductEntity.getId().toString());
        esSeckillProductEntity.setProductId(seckillProductEntity.getProductId());
        esSeckillProductEntity.setName(seckillProductEntity.getName());
        esSeckillProductEntity.setModel(seckillProductEntity.getModel());
        esSeckillProductEntity.setCostPrice(seckillProductEntity.getCostPrice());
        esSeckillProductEntity.setPrice(seckillProductEntity.getPrice());
        esSeckillProductEntity.setStartTime(seckillProductEntity.getStartTime());
        esSeckillProductEntity.setEndTime(seckillProductEntity.getEndTime());
        esSeckillProductEntity.setRemainQuantity(seckillProductEntity.getRemainQuantity());
        esSeckillProductEntity.setWithHoldQuantity(seckillProductEntity.getWithHoldQuantity());
        return esSeckillProductEntity;
    }

    private void checkParam(SeckillProductEntity seckillProductEntity) {
        List<ProductDTO> productList = productFeignClient.findByIds(Lists.newArrayList(seckillProductEntity.getProductId()));
        AssertUtil.notEmpty(productList, "商品ID在系统中不存在");
    }

    /**
     * 修改秒杀商品
     *
     * @param seckillProductEntity 秒杀商品信息
     * @return 结果
     */
    public void update(SeckillProductEntity seckillProductEntity) {
        checkParam(seckillProductEntity);
        FillUserUtil.fillUpdateUserInfo(seckillProductEntity);
        seckillProductMapper.update(seckillProductEntity);
        syncToESAndRedis(seckillProductEntity);
    }

    /**
     * 批量删除秒杀商品对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<SeckillProductEntity> entities = seckillProductMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "秒杀商品已被删除");

        SeckillProductEntity entity = new SeckillProductEntity();
        FillUserUtil.fillUpdateUserInfo(entity);

        return transactionTemplate.execute((status -> {
            int count = seckillProductMapper.deleteByIds(ids, entity);
            try {
                //todo这里后面需要优化，逻辑可以迁移到MQ中
                esTemplate.deleteBatch(businessConfig.getSeckillProductEsIndexName(), ids);
                for (Long id : ids) {
                    redisUtil.del(getSeckillProductDetailKey(id.toString()));
                }
            } catch (Exception e) {
                throw new BusinessException("删除秒杀商品失败");
            }
            return count;
        }));
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return seckillProductMapper;
    }

}
