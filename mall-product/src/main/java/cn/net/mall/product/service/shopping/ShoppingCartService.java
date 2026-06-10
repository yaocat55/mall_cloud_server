package cn.net.mall.product.service.shopping;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.net.mall.product.dto.ShoppingCartBuyDTO;
import cn.net.mall.product.dto.ShoppingCartConditionDTO;
import cn.net.mall.product.dto.ShoppingCartDTO;
import cn.net.mall.product.entity.ProductPhotoConditionEntity;
import cn.net.mall.product.entity.ProductPhotoEntity;
// import cn.net.mall.entity.marketing.CouponConditionEntity;
// import cn.net.mall.entity.marketing.CouponEntity;
// import cn.net.mall.entity.marketing.CouponUserProvideConditionEntity;
// import cn.net.mall.entity.marketing.CouponUserProvideEntity;
// import cn.net.mall.entity.marketing.CouponUserReceiveConditionEntity;
// import cn.net.mall.entity.marketing.CouponUserReceiveEntity;
// import cn.net.mall.entity.marketing.web.CouponWebEntity;
import cn.net.mall.product.entity.shopping.web.CouponGroupProductWebEntity;
import cn.net.mall.product.entity.shopping.web.ShoppingCartProductWebEntity;
import cn.net.mall.product.entity.shopping.web.ShoppingCartWebEntity;
import cn.net.mall.product.entity.shopping.web.CouponWebEntity; // Local mock/stub
// import cn.net.mall.enums.CouponTypeEnum;
import cn.net.mall.product.enums.PhotoTypeEnum;
import cn.net.mall.product.helper.UserProductHelper;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.mapper.ProductPhotoMapper;
// import cn.net.mall.mapper.marketing.CouponMapper;
// import cn.net.mall.mapper.marketing.CouponUserProvideMapper;
// import cn.net.mall.mapper.marketing.CouponUserReceiveMapper;
import cn.net.mall.service.BaseService;
// import cn.net.mall.service.marketing.strategy.CouponContext;
// import cn.net.mall.service.order.TradeService;
// import cn.net.mall.util.CouponUtil;
import cn.net.mall.util.UuidUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.net.mall.product.mapper.shopping.ShoppingCartMapper;
import cn.net.mall.product.entity.shopping.ShoppingCartConditionEntity;
import cn.net.mall.product.entity.shopping.ShoppingCartEntity;
import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;

import static cn.net.mall.util.FillUserUtil.getCurrentUserInfo;

/**
 * 购物车 服务层
 *
 * @date 2024-08-30 18:03:40
 */
@Service
public class ShoppingCartService extends BaseService<ShoppingCartEntity, ShoppingCartConditionEntity> {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserProductHelper userProductHelper;
    @Autowired
    private ProductPhotoMapper productPhotoMapper;

    // @Autowired
    // private CouponUserReceiveMapper couponUserReceiveMapper;
    // @Autowired
    // private CouponUserProvideMapper couponUserProvideMapper;
    // @Autowired
    // private CouponMapper couponMapper;
    // @Autowired
    // private TradeService tradeService;

    @Override
    protected BaseMapper getBaseMapper() {
        return shoppingCartMapper;
    }

    /**
     * 修改购物车
     *
     * @param shoppingCartDTO 购物车信息
     */
    public void updateShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCartEntity shoppingCartEntity = BeanUtil.toBean(shoppingCartDTO, ShoppingCartEntity.class);
        AssertUtil.notNull(shoppingCartEntity.getId(), "id不能为空");
        AssertUtil.notNull(shoppingCartEntity.getQuantity(), "quantity不能为空");

