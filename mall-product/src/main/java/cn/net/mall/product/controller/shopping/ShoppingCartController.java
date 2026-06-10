package cn.net.mall.product.controller.shopping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import cn.net.mall.product.dto.ShoppingCartConditionDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.entity.shopping.ShoppingCartConditionEntity;
import cn.net.mall.product.entity.shopping.ShoppingCartEntity;
import cn.net.mall.product.entity.shopping.web.ShoppingCartWebEntity;
import cn.net.mall.product.service.shopping.ShoppingCartService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import cn.hutool.core.bean.BeanUtil;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;

/**
 * 购物车 接口层
 *
 * @date 2024-08-30 18:03:40
 */
@RestController
@RequestMapping("/v1/shoppingCart")
public class ShoppingCartController {

	private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

	/**
	 * 通过id查询购物车信息
	 *
	 * @param id 系统ID
	 * @return 购物车信息
	 */
	@GetMapping("/findById")
	public ShoppingCartEntity findById(Long id) {
		return shoppingCartService.findById(id);
	}

	/**
    * 根据条件查询购物车列表
    *
    * @param shoppingCartConditionEntity 条件
    * @return 购物车列表
    */
	@PostMapping("/searchByPage")
	public ResponsePageEntity<ShoppingCartEntity> searchByPage(@RequestBody ShoppingCartConditionEntity shoppingCartConditionEntity) {
		return shoppingCartService.searchByPage(shoppingCartConditionEntity);
	}

	/**
     * 添加购物车
     *
     * @param shoppingCartEntity 购物车实体
     * @return 影响行数
     */
	@PostMapping("/insert")
	public int insert(@RequestBody ShoppingCartEntity shoppingCartEntity) {
		return shoppingCartService.insert(shoppingCartEntity);
	}

	/**
     * 修改购物车
     *
     * @param shoppingCartEntity 购物车实体
     * @return 影响行数
     */
	@PostMapping("/update")
	public int update(@RequestBody ShoppingCartEntity shoppingCartEntity) {
		return shoppingCartService.update(shoppingCartEntity);
	}

	/**
     * 添加购物车
     *
     * @param shoppingCartDTO 购物车信息
     * @return 是否新增
     */
    @PostMapping("/addShoppingCart")
    public Boolean addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        return shoppingCartService.addShoppingCart(shoppingCartDTO);
    }

    /**
     * 修改购物车
     *
     * @param shoppingCartDTO 购物车信息
     */
    @PostMapping("/updateShoppingCart")
    public void updateShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.updateShoppingCart(shoppingCartDTO);
    }

    /**
     * 通过id集合批量查询购物车信息
     *
     * @param ids 购物车ID
     * @return 购物车信息
     */
    @PostMapping("/findByIds")
    public List<ShoppingCartDTO> findShoppingCartByIds(@RequestBody List<Long> ids) {
        List<ShoppingCartEntity> shoppingCartEntities = shoppingCartService.findByIds(ids);
        if (CollectionUtils.isEmpty(shoppingCartEntities)) {
            return Collections.emptyList();
        }
        return shoppingCartEntities.stream().map(x -> {
            ShoppingCartDTO shoppingCartDTO = new ShoppingCartDTO();
            BeanUtil.copyProperties(x, shoppingCartDTO);
            return shoppingCartDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 根据条件搜索购物车商品列表
     *
     * @param shoppingCartConditionDTO 条件
     * @return 购物车商品列表
     */
    @PostMapping("/getShoppingCartProduct")
    public ShoppingCartBuyDTO getShoppingCartProduct(@RequestBody ShoppingCartConditionDTO shoppingCartConditionDTO) {
        return shoppingCartService.getShoppingCartProduct(shoppingCartConditionDTO);
    }

    /**
     * 批量删除购物车
     *
     * @param ids 购物车ID集合
     * @return 影响行数
     */
    @PostMapping("/deleteShoppingCart")
    public int deleteShoppingCart(@RequestBody @NotNull List<Long> ids) {
        return shoppingCartService.deleteByIds(ids);
    }
}
