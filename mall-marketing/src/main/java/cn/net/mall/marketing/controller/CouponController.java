package cn.net.mall.marketing.controller;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.marketing.entity.CouponConditionEntity;
import cn.net.mall.marketing.entity.CouponEntity;
import cn.net.mall.marketing.entity.web.CouponWebEntity;
import cn.net.mall.marketing.dto.CouponReceiveDTO;
import cn.net.mall.marketing.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券 接口层
 *
 * @date 2024-09-13 15:38:33
 */
@AllArgsConstructor
@RestController
@RequestMapping("/v1/coupon")
@Tag(name = "优惠券管理", description = "管理后台：优惠券配置、发放")
public class CouponController {

	private final CouponService couponService;

	/**
	 * 通过id查询优惠券信息
	 *
	 * @param id 系统ID
	 * @return 优惠券信息
	 */
	@GetMapping("/findById")
	public CouponEntity findById(Long id) {
		return couponService.findById(id);
	}

	/**
    * 根据条件查询优惠券列表
    *
    * @param couponConditionEntity 条件
    * @return 优惠券列表
    */
	@PostMapping("/searchByPage")
	public ResponsePageEntity<CouponEntity> searchByPage(@RequestBody CouponConditionEntity couponConditionEntity) {
		return couponService.searchByPage(couponConditionEntity);
	}


	/**
     * 添加优惠券
     *
     * @param couponEntity 优惠券实体
     * @return 影响行数
     */
	@PostMapping("/insert")
	public int insert(@RequestBody CouponEntity couponEntity) {
		return couponService.insert(couponEntity);
	}

	/**
     * 修改优惠券
     *
     * @param couponEntity 优惠券实体
     * @return 影响行数
     */
	@PostMapping("/update")
	public int update(@RequestBody CouponEntity couponEntity) {
		return couponService.update(couponEntity);
	}

	/**
     * 批量删除优惠券
     *
     * @param ids 优惠券ID集合
     * @return 影响行数
     */
	@PostMapping("/deleteByIds")
	public int deleteByIds(@RequestBody @NotNull List<Long> ids) {
		return couponService.deleteByIds(ids);
	}

    /**
     * 获取可领取的优惠券列表
     *
     * @return 优惠券列表
     */
    @GetMapping("/getObtainableCouponList")
    public List<CouponWebEntity> getObtainableCouponList() {
        return couponService.getObtainableCouponList();
    }

    /**
     * 获取某用户已经领取的优惠券列表
     *
     * @return 商品列表
     */
    @GetMapping("/getUserCouponList")
    public List<CouponWebEntity> getUserCouponList() {
        return couponService.getUserCouponList();
    }

    /**
     * 领取优惠券
     *
     * @param req 请求参数
     */
    @PostMapping("/receiveCoupon")
    public void receiveCoupon(@RequestBody CouponReceiveDTO req) {
        CouponWebEntity entity = new CouponWebEntity();
        entity.setId(req.getId());
        couponService.receiveCoupon(entity);
    }
}
