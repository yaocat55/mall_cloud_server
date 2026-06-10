package cn.net.mall.marketing.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.CouponUserReceiveConditionEntity;
import cn.net.mall.marketing.entity.CouponUserReceiveEntity;
import cn.net.mall.marketing.service.CouponUserReceiveService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券领取 接口层
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@RestController
@RequestMapping("/v1/couponUserReceive")
public class CouponUserReceiveController {

	private final CouponUserReceiveService couponUserReceiveService;

	/**
	 * 通过id查询优惠券领取信息
	 *
	 * @param id 系统ID
	 * @return 优惠券领取信息
	 */
	@GetMapping("/findById")
	public CouponUserReceiveEntity findById(Long id) {
		return couponUserReceiveService.findById(id);
	}

	/**
    * 根据条件查询优惠券领取列表
    *
    * @param couponUserReceiveConditionEntity 条件
    * @return 优惠券领取列表
    */
	@PostMapping("/searchByPage")
	public ResponsePageEntity<CouponUserReceiveEntity> searchByPage(@RequestBody CouponUserReceiveConditionEntity couponUserReceiveConditionEntity) {
		return couponUserReceiveService.searchByPage(couponUserReceiveConditionEntity);
	}


	/**
     * 添加优惠券领取
     *
     * @param couponUserReceiveEntity 优惠券领取实体
     * @return 影响行数
     */
	@PostMapping("/insert")
	public int insert(@RequestBody CouponUserReceiveEntity couponUserReceiveEntity) {
		return couponUserReceiveService.insert(couponUserReceiveEntity);
	}

	/**
     * 修改优惠券领取
     *
     * @param couponUserReceiveEntity 优惠券领取实体
     * @return 影响行数
     */
	@PostMapping("/update")
	public int update(@RequestBody CouponUserReceiveEntity couponUserReceiveEntity) {
		return couponUserReceiveService.update(couponUserReceiveEntity);
	}

	/**
     * 批量删除优惠券领取
     *
     * @param ids 优惠券领取ID集合
     * @return 影响行数
     */
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return couponUserReceiveService.deleteByIds(ids);
	}
}
