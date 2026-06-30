package cn.net.mall.auth.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 部门树 DTO
 * <p>
 * 用于 mall-admin-api 通过 Feign 调用获取部门树数据
 *
 * @date 2024/1/27 下午5:28
 */
@Schema(description = "部门树 DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeptTreeDTO {

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
     * 上级部门
     */
    @Schema(description = "上级部门", example = "0")
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
     * 部门层级
     */
    @Schema(description = "部门层级", example = "1")
    private Integer level;

    /**
     * 子部门
     */
    @Schema(description = "子部门")
    private List<DeptTreeDTO> children;

    /**
     * 增加添加子部门的方法
     *
     * @param deptTreeDTO 子部门
     */
    public void addChildren(DeptTreeDTO deptTreeDTO) {
        if (Objects.isNull(children)) {
            children = Lists.newArrayList();
        }
        children.add(deptTreeDTO);
    }
}
