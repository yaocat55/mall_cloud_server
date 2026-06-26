package cn.net.mall.product.service;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.exception.BusinessException;
import cn.net.mall.workid.IdGenerateHelper;
import cn.net.mall.auth.client.UserFeignClient;
import cn.net.mall.auth.dto.UserDTO;
import cn.net.mall.mapper.BaseMapper;
import cn.net.mall.product.dto.ProductCommentDTO;
import cn.net.mall.product.dto.ProductCommentSubmitDTO;
import cn.net.mall.product.entity.ProductCommentConditionEntity;
import cn.net.mall.product.entity.ProductCommentEntity;
import cn.net.mall.product.entity.ProductCommentPhotoEntity;
import cn.net.mall.product.entity.ProductEntity;
import cn.net.mall.product.entity.web.ProductCommentWebEntity;
import cn.net.mall.product.enums.ProductCommentTypeEnum;
import cn.net.mall.product.mapper.ProductCommentMapper;
import cn.net.mall.product.mapper.ProductCommentPhotoMapper;
import cn.net.mall.product.mapper.ProductMapper;
import cn.net.mall.service.BaseService;
import cn.net.mall.util.AssertUtil;
import cn.net.mall.util.FillUserUtil;
import cn.net.mall.util.DateFormatUtil;
import cn.net.mall.product.dto.OrderTradeProductCommentDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 商品评论 服务层
 *
 * @date 2024-08-31 15:50:38
 */
@AllArgsConstructor
@Service
public class ProductCommentService extends BaseService<ProductCommentEntity, ProductCommentConditionEntity> {

    private final ProductCommentMapper productCommentMapper;
    private final ProductMapper productMapper;
    private final IdGenerateHelper idGenerateHelper;
    private final UserFeignClient userFeignClient;
    private final ProductCommentPhotoMapper productCommentPhotoMapper;

    private int getCommentType(int rating) {
        if (rating < 2) {
            return ProductCommentTypeEnum.NEGATIVE.getValue();
        } else if (rating >= 2 && rating < 4) {
            return ProductCommentTypeEnum.MODERATE.getValue();
        } else {
            return ProductCommentTypeEnum.POSITIVE.getValue();
        }
    }

    /**
     * 查询商品评论信息
     *
     * @param id 商品评论ID
     * @return 商品评论信息
     */
    public ProductCommentEntity findById(Long id) {
        return productCommentMapper.findById(id);
    }


    /**
     * 根据条件搜索商品评论列表
     *
     * @param productCommentConditionEntity 条件
     * @return 商品评论列表
     */
    public ResponsePageEntity<ProductCommentWebEntity> searchProductComment(ProductCommentConditionEntity productCommentConditionEntity) {
        if (Objects.nonNull(productCommentConditionEntity.getType()) && productCommentConditionEntity.getType() == 0) {
            productCommentConditionEntity.setType(null);
        }
        int count = productCommentMapper.searchCount(productCommentConditionEntity);
        if (count == 0) {
            return ResponsePageEntity.buildEmpty(productCommentConditionEntity);
        }
        List<ProductCommentEntity> entities = productCommentMapper.searchByCondition(productCommentConditionEntity);
        List<ProductCommentWebEntity> webList = toWebList(entities);
        return ResponsePageEntity.build(productCommentConditionEntity, count, webList);
    }

