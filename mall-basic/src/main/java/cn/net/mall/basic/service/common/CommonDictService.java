package cn.net.mall.basic.service.common;

import cn.hutool.json.JSONUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonDictConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailConditionEntity;
import cn.net.mall.basic.entity.common.CommonDictDetailEntity;
import cn.net.mall.basic.entity.common.CommonDictEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.basic.mapper.common.CommonDictDetailMapper;
import cn.net.mall.basic.mapper.common.CommonDictMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.RedisUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据字典 服务层
 *
 * @date 2024-03-21 18:50:46
 */
@Service
public class CommonDictService extends BaseService<CommonDictEntity, CommonDictConditionEntity> {

    private static final String DICT_DATA_KEY = "dictData";

    private final CommonDictMapper commonDictMapper;
    private final CommonDictDetailMapper commonDictDetailMapper;
    private final RedisUtil redisUtil;

    public CommonDictService(CommonDictMapper commonDictMapper, CommonDictDetailMapper commonDictDetailMapper, RedisUtil redisUtil) {
        this.commonDictMapper = commonDictMapper;
        this.commonDictDetailMapper = commonDictDetailMapper;
        this.redisUtil = redisUtil;
    }

    /**
     * 查询数据字典信息
     *
     * @param id 数据字典ID
     * @return 数据字典信息
     */
    public CommonDictEntity findById(Long id) {
        return commonDictMapper.findById(id);
    }

    /**
     * 根据条件分页查询数据字典列表
     *
     * @param commonDictConditionEntity 数据字典信息
     * @return 数据字典集合
     */
    public ResponsePageEntity<CommonDictEntity> searchByPage(CommonDictConditionEntity commonDictConditionEntity) {
        return super.searchByPage(commonDictConditionEntity);
    }

    /**
     * 从缓存中获取数据字典
     *
     * @param dictName 数据字典名称
     * @return 数据字典
     */
    @Cacheable(value = "dict_data", keyGenerator = "dictCacheKeyGenerator")
    public List<CommonDictDetailEntity> queryDictDetailEntity(String dictName) {
        List<CommonDictDetailEntity> dataList = getDictDataFromRedis(dictName);
        if (CollectionUtils.isEmpty(dataList)) {
            dataList = commonDictDetailMapper.findByDictName(dictName);
            if (CollectionUtils.isEmpty(dataList)) {
                return Collections.emptyList();
            }

            Map<Object, Object> dictMap = new HashMap<>();
            dictMap.put(dictMap, JSONUtil.toJsonStr(dataList));
            redisUtil.putHashMap(DICT_DATA_KEY, dictMap);
            return dataList;
        }
        return dataList.stream().sorted((a, b) -> a.getSort().compareTo(b.getSort())).collect(Collectors.toList());
    }

    /**
     * 新增数据字典
     *
     * @param dictEntity 数据字典信息
     * @return 结果
     */
    public int insert(CommonDictEntity dictEntity) {
        FillUserUtil.fillCreateUserInfo(dictEntity);
        return commonDictMapper.insert(dictEntity);
    }

    /**
     * 修改数据字典
     *
     * @param dictEntity 数据字典信息
     * @return 结果
     */
    public int update(CommonDictEntity dictEntity) {
        FillUserUtil.fillUpdateUserInfo(dictEntity);
        return commonDictMapper.update(dictEntity);
    }

    /**
     * 删除数据字典对象
     *
     * @param ids 系统ID
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonDictEntity> dictEntities = commonDictMapper.findByIds(ids);
        AssertUtil.notEmpty(dictEntities, "数据字典详情已被删除");

        CommonDictEntity dictEntity = new CommonDictEntity();
        FillUserUtil.fillUpdateUserInfo(dictEntity);
        return commonDictMapper.deleteByIds(ids, dictEntity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonDictMapper;
    }

    /**
     * 更新redis中的数据字典
     */
    public void refreshDict() {
        CommonDictConditionEntity commonDictConditionEntity = new CommonDictConditionEntity();
        commonDictConditionEntity.setPageNo(0);
        List<CommonDictEntity> dictEntities = commonDictMapper.searchByCondition(commonDictConditionEntity);
        if (CollectionUtils.isEmpty(dictEntities)) {
            return;
        }

        List<Long> dictIdList = dictEntities.stream().map(CommonDictEntity::getId).collect(Collectors.toList());
        CommonDictDetailConditionEntity commonDictDetailConditionEntity = new CommonDictDetailConditionEntity();
        commonDictDetailConditionEntity.setDictIdList(dictIdList);
        commonDictDetailConditionEntity.setPageNo(0);
        List<CommonDictDetailEntity> dictDetailEntities = commonDictDetailMapper.searchByCondition(commonDictDetailConditionEntity);
        Map<Long, List<CommonDictDetailEntity>> dictDetailMap = dictDetailEntities.stream()
                .collect(Collectors.groupingBy(CommonDictDetailEntity::getDictId));

        Map<Object, Object> dictMap = new HashMap<>(dictEntities.size());
        for (CommonDictEntity dictEntity : dictEntities) {
            List<CommonDictDetailEntity> detailEntityList = dictDetailMap.get(dictEntity.getId());
            dictMap.put(dictEntity.getDictName(), JSONUtil.toJsonStr(detailEntityList));
        }

        redisUtil.putHashMap(DICT_DATA_KEY, dictMap);
    }

    /**
     * 从redis中获取数据字典数据
     *
     * @return 数据字典数据
     */
    public List<CommonDictDetailEntity> getDictDataFromRedis(String hashKey) {
        String json = (String) redisUtil.getHashValue(DICT_DATA_KEY, hashKey);
        if (!StringUtils.hasLength(json)) {
            return Collections.emptyList();
        }

        return JSONUtil.toList(json, CommonDictDetailEntity.class);
    }
}
