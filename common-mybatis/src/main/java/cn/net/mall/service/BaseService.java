package cn.net.mall.service;

import cn.net.mall.entity.RequestConditionEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.util.BetweenTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 公共service
 *
 * @date 2024/1/30 下午4:02
 */
@Slf4j
public abstract class BaseService<K, V> {
    private static final int MAX_EXCEL_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${mall.mgt.exportPageSize:2}")
    private int exportPageSize;

    @Value("${mall.mgt.sheetDataSize:4}")
    private int sheetDataSize;

    /**
     * 获取BaseMapper
     *
     * @return BaseMapper
     */
    protected abstract BaseMapper getBaseMapper();


    /**
     * 用户自定义导出逻辑
     *
     * @param v 查询条件
     * @return 是否自定义
     */
    public boolean customizeExport(V v) {
        return false;
    }

    /**
     * 通用的分页接口
     *
     * @param s   分页请求参数
     * @param <T> 分页返回实体
     * @return 数据
     */
    public <S extends RequestConditionEntity, T extends Object> ResponsePageEntity<T> searchByPage(S s) {
        BetweenTimeUtil.parseTime(s);
        int count = getBaseMapper().searchCount(s);
        if (count == 0) {
            return ResponsePageEntity.buildEmpty(s);
        }
        List<T> dataList = getBaseMapper().searchByCondition(s);
        return ResponsePageEntity.build(s, count, dataList);
    }

    /**
     * 公共excel导出方法
     *
     * @param v         查询条件
     * @param fileName  文件名称
     * @param clazzName 实体类名称
     * @IOException 异常
     */
    public String export(V v, String fileName, String clazzName) {
        if (customizeExport(v)) {
            return null;
        }

        return doExport(v, fileName, clazzName);
    }

    private String doExport(V v, String fileName, String clazzName) {
        //todo 改造异步导出excel功能
        return null;
    }


}
