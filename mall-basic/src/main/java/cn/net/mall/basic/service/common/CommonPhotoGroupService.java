package cn.net.mall.basic.service.common;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonPhotoGroupConditionEntity;
import cn.net.mall.basic.entity.common.CommonPhotoGroupEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.basic.mapper.common.CommonPhotoGroupMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 图片分组 服务层
 *
 * @date 2024-07-03 16:43:09
 */
@Service
public class CommonPhotoGroupService extends BaseService< CommonPhotoGroupEntity,  CommonPhotoGroupConditionEntity> {

	private final CommonPhotoGroupMapper commonPhotoGroupMapper;

	public CommonPhotoGroupService(CommonPhotoGroupMapper commonPhotoGroupMapper) {
		this.commonPhotoGroupMapper = commonPhotoGroupMapper;
	}

	/**
     * 查询图片分组信息
     *
     * @param id 图片分组ID
     * @return 图片分组信息
     */
	public CommonPhotoGroupEntity findById(Long id) {
	    return commonPhotoGroupMapper.findById(id);
	}

	/**
     * 根据条件分页查询图片分组列表
     *
     * @param commonPhotoGroupConditionEntity 图片分组信息
     * @return 图片分组集合
     */
	public ResponsePageEntity<CommonPhotoGroupEntity> searchByPage(CommonPhotoGroupConditionEntity commonPhotoGroupConditionEntity) {
		return super.searchByPage(commonPhotoGroupConditionEntity);
	}

    /**
     * 新增图片分组
     *
     * @param commonPhotoGroupEntity 图片分组信息
     * @return 结果
     */
	public int insert(CommonPhotoGroupEntity commonPhotoGroupEntity) {
	    return commonPhotoGroupMapper.insert(commonPhotoGroupEntity);
	}

	/**
     * 修改图片分组
     *
     * @param commonPhotoGroupEntity 图片分组信息
     * @return 结果
     */
	public int update(CommonPhotoGroupEntity commonPhotoGroupEntity) {
	    return commonPhotoGroupMapper.update(commonPhotoGroupEntity);
	}

	/**
     * 批量删除图片分组对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
	public int deleteByIds(List<Long> ids) {
		List<CommonPhotoGroupEntity> entities = commonPhotoGroupMapper.findByIds(ids);
		AssertUtil.notEmpty(entities, "图片分组已被删除");

		CommonPhotoGroupEntity entity = new CommonPhotoGroupEntity();
		FillUserUtil.fillUpdateUserInfo(entity);
		return commonPhotoGroupMapper.deleteByIds(ids, entity);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return commonPhotoGroupMapper;
	}

}
