package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.IndexProductDTO;
import cn.net.mall.product.entity.*;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.enums.ProductTypeEnum;
import cn.net.mall.product.mapper.IndexProductMapper;
import cn.net.mall.product.mapper.ProductMapper;
import cn.net.mall.product.mapper.ProductPhotoMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.net.mall.product.enums.ProductTypeEnum.*;
import static cn.net.mall.product.util.CoverUtil.getCover;

/**
 * 首页商品 服务层
 *
 * @date 2024-08-27 17:37:52
 */
@AllArgsConstructor
@Slf4j
@Service
public class IndexProductService extends BaseService<IndexProductEntity, IndexProductConditionEntity> {

    private static final String INDEX_PRODUCT_PREFIX = "indexProduct:";

    private final IndexProductMapper indexProductMapper;
    private final ProductMapper productMapper;
    private final ProductPhotoMapper productPhotoMapper;
    private final RedisUtil redisUtil;

    /**
     * 获取首页商品列表
     *
     * @return 首页商品列表
     */
    public List<IndexProductDTO> getIndexProductList(int type) {
        String value = redisUtil.get(getKey(type));
        return JSON.parseArray(value, IndexProductDTO.class);
    }


    /**
     * 刷新首页商品
     *
     * @param productTop 显示多少条数据
     * @param sortParam  排序
     */
    public void refreshIndexProduct(int productTop, String sortParam) {
        for (ProductTypeEnum productTypeEnum : ProductTypeEnum.values()) {
            Integer type = productTypeEnum.getValue();
            IndexProductConditionEntity indexProductConditionEntity = new IndexProductConditionEntity();
            indexProductConditionEntity.setType(type);
            indexProductConditionEntity.setPageNo(1);
            indexProductConditionEntity.setPageSize(productTop);
            if (StringUtils.hasLength(sortParam)) {
                indexProductConditionEntity.setSortField(Arrays.stream(sortParam.split(" ")).collect(Collectors.toList()));
            }
            List<IndexProductEntity> indexProductEntities = indexProductMapper.searchByCondition(indexProductConditionEntity);
            if (CollectionUtils.isEmpty(indexProductEntities)) {
                return;
            }

            fillProductInfo(indexProductEntities);
            redisUtil.set(getKey(type), JSON.toJSONString(indexProductEntities));
        }
        log.info("refreshIndexProduct 更新完成");
    }

    private String getKey(int type) {
        return String.format("%s%s", INDEX_PRODUCT_PREFIX, type);
    }

    /**
     * 查询首页商品信息
     *
     * @param id 首页商品ID
     * @return 首页商品信息
     */
    public IndexProductEntity findById(Long id) {
        return indexProductMapper.findById(id);
    }

    /**
     * 根据条件分页查询首页商品列表
     *
     * @param indexProductConditionEntity 首页商品信息
     * @return 首页商品集合
     */
    public ResponsePageEntity<IndexProductEntity> searchByPage(IndexProductConditionEntity indexProductConditionEntity) {
        ResponsePageEntity<IndexProductEntity> responsePageEntity = super.searchByPage(indexProductConditionEntity);

        if (CollectionUtils.isNotEmpty(responsePageEntity.getData())) {
            fillProductInfo(responsePageEntity.getData());
        }
        return responsePageEntity;
    }

    private void fillProductInfo(List<IndexProductEntity> dataList) {
        List<Long> productIdList = Lists.newArrayList();
        List<Long> hotNewProductIdList = dataList.stream()
                .filter(x -> HOT.getValue().equals(x.getType()) || NEW.getValue().equals(x.getType()))
                .map(IndexProductEntity::getProductId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(hotNewProductIdList)) {
            productIdList.addAll(hotNewProductIdList);
        }

        if (CollectionUtils.isEmpty(productIdList)) {
            return;
        }

        ProductPhotoConditionEntity productPhotoConditionEntity = new ProductPhotoConditionEntity();
        productPhotoConditionEntity.setProductIdList(productIdList);
        productPhotoConditionEntity.setPageNo(0);
        productPhotoConditionEntity.setType(1);
        List<ProductPhotoEntity> productPhotoEntities = productPhotoMapper.searchByCondition(productPhotoConditionEntity);

        ProductConditionEntity productConditionEntity = new ProductConditionEntity();
        productConditionEntity.setIdList(hotNewProductIdList);
        List<ProductEntity> productEntities = productMapper.searchByCondition(productConditionEntity);
        for (IndexProductEntity indexProductEntity : dataList) {
            if (HOT.getValue().equals(indexProductEntity.getType()) || NEW.getValue().equals(indexProductEntity.getType())) {
                fillData(indexProductEntity, indexProductEntity.getProductId(), productEntities, productPhotoEntities);
            }
        }

    }

    private void fillData(IndexProductEntity indexProductEntity, Long productId, List<ProductEntity> productEntities, List<ProductPhotoEntity> productPhotoEntities) {
        Optional<ProductEntity> productOptional = productEntities.stream().filter(x -> x.getId().equals(productId)).findAny();
        if (productOptional.isPresent()) {
            ProductEntity productEntity = productOptional.get();
            indexProductEntity.setProductName(productEntity.getName());
            indexProductEntity.setModel(productEntity.getModel());
            indexProductEntity.setPrice(productEntity.getPrice());
            indexProductEntity.setCover(getCover(productEntity.getId(), productPhotoEntities));
        }
    }

    /**
     * 新增首页商品
     *
     * @param indexProductEntity 首页商品信息
     * @return 结果
     */
    public int insert(IndexProductEntity indexProductEntity) {
        return indexProductMapper.insert(indexProductEntity);
    }

    /**
     * 修改首页商品
     *
     * @param indexProductEntity 首页商品信息
     * @return 结果
     */
    public int update(IndexProductEntity indexProductEntity) {
        return indexProductMapper.update(indexProductEntity);
    }

    /**
     * 批量删除首页商品对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<IndexProductEntity> entities = indexProductMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "首页商品已被删除");

        IndexProductEntity entity = new IndexProductEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return indexProductMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return indexProductMapper;
    }

}
