package cn.net.mall.product.runner;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ES 连接预热，解决首次查询超时问题.
 */
@Slf4j
@Component
public class EsWarmUpRunner implements ApplicationRunner {

    private final RestClient restClient;

    /**
     * RestClient 由 {@link cn.net.mall.product.config.EsConfig} 创建的
     * ElasticsearchOperations 所用，同一份 RestClient 实例。
     */
    public EsWarmUpRunner(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始进行 ES 连接预热...");
        try {
            var resp = restClient.performRequest(new Request("GET", "/"));
            log.info("ES 连接预热完成，状态码：{}", resp.getStatusLine().getStatusCode());
        } catch (Exception e) {
            log.warn("ES 连接预热失败（不影响启动）", e);
        }
    }
}
