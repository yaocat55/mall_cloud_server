package cn.net.mall.pay.mapper;

import cn.net.mall.pay.entity.PayBizConfigConditionEntity;
import cn.net.mall.pay.entity.PayBizConfigEntity;
import cn.net.mall.pay.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 业务渠道接入 mapper
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
public interface PayBizConfigMapper extends BaseMapper<PayBizConfigEntity, PayBizConfigConditionEntity> {
	/**
     * 查询业务渠道接入信息
     *
     * @param id 业务渠道接入ID
     * @return 业务渠道接入信息
     */
	PayBizConfigEntity findById(Long id);

	/**
     * 添加业务渠道接入
     *
     * @param payBizConfigEntity 业务渠道接入信息
     * @return 结果
     */
	int insert(PayBizConfigEntity payBizConfigEntity);

	/**
     * 修改业务渠道接入
     *
     * @param payBizConfigEntity 业务渠道接入信息
     * @return 结果
     */
	int update(PayBizConfigEntity payBizConfigEntity);

	/**
     * 批量删除业务渠道接入
     *
     * @param ids id集合
     * @param entity 业务渠道接入实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") PayBizConfigEntity entity);

	/**
     * 批量查询业务渠道接入信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<PayBizConfigEntity> findByIds(List<Long> ids);
}
