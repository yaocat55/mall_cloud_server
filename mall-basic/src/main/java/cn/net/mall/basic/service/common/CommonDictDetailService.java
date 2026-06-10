package cn.net.mall.basic.service.common;

import cn.net.mall.basic.entity.common.CommonDictConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailEntity;
import cn.net.mall.basic.entity.common.CommonDictEntity;
import cn.net.mall.basic.mapper.common.CommonDictDetailMapper;
import cn.net.mall.basic.mapper.common.CommonDictMapper;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据字典 服务层
 *
 * @date 2024-03-25 21:41:03
 */
@Service
public class CommonDictDetailService {

    private final CommonDictDetailMapper commonDictDetailMapper;
    private final CommonDictService commonDictService;
    private final CommonDictMapper commonDictMapper;

    public CommonDictDetailService(CommonDictDetailMapper commonDictDetailMapper, CommonDictService commonDictService, CommonDictMapper commonDictMapper) {
        this.commonDictDetailMapper = commonDictDetailMapper;
        this.commonDictService = commonDictService;
        this.commonDictMapper = commonDictMapper;
    }

    /**
     * 查询信息
     *
     * @param id 数据字典ID
     * @return 数据字典信息
     */
    public CommonDictDetailEntity findById(Long id) {
        return commonDictDetailMapper.findById(id);
    }

    /**
     * 根据条件分页查询数据字典列表
     *
     * @param commonDictDetailConditionEntity 数据字典信息
     * @return 数据字典集合
     */
    public ResponsePageEntity<CommonDictDetailEntity> searchByPage(CommonDictDetailConditionEntity commonDictDetailConditionEntity) {
        if (StringUtils.hasLength(commonDictDetailConditionEntity.getDictName())) {
            CommonDictConditionEntity commonDictConditionEntity = new CommonDictConditionEntity();
            commonDictConditionEntity.setDictName(commonDictDetailConditionEntity.getDictName());
            List<CommonDictEntity> dictEntities = commonDictMapper.searchByCondition(commonDictConditionEntity);
            if (!CollectionUtils.isEmpty(dictEntities)) {
                commonDictDetailConditionEntity.setDictId(dictEntities.get(0).getId());
            }
        }

        int count = commonDictDetailMapper.searchCount(commonDictDetailConditionEntity);
        if (count == 0) {
            return ResponsePageEntity.buildEmpty(commonDictDetailConditionEntity);
        }
        List<CommonDictDetailEntity> dataList = commonDictDetailMapper.searchByCondition(commonDictDetailConditionEntity);
        return ResponsePageEntity.build(commonDictDetailConditionEntity, count, dataList);
    }

    /**
     * 从缓存中查询数据字典详情
     *
     * @param commonDictDetailConditionEntity 查询条件
     * @return 数据字典详情
     */
    public List<CommonDictDetailEntity> searchDictDetailFromCache(CommonDictDetailConditionEntity commonDictDetailConditionEntity) {
        if (!StringUtils.hasLength(commonDictDetailConditionEntity.getDictName())) {
            return Lists.newArrayList();
        }
        return commonDictService.queryDictDetailEntity(commonDictDetailConditionEntity.getDictName());
    }

    /**
     * 新增数据字典
     *
     * @param dictDetailEntity 数据字典信息
     * @return 结果
     */
    public int insert(CommonDictDetailEntity dictDetailEntity) {
        AssertUtil.notNull(dictDetailEntity.getDict(), "dict不能为空");
        FillUserUtil.fillCreateUserInfo(dictDetailEntity);
        dictDetailEntity.setDictId(dictDetailEntity.getDict().getId());
        return commonDictDetailMapper.insert(dictDetailEntity);
    }

    /**
     * 修改数据字典
     *
     * @param dictDetailEntity 数据字典信息
     * @return 结果
     */
    public int update(CommonDictDetailEntity dictDetailEntity) {
        FillUserUtil.fillUpdateUserInfo(dictDetailEntity);
        return commonDictDetailMapper.update(dictDetailEntity);
    }

    /**
     * 删除数据字典门对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonDictDetailEntity> detailEntityList = commonDictDetailMapper.findByIds(ids);
        AssertUtil.notEmpty(detailEntityList, "数据字典详情已被删除");

        CommonDictDetailEntity dictDetailEntity = new CommonDictDetailEntity();
        FillUserUtil.fillUpdateUserInfo(dictDetailEntity);
        return commonDictDetailMapper.deleteByIds(ids, dictDetailEntity);
    }


}
