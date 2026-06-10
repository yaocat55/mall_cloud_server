package cn.net.mall.marketing.mapper;

import cn.net.mall.marketing.entity.SeckillProductConditionEntity;
import cn.net.mall.marketing.entity.SeckillProductEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品 mapper
 *
 * @date 2024-07-08 10:57:31
 */
public interface SeckillProductMapper extends BaseMapper<SeckillProductEntity, SeckillProductConditionEntity> {
	/**
     * 查询秒杀商品信息
     *
     * @param id 秒杀商品ID
     * @return 秒杀商品信息
     */
	SeckillProductEntity findById(Long id);

	/**
     * 添加秒杀商品
     *
     * @param seckillProductEntity 秒杀商品信息
     * @return 结果
     */
	int insert(SeckillProductEntity seckillProductEntity);

	/**
     * 修改秒杀商品
     *
     * @param seckillProductEntity 秒杀商品信息
     * @return 结果
     */
	int update(SeckillProductEntity seckillProductEntity);

	/**
     * 批量删除秒杀商品
     *
     * @param ids id集合
     * @param entity 秒杀商品实体
     * @return 结果
     */
	int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") SeckillProductEntity entity);

	/**
     * 批量查询秒杀商品信息
     *
     * @param ids ID集合
     * @return 部门信息
    */
	List<SeckillProductEntity> findByIds(List<Long> ids);
}
