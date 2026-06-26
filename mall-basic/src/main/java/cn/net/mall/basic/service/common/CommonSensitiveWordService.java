package cn.net.mall.basic.service.common;

import cn.net.mall.basic.entity.common.CommonSensitiveWordConditionEntity;
import cn.net.mall.basic.entity.common.CommonSensitiveWordEntity;
import cn.net.mall.basic.mapper.common.CommonSensitiveWordMapper;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 敏感词 服务层
 *
 * @date 2024-05-18 21:09:00
 */
@Slf4j
@Service
public class CommonSensitiveWordService extends BaseService<CommonSensitiveWordEntity, CommonSensitiveWordConditionEntity> implements ApplicationRunner {

    private final CommonSensitiveWordMapper commonSensitiveWordMapper;
    private final IdGenerateHelper idGenerateHelper;

    public CommonSensitiveWordService(CommonSensitiveWordMapper commonSensitiveWordMapper, IdGenerateHelper idGenerateHelper) {
        this.commonSensitiveWordMapper = commonSensitiveWordMapper;
        this.idGenerateHelper = idGenerateHelper;
    }

    @Value("#{'${mall.mgt.customDictionary:}'.split(',')}")
    private List<String> customDictionaryList;
    private static Map<String, Integer> sensitiveWordMap = Maps.newHashMap();

    /**
     * 定时刷新自定义的敏感词库
     */
    private void initCustomDictionary() {
        if (MapUtils.isEmpty(sensitiveWordMap)) {
            return;
        }

        sensitiveWordMap.forEach((word, value) -> {
            CustomDictionary.add(word);
        });
    }

    /**
     * 校验敏感词
     *
     * @param text
     */
    public void checkSensitiveWord(String text) {
        List<String> matchList = Lists.newArrayList();
        Map<String, String> segment = segment(text);
        segment.forEach((word, nature) -> {
            if (sensitiveWordMap.containsKey(word)) {
                matchList.add(word);
            }
        });

        AssertUtil.isTrue(CollectionUtils.isEmpty(matchList), String.format("您输入的内容，包含敏感词：%s", matchList));
    }


    private Map<String, String> segment(String text) {
        Map<String, String> wordMap = Maps.newHashMap();
        Segment segment = HanLP.newSegment().enableCustomDictionary(true);
        initCustomDictionary();
        List<Term> termList = segment.seg(text);
        for (Term term : termList) {
            String word = term.toString().substring(0, term.length());
            String nature = term.toString().substring(term.length() + 1);
            if (StringUtils.hasLength(word) && StringUtils.hasLength(nature)) {
                wordMap.put(word, nature);
            }
        }
        return wordMap;
    }

    /**
     * 初始化敏感词
     *
     * @param type     类型
     * @param filePath 文件路径
     */
    public Boolean initSensitiveWord(int type, String filePath) {
        List<CommonSensitiveWordEntity> addList = Lists.newArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!StringUtils.hasLength(line)) {
                    continue;
                }
                addList.add(creatCommonSensitiveWordEntity(type, line.trim()));
            }
        } catch (IOException e) {
            log.error("初始化敏感词失败，原因：", e);
        }

        if (CollectionUtils.isEmpty(addList)) {
            return Boolean.FALSE;
        }
        commonSensitiveWordMapper.batchInsert(addList);
        return Boolean.TRUE;
    }

    private CommonSensitiveWordEntity creatCommonSensitiveWordEntity(int type, String word) {
        CommonSensitiveWordEntity commonSensitiveWordEntity = new CommonSensitiveWordEntity();
        commonSensitiveWordEntity.setId(idGenerateHelper.nextId());
        commonSensitiveWordEntity.setType(type);
        commonSensitiveWordEntity.setWord(word.replace(",", ""));
        return commonSensitiveWordEntity;
    }

    /**
     * 查询敏感词信息
     *
     * @param id 敏感词ID
     * @return 敏感词信息
     */
    public CommonSensitiveWordEntity findById(Long id) {
        return commonSensitiveWordMapper.findById(id);
    }

    /**
     * 根据条件分页查询敏感词列表
     *
     * @param commonSensitiveWordConditionEntity 敏感词信息
     * @return 敏感词集合
     */
    public ResponsePageEntity<CommonSensitiveWordEntity> searchByPage(CommonSensitiveWordConditionEntity
                                                                              commonSensitiveWordConditionEntity) {
        return super.searchByPage(commonSensitiveWordConditionEntity);
    }

    /**
     * 新增敏感词
     *
     * @param commonSensitiveWordEntity 敏感词信息
     * @return 结果
     */
    public int insert(CommonSensitiveWordEntity commonSensitiveWordEntity) {
        checkParam(commonSensitiveWordEntity);
        return commonSensitiveWordMapper.insert(commonSensitiveWordEntity);
    }

    /**
     * 修改敏感词
     *
     * @param commonSensitiveWordEntity 敏感词信息
     * @return 结果
     */
    public int update(CommonSensitiveWordEntity commonSensitiveWordEntity) {
        AssertUtil.notNull(commonSensitiveWordEntity.getId(), "id不能为空");
        checkParam(commonSensitiveWordEntity);
        return commonSensitiveWordMapper.update(commonSensitiveWordEntity);
    }

    private void checkParam(CommonSensitiveWordEntity commonSensitiveWordEntity) {
        CommonSensitiveWordConditionEntity commonSensitiveWordConditionEntity = new CommonSensitiveWordConditionEntity();
        commonSensitiveWordConditionEntity.setWord(commonSensitiveWordEntity.getWord());
        List<CommonSensitiveWordEntity> commonSensitiveWordEntities = commonSensitiveWordMapper.searchByCondition(commonSensitiveWordConditionEntity);

        if (Objects.nonNull(commonSensitiveWordEntity.getId())) {
            Optional<CommonSensitiveWordEntity> optional = commonSensitiveWordEntities.stream()
                    .filter(x -> !x.getId().equals(commonSensitiveWordEntity.getId())).findAny();
            if (optional.isPresent()) {
                throw new BusinessException("该敏感词已存在，请重新修改");
            }
        } else {
            if (CollectionUtils.isNotEmpty(commonSensitiveWordEntities)) {
                throw new BusinessException("该敏感词已存在，请勿重复添加");
            }
        }
    }

    /**
     * 批量删除敏感词对象
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonSensitiveWordEntity> entities = commonSensitiveWordMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "敏感词已被删除");

        CommonSensitiveWordEntity entity = new CommonSensitiveWordEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return commonSensitiveWordMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonSensitiveWordMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CommonSensitiveWordConditionEntity commonSensitiveWordConditionEntity = new CommonSensitiveWordConditionEntity();
        commonSensitiveWordConditionEntity.setPageNo(0);
        List<CommonSensitiveWordEntity> commonSensitiveWordEntities = commonSensitiveWordMapper.searchByCondition(commonSensitiveWordConditionEntity);
        if (CollectionUtils.isEmpty(commonSensitiveWordEntities)) {
            return;
        }

        for (CommonSensitiveWordEntity commonSensitiveWordEntity : commonSensitiveWordEntities) {
            sensitiveWordMap.put(commonSensitiveWordEntity.getWord().trim(), commonSensitiveWordEntity.getType());
        }
    }
}
