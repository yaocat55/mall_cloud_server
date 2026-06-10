package cn.net.mall.helper;

import cn.net.mall.util.SnowFlakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统一封装ID生成服务
 *
 * @date 2024/5/21 下午5:27
 */
@Component
public class IdGenerateHelper {

    @Autowired
    private SnowFlakeIdWorker snowFlakeIdWorker;

    /**
     * 生成分布式ID
     *
     * @return 分布式ID
     */
    public Long nextId() {
        return snowFlakeIdWorker.nextId();
    }
}