        FillUserUtil.fillUpdateUserInfo(shoppingCartEntity);
        shoppingCartMapper.update(shoppingCartEntity);
    }

    /**
     * 通过id集合批量查询购物车信息
     *
     * @param ids 购物车ID
     * @return 购物车信息
     */
    public List<ShoppingCartEntity> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setIdList(ids);
        shoppingCartConditionEntity.setPageSize(0);
        List<ShoppingCartEntity> list = shoppingCartMapper.searchByCondition(shoppingCartConditionEntity);
        userProductHelper.fillProductInfo(list);
        return list;
    }

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO 条件
     * @return 购物车商品列表
     */
    public Boolean addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCartEntity shoppingCartEntity = BeanUtil.toBean(shoppingCartDTO, ShoppingCartEntity.class);
        shoppingCartEntity.setUserId(FillUserUtil.getCurrentUserInfo().getId());

        // TODO: 调用库存服务检查库存
        // tradeService.checkStock(Lists.newArrayList(shoppingCartEntity));

        ShoppingCartConditionEntity shoppingCartConditionEntity = new ShoppingCartConditionEntity();
        shoppingCartConditionEntity.setUserId(shoppingCartEntity.getUserId());
        shoppingCartConditionEntity.setProductId(shoppingCartEntity.getProductId());
        List<ShoppingCartEntity> shoppingCartEntities = shoppingCartMapper.searchByCondition(shoppingCartConditionEntity);
        if (CollectionUtils.isNotEmpty(shoppingCartEntities)) {
            ShoppingCartEntity oldShoppingCartEntity = shoppingCartEntities.get(0);
            oldShoppingCartEntity.setQuantity(oldShoppingCartEntity.getQuantity() + shoppingCartEntity.getQuantity());
            FillUserUtil.fillUpdateUserInfo(oldShoppingCartEntity);
            shoppingCartMapper.update(oldShoppingCartEntity);
            return Boolean.FALSE;
        } else {
            shoppingCartMapper.insert(shoppingCartEntity);
            return Boolean.TRUE;
        }
    }

    /**
     * 根据条件搜索购物车商品列表
     *
     * @param shoppingCartConditionDTO 条件
     * @return 购物车商品列表
     */
    public ShoppingCartBuyDTO getShoppingCartProduct(ShoppingCartConditionDTO shoppingCartConditionDTO) {
        ShoppingCartConditionEntity shoppingCartConditionEntity = BeanUtil.toBean(shoppingCartConditionDTO, ShoppingCartConditionEntity.class);
        ShoppingCartWebEntity shoppingCartWebEntity = new ShoppingCartWebEntity();

        shoppingCartConditionEntity.setUserId(FillUserUtil.getCurrentUserInfo().getId());
        shoppingCartConditionEntity.setPageSize(0);
        ResponsePageEntity<ShoppingCartEntity> responsePageEntity = super.searchByPage(shoppingCartConditionEntity);
        //填充商品信息
        userProductHelper.fillProductInfo(responsePageEntity.getData());
        List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities = convertShoppingCartProductWebEntity(responsePageEntity);
        //填充图片
        fillCover(shoppingCartProductWebEntities);
        //计算每个商品总金额
        calcTotalAmount(shoppingCartWebEntity, shoppingCartProductWebEntities);
        //计算优惠金额
        // calcCouponDiscount(shoppingCartWebEntity, shoppingCartProductWebEntities); // TODO: 恢复优惠计算逻辑

        // 暂时简单包装返回，因为没有 calcCouponDiscount 处理分组
        CouponGroupProductWebEntity group = new CouponGroupProductWebEntity();
        group.setShoppingCartList(shoppingCartProductWebEntities);
        shoppingCartWebEntity.setCouponGroupProductWebEntityList(Lists.newArrayList(group));

        //计算最终支付金额
        calcFinalMoney(shoppingCartWebEntity);
        return BeanUtil.toBean(shoppingCartWebEntity, ShoppingCartBuyDTO.class);
    }

    // 添加 convertShoppingCartProductWebEntity 方法，原代码中未直接展示，但从上下文推断应该是存在的，或者是基类方法？
    // 检查 BaseService 并没有这个方法。可能是源代码中漏掉了，或者是在其他地方。
    // 在原 ShoppingCartService.java 中我也没看到 convertShoppingCartProductWebEntity 的定义。
    // 让我再检查一下原文件。

    private List<ShoppingCartProductWebEntity> convertShoppingCartProductWebEntity(ResponsePageEntity<ShoppingCartEntity> responsePageEntity) {
        if (CollectionUtils.isEmpty(responsePageEntity.getData())) {
            return Lists.newArrayList();
        }
        return responsePageEntity.getData().stream().map(entity -> {
            ShoppingCartProductWebEntity webEntity = new ShoppingCartProductWebEntity();
            BeanUtil.copyProperties(entity, webEntity);
            webEntity.setUuid(UuidUtil.getUuid()); // 确保 UUID 生成
            return webEntity;
        }).collect(Collectors.toList());
    }

    private void fillCover(List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities) {
        if (CollectionUtils.isEmpty(shoppingCartProductWebEntities)) {
            return;
        }
        List<Long> productIds = shoppingCartProductWebEntities.stream().map(ShoppingCartProductWebEntity::getProductId).distinct().collect(Collectors.toList());
        ProductPhotoConditionEntity condition = new ProductPhotoConditionEntity();
        condition.setProductIdList(productIds);
        condition.setType(PhotoTypeEnum.COVER.getValue());
        List<ProductPhotoEntity> photos = productPhotoMapper.searchByCondition(condition);
        Map<Long, String> photoMap = photos.stream().collect(Collectors.toMap(ProductPhotoEntity::getProductId, ProductPhotoEntity::getUrl, (k1, k2) -> k1));

        shoppingCartProductWebEntities.forEach(item -> {
            if (photoMap.containsKey(item.getProductId())) {
                item.setCover(photoMap.get(item.getProductId()));
            }
        });
    }

    /*
    private void calcCouponDiscount(ShoppingCartWebEntity shoppingCartWebEntity, List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities) {
       // ... 省略 ...
    }
    */

    /*
    private List<CouponGroupProductWebEntity> calcUnionShoppingCartProduct(List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities,
                                                                           List<CouponGroupProductWebEntity> couponGroupProductEntities) {
       // ... 省略 ...
    }
    */

    private BigDecimal getAmount(BigDecimal price, int quantity) {
        if (price == null) return BigDecimal.ZERO;
        return price.multiply(new BigDecimal(quantity));
    }

    /*
    private CouponGroupProductWebEntity addNoCouponProduct(List<ShoppingCartProductWebEntity> allCouponProductWebEntities,
                                                           List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities) {
       // ... 省略 ...
    }
    */

    /*
    private void fillCouponInfo(List<CouponUserReceiveEntity> couponUserReceiveEntities,
                                List<CouponEntity> couponEntities,
                                List<CouponUserProvideEntity> couponUserProvideEntities) {
       // ... 省略 ...
    }
    */

    private void calcTotalAmount(ShoppingCartWebEntity shoppingCartWebEntity, List<ShoppingCartProductWebEntity> shoppingCartProductWebEntities) {
        BigDecimal totalMoney = BigDecimal.ZERO;
        for (ShoppingCartProductWebEntity shoppingCartProductWebEntity : shoppingCartProductWebEntities) {
            BigDecimal totalAmount = getAmount(shoppingCartProductWebEntity.getPrice(), shoppingCartProductWebEntity.getQuantity());
            shoppingCartProductWebEntity.setTotalAmount(totalAmount);
            shoppingCartProductWebEntity.setPayAmount(totalAmount);
            shoppingCartProductWebEntity.setPayPrice(shoppingCartProductWebEntity.getPrice());
            totalMoney = totalMoney.add(totalAmount);
        }
        shoppingCartWebEntity.setTotalMoney(totalMoney);
    }

    private void calcFinalMoney(ShoppingCartWebEntity shoppingCartWebEntity) {
        BigDecimal totalMoney = shoppingCartWebEntity.getTotalMoney();
        BigDecimal subtractMoney = shoppingCartWebEntity.getSubtractMoney();
        if (subtractMoney == null) subtractMoney = BigDecimal.ZERO;
        shoppingCartWebEntity.setFinalMoney(totalMoney.subtract(subtractMoney));
    }

    // 补充 insert 和 update 等方法，因为 Controller 调用了 insert, update, deleteByIds, findById

    public int insert(ShoppingCartEntity shoppingCartEntity) {
        return shoppingCartMapper.insert(shoppingCartEntity);
    }

    public int update(ShoppingCartEntity shoppingCartEntity) {
        return shoppingCartMapper.update(shoppingCartEntity);
    }

    public int deleteByIds(List<Long> ids) {
        // 原 Service 没有 deleteByIds 方法显式定义？可能是 BaseService 的？
        // 但是 Controller 调用了 shoppingCartService.deleteByIds(ids)
        // 检查 ShoppingCartMapper 有 deleteByIds
        // 如果 BaseService 有 deleteByIds，那就可以。
        // 原 ShoppingCartService 没有 deleteByIds 方法。
        // 假设 BaseService 没有支持 List<Long> ids 的 deleteByIds，这里可能需要自己实现。
        // 根据 Mapper 定义：int deleteByIds(@Param("ids") List<Long> ids, @Param("entity") ShoppingCartEntity entity);

        ShoppingCartEntity entity = new ShoppingCartEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return shoppingCartMapper.deleteByIds(ids, entity);
    }

    public ShoppingCartEntity findById(Long id) {
        return shoppingCartMapper.findById(id);
    }
}
