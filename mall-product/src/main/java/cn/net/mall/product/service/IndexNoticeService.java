package cn.net.mall.product.service;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.entity.RequestPageEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.dto.IndexNoticeDTO;
import cn.net.mall.product.entity.IndexNoticeConditionEntity;
import cn.net.mall.product.entity.IndexNoticeEntity;
import cn.net.mall.product.entity.web.IndexNoticeDetailWebEntity;
import cn.net.mall.product.entity.web.IndexNoticeWebEntity;
import cn.net.mall.product.mapper.IndexNoticeMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.DateFormatUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 首页公告 服务层
 *
 * @date 2024-10-03 15:58:40
 */
@AllArgsConstructor
@Slf4j
@Service
public class IndexNoticeService extends BaseService<IndexNoticeEntity, IndexNoticeConditionEntity> {

    private static final String INDEX_NOTICE_KEY = "indexNotice";

    private final IndexNoticeMapper indexNoticeMapper;
    private final RedisUtil redisUtil;

    /**
     * 获取首页公告列表
     *
     * @return 首页公告列表
     */
    public List<IndexNoticeDTO> getIndexNoticeList() {
        String value = redisUtil.get(INDEX_NOTICE_KEY);
        return JSON.parseArray(value, IndexNoticeDTO.class);
    }

    /**
     * 刷新首页轮播图到Redis中
     *
     * @param top 轮播图数量
     */
    public void refreshIndexNoticeToRedis(int top, String sortParam) {
        IndexNoticeConditionEntity indexNoticeConditionEntity = new IndexNoticeConditionEntity();
        indexNoticeConditionEntity.setPageNo(1);
        indexNoticeConditionEntity.setPageSize(top);
        if (StringUtils.hasLength(sortParam)) {
            indexNoticeConditionEntity.setSortField(Arrays.stream(sortParam.split(" ")).collect(Collectors.toList()));
        }

        List<IndexNoticeEntity> indexCarouselImageEntities = indexNoticeMapper.searchByCondition(indexNoticeConditionEntity);
        if (CollectionUtils.isEmpty(indexCarouselImageEntities)) {
            return;
        }

        redisUtil.set(INDEX_NOTICE_KEY, JSON.toJSONString(indexCarouselImageEntities));
        log.info("refreshIndexNoticeToRedis 更新完成");
    }

    /**
     * 查询首页公告信息
     *
     * @param id 首页公告ID
     * @return 首页公告信息
     */
    public IndexNoticeEntity findById(Long id) {
        return indexNoticeMapper.findById(id);
    }

    /**
     * 查询公告详情
     *
     * @param id 公告系统ID
     * @return 公告详情
     */
    public IndexNoticeDetailWebEntity getIndexNoticeDetail(Long id) {
        IndexNoticeDetailWebEntity indexNoticeDetailWebEntity = new IndexNoticeDetailWebEntity();
        IndexNoticeEntity noticeEntity = this.findById(id);
        if (Objects.isNull(noticeEntity)) {
            return indexNoticeDetailWebEntity;
        }

        indexNoticeDetailWebEntity.setId(noticeEntity.getId());
        indexNoticeDetailWebEntity.setTitle(noticeEntity.getTitle());
        indexNoticeDetailWebEntity.setContent(noticeEntity.getContent());
        indexNoticeDetailWebEntity.setCreateTime(DateFormatUtil.parseToString(noticeEntity.getCreateTime()));
        return indexNoticeDetailWebEntity;
    }

    /**
     * 根据条件搜索公告列表
     *
     * @param requestPageEntity 条件
     * @return 公告列表
     */
    public ResponsePageEntity<IndexNoticeDTO> searchIndexNoticeByPage(RequestPageEntity requestPageEntity) {
        IndexNoticeConditionEntity indexNoticeConditionEntity = new IndexNoticeConditionEntity();
        BeanUtil.copyProperties(requestPageEntity, indexNoticeConditionEntity, false);
        ResponsePageEntity<IndexNoticeEntity> indexNoticeEntityResponsePageEntity = this.searchByPage(indexNoticeConditionEntity);
        if (CollectionUtils.isEmpty(indexNoticeEntityResponsePageEntity.getData())) {
            return ResponsePageEntity.buildEmpty(requestPageEntity);
        }

        List<IndexNoticeDTO> dataList = indexNoticeEntityResponsePageEntity.getData().stream().map(x -> {
            IndexNoticeDTO indexNoticeWebEntity = new IndexNoticeDTO();
            indexNoticeWebEntity.setId(x.getId());
            indexNoticeWebEntity.setTitle(x.getTitle());
            indexNoticeWebEntity.setCreateTime(DateFormatUtil.parseToString(x.getCreateTime()));
            return indexNoticeWebEntity;
        }).collect(Collectors.toList());
        return ResponsePageEntity.build(requestPageEntity, indexNoticeEntityResponsePageEntity.getTotalCount(), dataList);
    }

    /**
     * 根据条件分页查询首页公告列表
     *
     * @param indexNoticeConditionEntity 首页公告信息
     * @return 首页公告集合
     */
    public ResponsePageEntity<IndexNoticeEntity> searchByPage(IndexNoticeConditionEntity indexNoticeConditionEntity) {
        return super.searchByPage(indexNoticeConditionEntity);
    }

    /**
     * 新增首页公告
     *
     * @param indexNoticeEntity 首页公告信息
     * @return 结果
     */
    public int insert(IndexNoticeEntity indexNoticeEntity) {
        return indexNoticeMapper.insert(indexNoticeEntity);
    }

    /**
     * 修改首页公告
     *
     * @param indexNoticeEntity 首页公告信息
     * @return 结果
     */
    public int update(IndexNoticeEntity indexNoticeEntity) {
        return indexNoticeMapper.update(indexNoticeEntity);
    }

    /**
     * 批量删除首页公告
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<IndexNoticeEntity> entities = indexNoticeMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "首页公告已被删除");

        IndexNoticeEntity entity = new IndexNoticeEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return indexNoticeMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return indexNoticeMapper;
    }
}
