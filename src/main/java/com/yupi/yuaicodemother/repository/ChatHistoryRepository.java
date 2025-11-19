package com.yupi.yuaicodemother.repository;

import com.yupi.yuaicodemother.model.entity.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 Repository (JPA Data Access Layer)
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - Spring Data JPA
 */
@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long>, JpaSpecificationExecutor<ChatHistory> {

    /**
     * 根据应用ID查询对话历史 (分页, 按时间倒序)
     *
     * @param appId 应用ID
     * @param pageable 分页参数
     * @return 对话历史分页结果
     */
    Page<ChatHistory> findByAppIdOrderByCreateTimeDesc(Long appId, Pageable pageable);

    /**
     * 根据应用ID和用户ID查询对话历史
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 对话历史分页结果
     */
    Page<ChatHistory> findByAppIdAndUserIdOrderByCreateTimeDesc(Long appId, Long userId, Pageable pageable);

    /**
     * 根据用户ID查询对话历史
     *
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 对话历史分页结果
     */
    Page<ChatHistory> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据应用ID查询对话数量
     *
     * @param appId 应用ID
     * @return 对话数量
     */
    long countByAppId(Long appId);

    /**
     * 根据应用ID和消息类型查询对话数量
     *
     * @param appId 应用ID
     * @param messageType 消息类型
     * @return 对话数量
     */
    long countByAppIdAndMessageType(Long appId, String messageType);

    /**
     * 查询应用的最新N条对话记录
     *
     * @param appId 应用ID
     * @param limit 数量限制
     * @return 对话历史列表
     */
    @Query("SELECT ch FROM ChatHistory ch WHERE ch.appId = :appId ORDER BY ch.createTime DESC LIMIT :limit")
    List<ChatHistory> findLatestByAppId(@Param("appId") Long appId, @Param("limit") int limit);

    /**
     * 查询应用在指定时间之后的对话记录 (游标分页)
     *
     * @param appId 应用ID
     * @param afterTime 起始时间
     * @param limit 数量限制
     * @return 对话历史列表
     */
    @Query("SELECT ch FROM ChatHistory ch WHERE ch.appId = :appId AND ch.createTime > :afterTime ORDER BY ch.createTime ASC")
    List<ChatHistory> findByAppIdAfterTime(
        @Param("appId") Long appId,
        @Param("afterTime") LocalDateTime afterTime,
        Pageable pageable
    );

    /**
     * 删除应用的所有对话历史
     *
     * @param appId 应用ID
     */
    @Modifying
    @Query("UPDATE ChatHistory ch SET ch.isDelete = 1 WHERE ch.appId = :appId")
    void softDeleteByAppId(@Param("appId") Long appId);

    /**
     * 删除指定时间之前的对话历史 (清理旧数据)
     *
     * @param beforeTime 截止时间
     */
    @Modifying
    @Query("UPDATE ChatHistory ch SET ch.isDelete = 1 WHERE ch.createTime < :beforeTime")
    void softDeleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
}
