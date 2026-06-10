package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonDictDetailConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据字典详情 mapper
 *
 * @date 2024-03-25 21:41:03
 */
public interface CommonDictDetailMapper {
    /**
     * 查询数据字典详情信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    CommonDictDetailEntity findById(Long id);

    /**
     * 根据条件查询数据字典详情列表
     *
     * @param commonDictDetailConditionEntity 数据字典详情信息
     * @return 部门集合
     */
    List<CommonDictDetailEntity> searchByCondition(CommonDictDetailConditionEntity commonDictDetailConditionEntity);

    /**
     * 根据条件查询数据字典详情数量
     *
     * @param commonDictDetailConditionEntity 数据字典详情信息
     * @return 部门集合
     */
    int searchCount(CommonDictDetailConditionEntity commonDictDetailConditionEntity);

    /**
     * 添加数据字典详情
     *
     * @param dictDetailEntity 数据字典详情信息
     * @return 结果
     */
    int insert(CommonDictDetailEntity dictDetailEntity);

    /**
     * 修改数据字典详情
     *
     * @param dictDetailEntity 数据字典详情信息
     * @return 结果
     */
    int update(CommonDictDetailEntity dictDetailEntity);

    /**
     * 删除数据字典详情
     *
     * @param id 数据字典详情ID
     * @return 结果
     */
    int deleteById(Long id);


    /**
     * 批量查询数据字典详情信息
     *
     * @param ids ID集合
     * @return 数据字段信息
     */
    List<CommonDictDetailEntity> findByIds(List<Long> ids);

    /**
     * 删除数据字典详情
     *
     * @param ids              id集合
     * @param dictDetailEntity 数据字段实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("dictDetailEntity") CommonDictDetailEntity dictDetailEntity);


    /**
     * 根据数据字典名称查询数据字典详情
     *
     * @param dictName 数据字典名称
     * @return 数据字典详情
     */
    List<CommonDictDetailEntity> findByDictName(@Param("dictName") String dictName);
}
