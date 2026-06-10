package cn.net.mall.product.runner;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ES预热启动器，解决首次调用ES超时问题
 */
@Slf4j
@Component
public class EsWarmUpRunner implements ApplicationRunner {

    private final RestHighLevelClient restHighLevelClient;

    public EsWarmUpRunner(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始进行ES连接预热...");
        try {
            boolean ping = restHighLevelClient.ping(RequestOptions.DEFAULT);
            log.info("ES连接预热完成，ping结果：{}", ping);
        } catch (Exception e) {
            log.error("ES连接预热失败", e);
        }
    }
}
