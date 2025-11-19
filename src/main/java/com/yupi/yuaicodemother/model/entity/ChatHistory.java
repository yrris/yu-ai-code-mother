package com.yupi.yuaicodemother.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史 实体类。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - JPA/Hibernate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_history", indexes = {
    @Index(name = "idx_chat_app_id", columnList = "app_id"),
    @Index(name = "idx_chat_create_time", columnList = "create_time"),
    @Index(name = "idx_chat_app_time", columnList = "app_id, create_time"),
    @Index(name = "idx_chat_user_id", columnList = "user_id")
})
@SQLDelete(sql = "UPDATE chat_history SET is_delete = 1 WHERE id = ?")
@SQLRestriction("is_delete = 0")
public class ChatHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * 消息类型: user/ai
     */
    @NotBlank(message = "消息类型不能为空")
    @Pattern(regexp = "^(user|ai)$", message = "消息类型必须是 user 或 ai")
    @Column(name = "message_type", nullable = false, length = 32)
    private String messageType;

    /**
     * 应用id
     */
    @NotNull(message = "应用ID不能为空")
    @Column(name = "app_id", nullable = false)
    private Long appId;

    /**
     * 应用关联 (Many-to-One)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_chat_app"))
    private App app;

    /**
     * 创建用户id
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户关联 (Many-to-One)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_chat_user"))
    private User user;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    /**
     * 是否删除 (软删除标记)
     */
    @Column(name = "is_delete", nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    private Integer isDelete;

    @PrePersist
    protected void onCreate() {
        if (isDelete == null) {
            isDelete = 0;
        }
    }
}
