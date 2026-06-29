package cn.net.mall.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * 分类树实体
 *
 * @date 2024/7/1 下午5:28
 */
@Schema(description = "分类树实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryTreeDTO {

    /**
     * 系统ID
     */
    @Schema(description = "系统ID", example = "1")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试数据")
    private String name;

    /**
     * 前端页面显示用的标签名称
     */
    @Schema(description = "标签名称", example = "标签")
    private String label;

    /**
     * 父分类ID
     */
    @Schema(description = "父分类ID", example = "0")
    private Long pid;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(description = "有效状态 1:有效 0:无效", example = "1")
    private Integer validStatus;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01 00:00:00")
    private Date createTime;

    /**
     * 是否叶子节点
     */
    @Schema(description = "是否叶子节点", example = "false")
    private Boolean leaf;


    /**
     * 是否有下一级
     */
    @Schema(description = "是否有下一级", example = "false")
    private Boolean hasChildren;

    /**
     * 下一级数量
     */
    @Schema(description = "下一级数量", example = "0")
    private Integer subCount;

    /**
     * 层级
     */
    @Schema(description = "层级", example = "1")
    private Integer level;

    /**
     * 子部门
     */
    private List<CategoryTreeDTO> children;

    /**
     * 增加添加子部门的方法
     *
     * @param deptTreeDTO 子部门
     */
    public void addChildren(CategoryTreeDTO deptTreeDTO) {
        if (Objects.isNull(children)) {
            children = Lists.newArrayList();
        }
        children.add(deptTreeDTO);
    }
}
