package cn.net.mall.marketing.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.CouponUserProvideConditionEntity;
import cn.net.mall.marketing.entity.CouponUserProvideEntity;
import cn.net.mall.marketing.service.CouponUserProvideService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券发放 接口层
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@RestController
@RequestMapping("/v1/couponUserProvide")
public class CouponUserProvideController {

	private final CouponUserProvideService couponUserProvideService;

	/**
	 * 通过id查询优惠券发放信息
	 *
	 * @param id 系统ID
	 * @return 优惠券发放信息
	 */
	@GetMapping("/findById")
	public CouponUserProvideEntity findById(Long id) {
		return couponUserProvideService.findById(id);
	}

	/**
    * 根据条件查询优惠券发放列表
    *
    * @param couponUserProvideConditionEntity 条件
    * @return 优惠券发放列表
    */
	@PostMapping("/searchByPage")
	public ResponsePageEntity<CouponUserProvideEntity> searchByPage(@RequestBody CouponUserProvideConditionEntity couponUserProvideConditionEntity) {
		return couponUserProvideService.searchByPage(couponUserProvideConditionEntity);
	}


	/**
     * 添加优惠券发放
     *
     * @param couponUserProvideEntity 优惠券发放实体
     * @return 影响行数
     */
	@PostMapping("/insert")
	public int insert(@RequestBody CouponUserProvideEntity couponUserProvideEntity) {
		return couponUserProvideService.insert(couponUserProvideEntity);
	}

	/**
     * 修改优惠券发放
     *
     * @param couponUserProvideEntity 优惠券发放实体
     * @return 影响行数
     */
	@PostMapping("/update")
	public int update(@RequestBody CouponUserProvideEntity couponUserProvideEntity) {
		return couponUserProvideService.update(couponUserProvideEntity);
	}

	/**
     * 批量删除优惠券发放
     *
     * @param ids 优惠券发放ID集合
     * @return 影响行数
     */
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return couponUserProvideService.deleteByIds(ids);
	}
}
