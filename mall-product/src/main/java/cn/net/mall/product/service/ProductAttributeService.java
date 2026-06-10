package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.ProductAttributeConditionEntity;
import cn.net.mall.product.entity.ProductAttributeEntity;
import cn.net.mall.product.mapper.ProductAttributeMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 商品属性 服务层
 *
 * @date 2024-05-09 14:43:56
 */
@Service
public class ProductAttributeService extends BaseService< ProductAttributeEntity,  ProductAttributeConditionEntity> {

	private final ProductAttributeMapper productAttributeMapper;

	public ProductAttributeService(ProductAttributeMapper productAttributeMapper) {
		this.productAttributeMapper = productAttributeMapper;
	}

	/**
     * 查询商品属性信息
     *
     * @param id 商品属性ID
     * @return 商品属性信息
     */
	public ProductAttributeEntity findById(Long id) {
	    return productAttributeMapper.findById(id);
	}

	/**
     * 根据条件分页查询商品属性列表
     *
     * @param productAttributeConditionEntity 商品属性信息
     * @return 商品属性集合
     */
	public ResponsePageEntity<ProductAttributeEntity> searchByPage(ProductAttributeConditionEntity productAttributeConditionEntity) {
		int count = productAttributeMapper.searchCount(productAttributeConditionEntity);
		if (count == 0) {
			return ResponsePageEntity.buildEmpty(productAttributeConditionEntity);
		}
		List<ProductAttributeEntity> dataList = productAttributeMapper.searchByCondition(productAttributeConditionEntity);
		return ResponsePageEntity.build(productAttributeConditionEntity, count, dataList);
	}

    /**
     * 新增商品属性
     *
     * @param productAttributeEntity 商品属性信息
     * @return 结果
     */
	public int insert(ProductAttributeEntity productAttributeEntity) {
	    return productAttributeMapper.insert(productAttributeEntity);
	}

	/**
     * 修改商品属性
     *
     * @param productAttributeEntity 商品属性信息
     * @return 结果
     */
	public int update(ProductAttributeEntity productAttributeEntity) {
	    return productAttributeMapper.update(productAttributeEntity);
	}

	/**
     * 批量删除商品属性对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<ProductAttributeEntity> entities = productAttributeMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "商品属性已被删除");

		ProductAttributeEntity entity = new ProductAttributeEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return productAttributeMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return productAttributeMapper;
	}

}
