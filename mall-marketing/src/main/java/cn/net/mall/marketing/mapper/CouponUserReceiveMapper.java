package cn.net.mall.marketing.mapper;

import cn.net.mall.marketing.entity.CouponUserReceiveConditionEntity;
import cn.net.mall.marketing.entity.CouponUserReceiveEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 优惠券领取 mapper
 *
 * @date 2024-09-13 15:38:33
 */
public interface CouponUserReceiveMapper extends BaseMapper<CouponUserReceiveEntity, CouponUserReceiveConditionEntity> {
	/**
     * 查询优惠券领取信息
     *
     * @param id 优惠券领取ID
     * @return 优惠券领取信息
     */
	CouponUserReceiveEntity findById(Long id);

	/**
     * 添加优惠券领取
     *
     * @param couponUserReceiveEntity 优惠券领取信息
     * @return 结果
     */
	int insert(CouponUserReceiveEntity couponUserReceiveEntity);

	/**
     * 修改优惠券领取
     *
     * @param couponUserReceiveEntity 优惠券领取信息
     * @return 结果
     */
	int update(CouponUserReceiveEntity couponUserReceiveEntity);

    /**
     * 批量删除优惠券领取
     *
     * @param ids id集合
     * @param entity 优惠券领取实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") CouponUserReceiveEntity entity);

    /**
     * 批量查询优惠券领取信息
     *
     * @param ids ID集合
     * @return 优惠券领取信息
    */
    List<CouponUserReceiveEntity> findByIds(List<Long> ids);

	/**
	 * 批量更新优惠券使用状态
	 *
	 * @param list 优惠券领取集合
	 * @return 影响行数
	 */
	int updateForBatch(List<CouponUserReceiveEntity> list);
}