    private List<ProductCommentWebEntity> toWebList(List<ProductCommentEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>(0);
        }
        List<Long> userIds = entities.stream().map(ProductCommentEntity::getUserId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, UserDTO> userMap = getUserMap(userIds);
        List<Long> commentIds = entities.stream().map(ProductCommentEntity::getId).filter(Objects::nonNull).collect(Collectors.toList());
        final Map<Long, List<String>> photosMap;
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(commentIds)) {
            cn.net.mall.product.entity.ProductCommentPhotoConditionEntity c = new cn.net.mall.product.entity.ProductCommentPhotoConditionEntity();
            c.setCommentIdList(commentIds);
            c.setPageNo(0);
            List<cn.net.mall.product.entity.ProductCommentPhotoEntity> photos = productCommentPhotoMapper.searchByCondition(c);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(photos)) {
                photosMap = photos.stream().collect(Collectors.groupingBy(cn.net.mall.product.entity.ProductCommentPhotoEntity::getCommentId,
                        Collectors.mapping(cn.net.mall.product.entity.ProductCommentPhotoEntity::getUrl, Collectors.toList())));
            } else {
                photosMap = java.util.Collections.emptyMap();
            }
        } else {
            photosMap = java.util.Collections.emptyMap();
        }
        return entities.stream().map(e -> {
            ProductCommentWebEntity w = new ProductCommentWebEntity();
            w.setId(e.getId());
            w.setProductId(e.getProductId());
            w.setParentId(e.getParentId());
            w.setUserId(e.getUserId());
            w.setUserName(e.getUserName());
            w.setContent(e.getContent());
            w.setRating(e.getRating());
            w.setType(e.getType());
            Date ct = e.getCreateTime();
            if (ct != null) {
                w.setCreateTimeStr(DateFormatUtil.parseToString(ct));
            }
            if (e.getUserId() != null) {
                UserDTO u = userMap.get(e.getUserId());
                if (u != null) {
                    w.setAvatar(u.getAvatarUrl());
                    w.setNickName(Objects.nonNull(u.getNickName()) ? u.getNickName() : u.getUserName());
                }
            }
            List<String> ph = photosMap.get(e.getId());
            if (ph != null) {
                w.setPhotos(ph);
            }
            return w;
        }).collect(Collectors.toList());
    }

    private Map<Long, UserDTO> getUserMap(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return java.util.Collections.emptyMap();
        }
        List<UserDTO> users = userFeignClient.findByIds(userIds);
        if (CollectionUtils.isEmpty(users)) {
            return java.util.Collections.emptyMap();
        }
        return users.stream().collect(Collectors.toMap(UserDTO::getId, x -> x, (a, b) -> a));
    }

    /**
     * 根据条件分页查询商品评论列表
     *
     * @param productCommentConditionEntity 商品评论信息
     * @return 商品评论集合
     */
    public ResponsePageEntity<ProductCommentEntity> searchByPage(ProductCommentConditionEntity productCommentConditionEntity) {
        return super.searchByPage(productCommentConditionEntity);
    }

    /**
     * 新增商品评论
     *
     * @param productCommentEntity 商品评论信息
     * @return 结果
     */
    public int insert(ProductCommentEntity productCommentEntity) {
        checkParam(productCommentEntity);
        return productCommentMapper.insert(productCommentEntity);
    }

    /**
     * 修改商品评论
     *
     * @param productCommentEntity 商品评论信息
     * @return 结果
     */
    public int update(ProductCommentEntity productCommentEntity) {
        checkParam(productCommentEntity);
        return productCommentMapper.update(productCommentEntity);
    }

    public int batchInsert(List<ProductCommentEntity> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        for (ProductCommentEntity entity : list) {
            checkParam(entity);
            if (entity.getId() == null) {
                entity.setId(idGenerateHelper.nextId());
            }
        }
        return productCommentMapper.batchInsert(list);
    }

    public void saveProductComment(OrderTradeProductCommentDTO orderTradeProductCommentDTO) {
        AssertUtil.notEmpty(orderTradeProductCommentDTO.getProductCommentList(), "商品评价列表不能为空");
        Long orderId = orderTradeProductCommentDTO.getOrderId();
        AssertUtil.notNull(orderId, "订单ID不能为空");
        List<ProductCommentSubmitDTO> submitList = orderTradeProductCommentDTO.getProductCommentList();
        List<ProductCommentEntity> comments = new ArrayList<>();
        List<ProductCommentPhotoEntity> photos = new ArrayList<>();
        Long currentUserId = FillUserUtil.getCurrentUserInfo().getId();
        String currentUserName = FillUserUtil.getCurrentUserInfo().getUsername();
        for (cn.net.mall.product.dto.ProductCommentSubmitDTO web : submitList) {
            Integer type = web.getType();
            if (type == null) {
                int rating = web.getRating() == null ? 0 : web.getRating();
                type = getCommentType(rating);
            }

            ProductCommentEntity comment = new ProductCommentEntity();
            Long cid = idGenerateHelper.nextId();
            comment.setId(cid);
            comment.setProductId(web.getProductId());
            comment.setContent(web.getContent());
            comment.setRating(web.getRating());
            comment.setType(type);
            comment.setParentId(0L);
            comment.setOrderId(orderId);
            comment.setUserId(currentUserId);
            comment.setUserName(currentUserName);
            comments.add(comment);

            if (CollectionUtils.isNotEmpty(web.getPhotos())) {
                int idx = 0;
                for (String url : web.getPhotos()) {
                    ProductCommentPhotoEntity p = new ProductCommentPhotoEntity();
                    p.setId(idGenerateHelper.nextId());
                    p.setCommentId(cid);
                    p.setProductId(web.getProductId());
                    p.setUrl(url);
                    p.setSort(idx++);
                    photos.add(p);
                }
            }
        }
        if (comments.isEmpty()) {
            throw new BusinessException("评价内容为空");
        }
        batchInsert(comments);
        if (CollectionUtils.isNotEmpty(photos)) {
            productCommentPhotoMapper.batchInsert(photos);
        }
    }

    private void checkParam(ProductCommentEntity productCommentEntity) {
        if (Objects.nonNull(productCommentEntity.getParentId()) && productCommentEntity.getParentId() > 0) {
            ProductEntity productEntity = productMapper.findById(productCommentEntity.getParentId());
            AssertUtil.notNull(productEntity, "该父评论在系统中不存在");
        }

        ProductEntity productEntity = productMapper.findById(productCommentEntity.getProductId());
        AssertUtil.notNull(productEntity, "该商品在系统中不存在");
    }

    /**
     * 批量删除商品评论
     *
     * @param ids 系统ID集合
     * @return 结果
     */
    public int deleteByIds(List<Long> ids) {
        List<ProductCommentEntity> entities = productCommentMapper.findByIds(ids);
        AssertUtil.notEmpty(entities, "商品评论已被删除");

        ProductCommentEntity entity = new ProductCommentEntity();
        FillUserUtil.fillUpdateUserInfo(entity);
        return productCommentMapper.deleteByIds(ids, entity);
    }

    @Override
    protected BaseMapper getBaseMapper() {
        return productCommentMapper;
    }
}
