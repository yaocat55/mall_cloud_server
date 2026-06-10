package cn.net.mall.product.mapper;

import cn.net.mall.product.entity
        .IndexCarouselImageConditionEntity;
import cn.net.mall.product.entity
        .IndexCarouselImageEntity;
import cn.net.mall.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页轮播图 mapper
 *
 * @date 2024-08-21 18:34:11
 */
public interface IndexCarouselImageMapper extends BaseMapper<IndexCarouselImageEntity, IndexCarouselImageConditionEntity> {
    /**
     * 查询首页轮播图信息
     *
     * @param id 首页轮播图ID
     * @return 首页轮播图信息
     */
    IndexCarouselImageEntity findById(Long id);

    /**
     * 添加首页轮播图
     *
     * @param indexCarouselImageEntity 首页轮播图信息
     * @return 结果
     */
    int insert(IndexCarouselImageEntity indexCarouselImageEntity);

    /**
     * 修改首页轮播图
     *
     * @param indexCarouselImageEntity 首页轮播图信息
     * @return 结果
     */
    int update(IndexCarouselImageEntity indexCarouselImageEntity);

    /**
     * 批量删除首页轮播图
     *
     * @param ids    id集合
     * @param entity 首页轮播图实体
     * @return 结果
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") IndexCarouselImageEntity entity);

    /**
     * 批量查询首页轮播图信息
     *
     * @param ids ID集合
     * @return 部门信息
     */
    List<IndexCarouselImageEntity> findByIds(List<Long> ids);
}
