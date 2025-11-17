package com.yupi.yuaicodemother.repository;

import com.yupi.yuaicodemother.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户 Repository (JPA Data Access Layer)
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - Spring Data JPA
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户账号查询用户
     *
     * @param userAccount 用户账号
     * @return 用户实体
     */
    Optional<User> findByUserAccount(String userAccount);

    /**
     * 检查用户账号是否存在
     *
     * @param userAccount 用户账号
     * @return 是否存在
     */
    boolean existsByUserAccount(String userAccount);

    /**
     * 根据用户角色查询用户数量
     *
     * @param userRole 用户角色
     * @return 用户数量
     */
    long countByUserRole(String userRole);

    /**
     * 根据用户名模糊查询
     *
     * @param userName 用户名
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName%")
    java.util.List<User> findByUserNameContaining(@Param("userName") String userName);
}
