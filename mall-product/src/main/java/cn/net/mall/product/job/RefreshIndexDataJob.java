package cn.net.mall.product.job;

import cn.net.mall.enums.JobResult;
import cn.net.mall.product.service.IndexCarouselImageService;
import cn.net.mall.product.service.IndexNoticeService;
import cn.net.mall.product.service.IndexProductService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static cn.net.mall.enums.JobResult.SUCCESS;
import static cn.net.mall.enums.JobResult.FAILURE;


/**
 * 更新首页数据
 * 将首页数据更新到Redis当中
 *
 * @date 2024/8/25 下午5:29
 */
@AllArgsConstructor
@Slf4j
@Component
public class RefreshIndexDataJob {

    private static final int DEFAULT_INDEX_CAROUSE_IMAGE_TOP = 5;
    private static final int DEFAULT_INDEX_PRODUCT_TOP = 12;

    private final IndexCarouselImageService indexCarouselImageService;
    private final IndexProductService indexProductService;
    private final IndexNoticeService indexNoticeService;

    public JobResult doRun(String params) {
        try {
            List<String> paramList = parseParam(params);
            int top = Integer.parseInt(paramList.get(0));
            int productTop = Integer.parseInt(paramList.get(1));
            String sortParam = paramList.size() > 2 ? paramList.get(2) : null;

            indexCarouselImageService.refreshIndexCarouseImageToRedis(top, sortParam);
            indexProductService.refreshIndexProduct(productTop, sortParam);
            indexNoticeService.refreshIndexNoticeToRedis(top, sortParam);
            return SUCCESS;
        } catch (Exception e) {
            log.error("RefreshIndexCarouseImageJob 执行失败，原因：", e);
            return FAILURE;
        }
    }


    /**
     * 参数示例 top=5&productTop=12&sortParam=sort asc,create_time desc
     */
    private List<String> parseParam(String params) {
        if (StringUtils.hasLength(params)) {
            String[] values = params.split("&");
            List<String> valueList = Lists.newArrayList();
            for (String value : values) {
                String[] paramValues = value.split("=");
                valueList.add(paramValues[1]);
            }
            return valueList;
        }
        return Lists.newArrayList(String.valueOf(DEFAULT_INDEX_CAROUSE_IMAGE_TOP), String.valueOf(DEFAULT_INDEX_PRODUCT_TOP));
    }
}
