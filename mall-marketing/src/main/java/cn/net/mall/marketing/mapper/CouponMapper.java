package cn.net.mall.marketing.mapper;

import cn.net.mall.marketing.entity.CouponConditionEntity;
import cn.net.mall.marketing.entity.CouponEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券 mapper
 *
 * @date 2024-09-13 15:38:33
 */
public interface CouponMapper extends BaseMapper<CouponEntity, CouponConditionEntity> {
	/**
     * 查询优惠券信息
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
	CouponEntity findById(Long id);

	/**
     * 添加优惠券
     *
     * @param couponEntity 优惠券信息
     * @return 结果
     */
	int insert(CouponEntity couponEntity);

	/**
     * 修改优惠券
     *
     * @param couponEntity 优惠券信息
     * @return 结果
     */
	int update(CouponEntity couponEntity);

    /**
     * 批量删除优惠券
     *
     * @param ids id集合
     * @param entity 优惠券实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") CouponEntity entity);

    /**
     * 批量查询优惠券信息
     *
     * @param ids ID集合
     * @return 优惠券信息
    */
    List<CouponEntity> findByIds(List<Long> ids);
}
