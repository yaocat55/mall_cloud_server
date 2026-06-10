package cn.net.mall.auth.mapper.auth;

import cn.net.mall.auth.entity.auth.UserConditionEntity;
import cn.net.mall.auth.entity.auth.UserEntity;
import cn.net.mall.mapper.BaseMapper;

import java.util.List;

/**
 * 用户 mapper
 *
 * @date 2024-01-08 14:03:43
 */
public interface UserMapper extends BaseMapper<UserEntity, UserConditionEntity> {
    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserEntity findById(Long id);

    /**
     * 根据用户名称查询用户信息
     *
     * @param userName 用户名称
     * @return 用户信息
     */
    UserEntity findByUserName(String userName);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    UserEntity findByPhone(String phone);

    /**
     * 添加用户
     *
     * @param userEntity 用户信息
     * @return 结果
     */
    int insert(UserEntity userEntity);

    /**
     * 修改用户
     *
     * @param userEntity 用户信息
     * @return 结果
     */
    int update(UserEntity userEntity);

    /**
     * 批量查询用户信息
     *
     * @param ids 用户ID
     * @return 用户信息
     */
    List<UserEntity> findByIds(List<Long> ids);

    /**
     * 删除用户
     *
     * @param ids    用户ID
     * @param entity 用户实体
     * @return 结果
     */
    int deleteByIds(List<Long> ids, UserEntity entity);

    /**
     * 批量更新用户密码
     *
     * @param list 用户集合
     * @return 影响行数
     */
    int updateForBatch(List<UserEntity> list);
}
