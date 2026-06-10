package cn.net.mall.product.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.dto.ProductPhotoDTO;
import cn.net.mall.product.entity.ProductPhotoConditionEntity;
import cn.net.mall.product.entity.ProductPhotoEntity;
import cn.net.mall.product.mapper.ProductPhotoMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品图片 服务层
 *
 * @date 2024-05-09 14:43:56
 */
@Service
public class ProductPhotoService extends BaseService<ProductPhotoEntity, ProductPhotoConditionEntity> {

    private final ProductPhotoMapper productPhotoMapper;

    public ProductPhotoService(ProductPhotoMapper productPhotoMapper) {
        this.productPhotoMapper = productPhotoMapper;
    }

    /**
     * 查询商品图片信息
     *
     * @param id 商品图片ID
     * @return 商品图片信息
     */
    public ProductPhotoEntity findById(Long id) {
        return productPhotoMapper.findById(id);
    }


    /**
     * 通过商品id集合批量查询商品图片信息
     *
     * @param productIds 商品ID
     * @return 商品图片信息
     */
    public List<ProductPhotoDTO> findByProductIds(@RequestBody List<Long> productIds) {
        ProductPhotoConditionEntity productPhotoConditionEntity = new ProductPhotoConditionEntity();
        productPhotoConditionEntity.setProductIdList(productIds);
        productPhotoConditionEntity.setPageNo(0);
        List<ProductPhotoEntity> productPhotoEntities = productPhotoMapper.searchByCondition(productPhotoConditionEntity);
        return BeanUtil.copyToList(productPhotoEntities, ProductPhotoDTO.class);
    }

    /**
     * 根据条件分页查询商品图片列表
     *
     * @param productPhotoConditionEntity 商品图片信息
     * @return 商品图片集合
     */
    public ResponsePageEntity<ProductPhotoEntity> searchByPage(ProductPhotoConditionEntity productPhotoConditionEntity) {
        int count = productPhotoMapper.searchCount(productPhotoConditionEntity);
        if (count == 0) {
            return ResponsePageEntity.buildEmpty(productPhotoConditionEntity);
        }
        List<ProductPhotoEntity> dataList = productPhotoMapper.searchByCondition(productPhotoConditionEntity);
        return ResponsePageEntity.build(productPhotoConditionEntity, count, dataList);
    }

    /**
     * 新增商品图片
     *
     * @param productPhotoEntity 商品图片信息
     * @return 结果
     */
    public int insert(ProductPhotoEntity productPhotoEntity) {
        return productPhotoMapper.insert(productPhotoEntity);
    }

    /**
     * 修改商品图片
     *
     * @param productPhotoEntity 商品图片信息
     * @return 结果
     */
    public int update(ProductPhotoEntity productPhotoEntity) {
        return productPhotoMapper.update(productPhotoEntity);
    }

    /**
     * 批量删除商品图片对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<ProductPhotoEntity> entities = productPhotoMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "商品图片已被删除");

        ProductPhotoEntity entity = new ProductPhotoEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return productPhotoMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return productPhotoMapper;
    }

}
