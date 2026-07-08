package cn.net.mall.workid;

/**
 * 统一封装ID生成服务
 *
 * @date 2024/5/21 下午5:27
 */
public class IdGenerateHelper {

    private final SnowFlakeIdWorker snowFlakeIdWorker;

    public IdGenerateHelper(SnowFlakeIdWorker snowFlakeIdWorker) {
        this.snowFlakeIdWorker = snowFlakeIdWorker;
    }

    /**
     * 生成分布式ID
     *
     * @return 分布式ID
     */
    public Long nextId() {
        return snowFlakeIdWorker.nextId();
    }
}
