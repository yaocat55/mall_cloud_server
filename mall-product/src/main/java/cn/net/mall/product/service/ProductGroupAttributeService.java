package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.ProductGroupAttributeConditionEntity;
import cn.net.mall.product.entity.ProductGroupAttributeEntity;
import cn.net.mall.product.mapper.ProductGroupAttributeMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.stereotype.Service;

import java.util.List;

 /**
 * 商品组属性 服务层
 *
 * @date 2024-09-07 17:28:48
 */
@Service
public class ProductGroupAttributeService extends BaseService<ProductGroupAttributeEntity, ProductGroupAttributeConditionEntity> {

	private final ProductGroupAttributeMapper productGroupAttributeMapper;

	 public ProductGroupAttributeService(ProductGroupAttributeMapper productGroupAttributeMapper) {
		 this.productGroupAttributeMapper = productGroupAttributeMapper;
	 }

	 /**
     * 查询商品组属性信息
     *
     * @param id 商品组属性ID
     * @return 商品组属性信息
     */
	public ProductGroupAttributeEntity findById(Long id) {
	    return productGroupAttributeMapper.findById(id);
	}

	/**
     * 根据条件分页查询商品组属性列表
     *
     * @param productGroupAttributeConditionEntity 商品组属性信息
     * @return 商品组属性集合
     */
	public ResponsePageEntity<ProductGroupAttributeEntity> searchByPage(ProductGroupAttributeConditionEntity productGroupAttributeConditionEntity) {
		return super.searchByPage(productGroupAttributeConditionEntity);
	}

    /**
     * 新增商品组属性
     *
     * @param productGroupAttributeEntity 商品组属性信息
     * @return 结果
     */
	public int insert(ProductGroupAttributeEntity productGroupAttributeEntity) {
	    return productGroupAttributeMapper.insert(productGroupAttributeEntity);
	}

	/**
     * 修改商品组属性
     *
     * @param productGroupAttributeEntity 商品组属性信息
     * @return 结果
     */
	public int update(ProductGroupAttributeEntity productGroupAttributeEntity) {
	    return productGroupAttributeMapper.update(productGroupAttributeEntity);
	}

	/**
     * 批量删除商品组属性
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<ProductGroupAttributeEntity> entities = productGroupAttributeMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "商品组属性已被删除");

		ProductGroupAttributeEntity entity = new ProductGroupAttributeEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return productGroupAttributeMapper.deleteByIds(ids, entity);
	}

    @Override
    protected BaseMapper getBaseMapper() {
        return productGroupAttributeMapper;
    }
}
