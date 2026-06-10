package cn.net.mall.basic.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.basic.enums.AreaLevelEnum;
import cn.net.mall.basic.util.PinyinUtil;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonAreaConditionEntity;
import cn.net.mall.basic.entity.common.CommonAreaEntity;
import cn.net.mall.basic.dto.AreaDTO;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.basic.mapper.common.CommonAreaMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 地区 服务层
 *
 * @date 2024-10-04 11:43:55
 */
@Slf4j
@Service
public class CommonAreaService extends BaseService<CommonAreaEntity, CommonAreaConditionEntity> {
    private final static String CITY_STRICT = "市辖区";

    @Value("${mall.job.guoJiaStatBaseUrl:https://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2023/}")
    private String guoJiaStatBaseUrl;
    @Value("${mall.job.guoJiaStatProvinceUrl:https://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2023/index.html}")
    private String guoJiaStatProvinceUrl;

    /**
     * 连接超时时间，单位 ms
     */
    private final static int CONNECT_TIME_OUT = 30 * 60 * 1000;

    private final CommonAreaMapper commonAreaMapper;

    public CommonAreaService(CommonAreaMapper commonAreaMapper) {
        this.commonAreaMapper = commonAreaMapper;
    }

    /**
     * 根据parentId获取地区列表
     *
     * @param parentId 上级地区ID
     * @return 地区列表
     */
    public List<AreaDTO> getAreaByParentId(Long parentId) {
        CommonAreaConditionEntity commonAreaConditionEntity = new CommonAreaConditionEntity();
        commonAreaConditionEntity.setParentId(parentId);
        commonAreaConditionEntity.setPageNo(0);
        commonAreaConditionEntity.setSortField(Lists.newArrayList("create_time,asc"));
        List<CommonAreaEntity> commonAreaEntities = commonAreaMapper.searchByCondition(commonAreaConditionEntity);
        if (CollectionUtils.isEmpty(commonAreaEntities)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(commonAreaEntities, AreaDTO.class);
    }

    /**
     * 从国家统计局抓取省市县区的数据
     */
    public void fetchData() {
        try {
            Connection connection = getConnection(guoJiaStatProvinceUrl);
            Document document = connection.get();
            // 获取所有省级别的元素
            Elements provinceElements = document.body().select("tr.provincetr").select("a[href]");
            if (CollectionUtils.isEmpty(provinceElements)) {
                log.info("行政区划数据拉取-未获取到省级数据");
                return;
            }
//            CountDownLatch countDownLatch = new CountDownLatch(provinceElements.size());
//            new ElementParseExecutor(provinceElements, countDownLatch).execute();
//            countDownLatch.await();
            for (Element element : provinceElements) {
                handProvince(element);
            }
            log.info("行政区划数据拉取-所有行政区划数据处理完成");
        } catch (Exception e) {
            log.error("抓取省市县区的数据失败，原因：", e);
            throw new BusinessException("抓取省市县区的数据失败");
        }
    }

    private void init() {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Connection getConnection(String url) {
        init();
        Connection connection = Jsoup.connect(url).timeout(CONNECT_TIME_OUT).method(Connection.Method.GET).followRedirects(false);
        connection.header(HttpConnection.CONTENT_ENCODING, "UTF-8");
        connection.header("Accept", "*/*");
        connection.header("Accept-Encoding", "gzip, deflate, br");
        connection.header("Accept-Language", "zh-CN,zh;q=0.9");
        connection.header("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        return connection;
    }

    /**
     * 处理省级数据
     *
     * @param provinceElement 省级页面元素
     */
    private void handProvince(Element provinceElement) {
        // 获取 <a> 标签的文本
        String provinceName = provinceElement.text();
        // 获取 <a> 标签的链接
        String href = provinceElement.attr("href");
        CommonAreaEntity provinceEntity = new CommonAreaEntity();
        provinceEntity.setName(provinceName);
        provinceEntity.setPinyin(PinyinUtil.toPinyin(provinceName));
        provinceEntity.setCode(href.replace(".html", ""));
        provinceEntity.setParentId(0L);
        provinceEntity.setFullName(provinceName);
        provinceEntity.setLevel(AreaLevelEnum.PROVINCE.getValue());
        // 存储数据
        save(provinceEntity);
        // 获取市级别的数据
        String cityUrl = guoJiaStatBaseUrl + href;
        handleCity(cityUrl, provinceEntity);
    }

    /**
     * 处理市级数据
     *
     * @param cityUrl      市级页面URL
     * @param parentEntity 上级行政区区划
     */
    private void handleCity(String cityUrl, CommonAreaEntity parentEntity) {
        try {
            Connection connection = getConnection(cityUrl);
            Document document = connection.get();
            Elements cityElements = document.body().select("table.citytable").select("a[href]");
            if (CollectionUtils.isEmpty(cityElements)) {
                log.info("行政区划数据拉取-未获取到市级数据");
                return;
            }
            int size = cityElements.size() / 2;
            for (int i = 0; i < size; i++) {
                Element cityCodeElement = cityElements.get(i * 2);
                CommonAreaEntity cityEntity = saveArea(cityElements, parentEntity, i, AreaLevelEnum.CITY);
                // 获取区县级数据
                String countyUrl = cityCodeElement.absUrl("href");
                handleDistrict(countyUrl, cityEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理区县级数据
     *
     * @param countyUrl    县级页面URL
     * @param parentEntity 上级行政区划
     */
    private void handleDistrict(String countyUrl, CommonAreaEntity parentEntity) {
        try {
            Connection connection = getConnection(countyUrl);
            Document document = connection.get();
            Elements districtElements = document.body().select("table.countytable").select("a[href]");
            if (CollectionUtils.isEmpty(districtElements)) {
                log.info("行政区划数据拉取-未获取到区县级数据");
                return;
            }
            int size = districtElements.size() / 2;
            for (int i = 0; i < size; i++) {
                Element element = districtElements.get(i * 2);
                CommonAreaEntity districtEntity = saveArea(districtElements, parentEntity, i, AreaLevelEnum.DISTRICT);
                if ("东城区".equals(districtEntity.getName())) {
                    System.out.println(districtEntity.getName());
                }
                // 获取区镇|街道数据
                String townUrl = element.absUrl("href");
                handleTown(townUrl, districtEntity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 处理乡镇
     *
     * @param townUrl      镇级页面URL
     * @param parentEntity 上级行政区划
     */
    private void handleTown(String townUrl, CommonAreaEntity parentEntity) {
        try {
            Connection connection = getConnection(townUrl);
            Document document = connection.get();
            Elements townElements = document.body().select("table.towntable").select("a[href]");
            if (CollectionUtils.isEmpty(townElements)) {
                log.info("行政区划数据拉取-未获取到镇级数据");
                return;
            }
            int size = townElements.size() / 2;
            for (int i = 0; i < size; i++) {
                saveArea(townElements, parentEntity, i, AreaLevelEnum.TOWN);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private CommonAreaEntity saveArea(Elements townElements,
                                      CommonAreaEntity parentEntity,
                                      int i,
                                      AreaLevelEnum areaLevelEnum) {
        int index = i * 2;
        Element element = townElements.get(index);
        String code = element.text();
        Element nameElement = townElements.get(index + 1);
        String areaName = nameElement.text();
        CommonAreaEntity commonAreaEntity = new CommonAreaEntity();
        if (CITY_STRICT.equals(areaName)) {
            areaName = parentEntity.getName();
        }
        commonAreaEntity.setName(areaName);
        commonAreaEntity.setPinyin(PinyinUtil.toPinyin(areaName));
        commonAreaEntity.setCode(code);
        commonAreaEntity.setLevel(areaLevelEnum.getValue());
        commonAreaEntity.setParentId(parentEntity.getId());
        commonAreaEntity.setFullName(parentEntity.getFullName() + "/" + areaName);
        // 存储数据
        save(commonAreaEntity);
        return commonAreaEntity;
    }


    class ElementParseExecutor {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(8, 10, 5,
                TimeUnit.MINUTES, new LinkedBlockingDeque<>(50), new ThreadPoolExecutor.AbortPolicy());
        private final List<Element> elements;
        private final CountDownLatch countDownLatch;

        ElementParseExecutor(List<Element> elements, CountDownLatch countDownLatch) {
            this.elements = elements;
            this.countDownLatch = countDownLatch;
        }

        public void execute() {
            if (!CollectionUtils.isEmpty(elements)) {
                for (Element element : elements) {
                    poolExecutor.execute(() -> {
                        handProvince(element);
                        countDownLatch.countDown();
                    });
                }
            }
        }
    }


    /**
     * 查询地区信息
     *
     * @param id 地区ID
     * @return 地区信息
     */
    public CommonAreaEntity findById(Long id) {
        return commonAreaMapper.findById(id);
    }

    /**
     * 根据条件分页查询地区列表
     *
     * @param commonAreaConditionEntity 地区信息
     * @return 地区集合
     */
    public ResponsePageEntity<CommonAreaEntity> searchByPage(CommonAreaConditionEntity commonAreaConditionEntity) {
        return super.searchByPage(commonAreaConditionEntity);
    }

    /**
     * 保存地区
     *
     * @param commonAreaEntity 地区信息
     * @return 结果
     */
    public int save(CommonAreaEntity commonAreaEntity) {
        CommonAreaConditionEntity commonAreaConditionEntity = new CommonAreaConditionEntity();
        commonAreaConditionEntity.setCode(commonAreaEntity.getCode());
        List<CommonAreaEntity> commonAreaEntities = commonAreaMapper.searchByCondition(commonAreaConditionEntity);
        if (CollectionUtils.isNotEmpty(commonAreaEntities)) {
            CommonAreaEntity oldCommonAreaEntity = commonAreaEntities.get(0);
            commonAreaEntity.setId(oldCommonAreaEntity.getId());
            FillUserUtil.fillUpdateUserInfo(commonAreaEntity);
            return commonAreaMapper.update(commonAreaEntity);
        }

        if (Objects.isNull(commonAreaEntity.getParentId())) {
            log.info("commonAreaEntity:{}", commonAreaEntity);
            return 0;
        }

        return commonAreaMapper.insert(commonAreaEntity);
    }

    /**
     * 新增地区
     *
     * @param commonAreaEntity 地区信息
     * @return 结果
     */
    public int insert(CommonAreaEntity commonAreaEntity) {
        return commonAreaMapper.insert(commonAreaEntity);
    }

    /**
     * 修改地区
     *
     * @param commonAreaEntity 地区信息
     * @return 结果
     */
    public int update(CommonAreaEntity commonAreaEntity) {
        return commonAreaMapper.update(commonAreaEntity);
    }

    /**
     * 批量删除地区
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<CommonAreaEntity> entities = commonAreaMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "地区已被删除");

        CommonAreaEntity entity = new CommonAreaEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return commonAreaMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return commonAreaMapper;
    }
}
