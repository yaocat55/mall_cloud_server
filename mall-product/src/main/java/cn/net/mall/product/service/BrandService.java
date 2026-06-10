package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.entity.BrandConditionEntity;
import cn.net.mall.product.entity.BrandEntity;
import cn.net.mall.product.mapper.BrandMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
/**
 * 品牌 服务层
 *
 * @date 2024-05-09 14:43:55
 */
@Service
public class BrandService extends BaseService< BrandEntity,  BrandConditionEntity> {

	private final BrandMapper brandMapper;

	public BrandService(BrandMapper brandMapper) {
		this.brandMapper = brandMapper;
	}

	/**
     * 查询品牌信息
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
	public BrandEntity findById(Long id) {
	    return brandMapper.findById(id);
	}

	/**
     * 根据条件分页查询品牌列表
     *
     * @param brandConditionEntity 品牌信息
     * @return 品牌集合
     */
	public ResponsePageEntity<BrandEntity> searchByPage(BrandConditionEntity brandConditionEntity) {
		return super.searchByPage(brandConditionEntity);
	}

    /**
     * 新增品牌
     *
     * @param brandEntity 品牌信息
     * @return 结果
     */
	public int insert(BrandEntity brandEntity) {
		checkParam(brandEntity);
	    return brandMapper.insert(brandEntity);
	}

	private void checkParam(BrandEntity brandEntity) {
		brandEntity.setName(brandEntity.getName().trim());

		BrandConditionEntity brandConditionEntity = new BrandConditionEntity();
		brandConditionEntity.setName(brandEntity.getName());
		List<BrandEntity> brandEntities = brandMapper.searchByCondition(brandConditionEntity);
		if(Objects.nonNull(brandEntity.getId())) {
			Optional<BrandEntity> optional = brandEntities.stream().filter(x -> !x.getId().equals(brandEntity.getId())).findAny();
			if(optional.isPresent()) {
				throw new BusinessException("该品牌在系统中已存在");
			}
		} else {
			if(CollectionUtils.isNotEmpty(brandEntities)) {
				throw new BusinessException("该品牌在系统中已存在");
			}
		}
	}

	/**
     * 修改品牌
     *
     * @param brandEntity 品牌信息
     * @return 结果
     */
	public int update(BrandEntity brandEntity) {
	    return brandMapper.update(brandEntity);
	}

	/**
     * 批量删除品牌对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<BrandEntity> entities = brandMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "品牌已被删除");

		BrandEntity entity = new BrandEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return brandMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return brandMapper;
	}

}
