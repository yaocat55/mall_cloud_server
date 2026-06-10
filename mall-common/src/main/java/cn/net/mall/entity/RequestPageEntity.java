package cn.net.mall.entity;

import cn.hutool.core.collection.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 分页请求实体
 *
 * @date 2024/1/4 下午3:55
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestPageEntity implements Serializable {

    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 页码，默认从一页开始
     */
    private Integer pageNo = 1;

    /**
     * 每页大小，默认一页查询10条数据
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private List<String> sortField;


    /**
     * 获取分页开始位置
     *
     * @return 分页开始位置
     */
    public Integer getPageBegin() {
        if (Objects.isNull(this.pageNo) || this.pageNo <= 0) {
            this.pageNo = 1;
        }

        if (Objects.isNull(this.pageSize)) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }

        return (this.pageNo - 1) * this.pageSize;
    }

    /**
     * 获取用户自定义排序集合
     *
     * @return 排序集合实体
     */
    public String getSortString() {
        List<String> sortField = this.getSortField();
        if (CollectionUtil.isEmpty(sortField)) {
            return null;
        }
        StringBuilder sortBuilder = new StringBuilder();
        for (String field : sortField) {
            String[] values = field.split(",");
            sortBuilder.append(String.format("%s %s", values[0], values[1])).append(",");
        }
        return sortBuilder.deleteCharAt(sortBuilder.length() - 1).toString();
    }
}
