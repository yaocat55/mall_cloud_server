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
 * 部门树实体
 *
 * @date 2024/1/27 下午5:28
 */
@Schema(name = "部门树实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeptTreeDTO {

    /**
     * 系统ID
     */
    @Schema(name = "系统ID")
    private Long id;

    /**
     * 名称
     */
    @Schema(name = "名称")
    private String name;

    /**
     * 前端页面显示用的标签名称
     */
    @Schema(name = "标签名称")
    private String label;

    /**
     * 上级部门
     */
    @Schema(name = "上级部门")
    private Long pid;

    /**
     * 有效状态 1:有效 0:无效
     */
    @Schema(name = "有效状态 1:有效 0:无效")
    private Integer validStatus;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private Date createTime;

    /**
     * 是否叶子节点
     */
    @Schema(name = "是否叶子节点")
    private Boolean leaf;


    /**
     * 是否有下一级
     */
    @Schema(name = "是否有下一级")
    private Boolean hasChildren;

    /**
     * 下一级数量
     */
    @Schema(name = "下一级数量")
    private Integer subCount;

    /**
     * 部门层级
     */
    @Schema(name = "部门层级")
    private Integer level;

    /**
     * 子部门
     */
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
