package cn.net.mall.basic.mapper.common;

import cn.net.mall.basic.entity.common.CommonDictConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据字典 mapper
 *
 * @date 2024-03-21 18:50:46
 */
public interface CommonDictMapper extends BaseMapper<CommonDictEntity, CommonDictConditionEntity> {
    /**
     * 查询数据字典信息
     *
     * @param id 数据字典ID
     * @return 数据字典信息
     */
    CommonDictEntity findById(Long id);

    /**
     * 添加数据字典
     *
     * @param dictEntity 数据字典信息
     * @return 结果
     */
    int insert(CommonDictEntity dictEntity);

    /**
     * 修改数据字典
     *
     * @param dictEntity 数据字典信息
     * @return 结果
     */
    int update(CommonDictEntity dictEntity);

    /**
     * 删除数据字典
     *
     * @param id 数据字典ID
     * @return 结果
     */
    int deleteById(Long id);


    /**
     * 批量查询数据字典信息
     *
     * @param ids ID集合
     * @return 数据字典信息
     */
    List<CommonDictEntity> findByIds(List<Long> ids);

    /**
     * 删除数据字典
     *
     * @param ids        id集合
     * @param dictEntity 数据字段实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("dictEntity") CommonDictEntity dictEntity);

}
