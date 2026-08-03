package cn.net.mall.pay.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import cn.net.mall.entity.BaseEntity;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 对账批次实体
 *
 * @author yaomingye
 * @date 2026-08-03 15:35:10
 */
@Schema(description = "对账批次实体")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ReconBatchEntity extends BaseEntity {


	/**
	 * 批次号：RECON{yyyyMMdd}{渠道}{序号}
	 */
	@Schema(description = "批次号：RECON{yyyyMMdd}{渠道}{序号}")
	private String batchNo;

	/**
	 * 渠道编码
	 */
	@Schema(description = "渠道编码")
	private String channelCode;

	/**
	 * 对账日
	 */
	@Schema(description = "对账日")
	private Date tradeDate;

	/**
	 * 对账单文件名
	 */
	@Schema(description = "对账单文件名")
	private String fileName;

	/**
	 * 文件存储路径（MinIO）
	 */
	@Schema(description = "文件存储路径（MinIO）")
	private String filePath;

	/**
	 * 渠道侧交易总笔数
	 */
	@Schema(description = "渠道侧交易总笔数")
	private Integer channelCount;

	/**
	 * 渠道侧交易总额（分）
	 */
	@Schema(description = "渠道侧交易总额（分）")
	private Long channelTotalAmount;

	/**
	 * 本地匹配交易笔数
	 */
	@Schema(description = "本地匹配交易笔数")
	private Integer platformCount;

	/**
	 * 本地匹配交易总额（分）
	 */
	@Schema(description = "本地匹配交易总额（分）")
	private Long platformTotalAmount;

	/**
	 * 差异笔数
	 */
	@Schema(description = "差异笔数")
	private Integer diffCount;

	/**
	 * 差异总金额（分）
	 */
	@Schema(description = "差异总金额（分）")
	private Long diffTotalAmount;

	/**
	 * 状态：1已下载 2已解析入库 3对账中 4对账完成 5有差异待处理 6差异已处理 7失败
	 */
	@Schema(description = "状态：1已下载 2已解析入库 3对账中 4对账完成 5有差异待处理 6差异已处理 7失败")
	private Integer status;

	/**
	 * 对账开始时间
	 */
	@Schema(description = "对账开始时间")
	private Date beginTime;

	/**
	 * 对账结束时间
	 */
	@Schema(description = "对账结束时间")
	private Date endTime;

	/**
	 * 失败原因
	 */
	@Schema(description = "失败原因")
	private String errorMsg;

	/**
	 * 乐观锁
	 */
	@Schema(description = "乐观锁")
	private Integer version;

}
