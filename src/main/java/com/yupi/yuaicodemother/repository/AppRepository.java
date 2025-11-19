package com.yupi.yuaicodemother.repository;

import com.yupi.yuaicodemother.model.entity.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 应用 Repository (JPA Data Access Layer)
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - Spring Data JPA
 */
@Repository
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {

    /**
     * 根据部署标识查询应用
     *
     * @param deployKey 部署标识
     * @return 应用实体
     */
    Optional<App> findByDeployKey(String deployKey);

    /**
     * 检查部署标识是否存在
     *
     * @param deployKey 部署标识
     * @return 是否存在
     */
    boolean existsByDeployKey(String deployKey);

    /**
     * 根据用户ID查询应用列表 (分页)
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 应用分页结果
     */
    Page<App> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据用户ID查询应用数量
     *
     * @param userId 用户ID
     * @return 应用数量
     */
    long countByUserId(Long userId);

    /**
     * 查询优质应用列表 (按优先级降序)
     *
     * @param minPriority 最小优先级
     * @param pageable 分页参数
     * @return 应用分页结果
     */
    @Query("SELECT a FROM App a WHERE a.priority >= :minPriority ORDER BY a.priority DESC, a.createTime DESC")
    Page<App> findFeaturedApps(@Param("minPriority") Integer minPriority, Pageable pageable);

    /**
     * 根据应用名称模糊查询
     *
     * @param appName 应用名称
     * @param pageable 分页参数
     * @return 应用分页结果
     */
    Page<App> findByAppNameContaining(String appName, Pageable pageable);

    /**
     * 根据代码生成类型查询
     *
     * @param codeGenType 代码生成类型
     * @param pageable 分页参数
     * @return 应用分页结果
     */
    Page<App> findByCodeGenType(String codeGenType, Pageable pageable);

    /**
     * 查询已部署的应用列表
     *
     * @param pageable 分页参数
     * @return 应用分页结果
     */
    @Query("SELECT a FROM App a WHERE a.deployedTime IS NOT NULL ORDER BY a.deployedTime DESC")
    Page<App> findDeployedApps(Pageable pageable);

    /**
     * 获取用户的最新应用
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 应用列表
     */
    @Query("SELECT a FROM App a WHERE a.userId = :userId ORDER BY a.createTime DESC LIMIT :limit")
    List<App> findLatestByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
