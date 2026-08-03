package cn.net.mall.pay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import cn.net.mall.entity.RequestConditionEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 对账批次查询条件实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "查询条件实体")
@EqualsAndHashCode(callSuper = true)
@Data
public class ReconBatchConditionEntity extends RequestConditionEntity {


	/**
	 *  主键，雪花ID
     */
	@Schema(description = "主键，雪花ID")
	private Long id;

	/**
	 *  批次号：RECON{yyyyMMdd}{渠道}{序号}
     */
	@Schema(description = "批次号：RECON{yyyyMMdd}{渠道}{序号}")
	private String batchNo;

	/**
	 *  渠道编码
     */
	@Schema(description = "渠道编码")
	private String channelCode;

	/**
	 *  对账日
     */
	@Schema(description = "对账日")
	private Date tradeDate;

	/**
	 *  对账单文件名
     */
	@Schema(description = "对账单文件名")
	private String fileName;

	/**
	 *  文件存储路径（MinIO）
     */
	@Schema(description = "文件存储路径（MinIO）")
	private String filePath;

	/**
	 *  渠道侧交易总笔数
     */
	@Schema(description = "渠道侧交易总笔数")
	private Integer channelCount;

	/**
	 *  渠道侧交易总额（分）
     */
	@Schema(description = "渠道侧交易总额（分）")
	private Long channelTotalAmount;

	/**
	 *  本地匹配交易笔数
     */
	@Schema(description = "本地匹配交易笔数")
	private Integer platformCount;

	/**
	 *  本地匹配交易总额（分）
     */
	@Schema(description = "本地匹配交易总额（分）")
	private Long platformTotalAmount;

	/**
	 *  差异笔数
     */
	@Schema(description = "差异笔数")
	private Integer diffCount;

	/**
	 *  差异总金额（分）
     */
	@Schema(description = "差异总金额（分）")
	private Long diffTotalAmount;

	/**
	 *  状态：1已下载 2已解析入库 3对账中 4对账完成 5有差异待处理 6差异已处理 7失败
     */
	@Schema(description = "状态：1已下载 2已解析入库 3对账中 4对账完成 5有差异待处理 6差异已处理 7失败")
	private Integer status;

	/**
	 *  对账开始时间
     */
	@Schema(description = "对账开始时间")
	private Date beginTime;

	/**
	 *  对账结束时间
     */
	@Schema(description = "对账结束时间")
	private Date endTime;

	/**
	 *  失败原因
     */
	@Schema(description = "失败原因")
	private String errorMsg;

	/**
	 *  乐观锁
     */
	@Schema(description = "乐观锁")
	private Integer version;

	/**
	 *  创建人ID
     */
	@Schema(description = "创建人ID")
	private Long createUserId;

	/**
	 *  创建人姓名
     */
	@Schema(description = "创建人姓名")
	private String createUserName;

	/**
	 *  创建时间
     */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 *  更新人ID
     */
	@Schema(description = "更新人ID")
	private Long updateUserId;

	/**
	 *  更新人姓名
     */
	@Schema(description = "更新人姓名")
	private String updateUserName;

	/**
	 *  更新时间
     */
	@Schema(description = "更新时间")
	private Date updateTime;
}
