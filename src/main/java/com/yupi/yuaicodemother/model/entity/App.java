package com.yupi.yuaicodemother.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 * 应用 实体类。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - JPA/Hibernate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app", indexes = {
    @Index(name = "idx_app_name", columnList = "app_name"),
    @Index(name = "idx_app_user_id", columnList = "user_id"),
    @Index(name = "idx_app_priority", columnList = "priority"),
    @Index(name = "idx_app_code_gen_type", columnList = "code_gen_type"),
    @Index(name = "idx_app_create_time", columnList = "create_time")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_deploy_key", columnNames = "deploy_key")
})
@SQLDelete(sql = "UPDATE app SET is_delete = 1 WHERE id = ?")
@SQLRestriction("is_delete = 0")
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用名称
     */
    @Size(max = 256, message = "应用名称长度不能超过256个字符")
    @Column(name = "app_name", length = 256)
    private String appName;

    /**
     * 应用封面 (screenshot URL)
     */
    @Size(max = 512, message = "封面URL长度不能超过512个字符")
    @Column(name = "cover", length = 512)
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    @Column(name = "init_prompt", columnDefinition = "TEXT")
    private String initPrompt;

    /**
     * 代码生成类型（枚举）: html, multi_file, vue_project
     */
    @Size(max = 64, message = "代码生成类型长度不能超过64个字符")
    @Column(name = "code_gen_type", length = 64)
    private String codeGenType;

    /**
     * 部署标识
     */
    @Size(max = 64, message = "部署标识长度不能超过64个字符")
    @Column(name = "deploy_key", length = 64, unique = true)
    private String deployKey;

    /**
     * 部署时间
     */
    @Column(name = "deployed_time")
    private LocalDateTime deployedTime;

    /**
     * 优先级 (featured apps)
     */
    @Column(name = "priority", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer priority;

    /**
     * 创建用户id
     */
    @NotNull(message = "创建用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 用户关联 (Many-to-One)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_app_user"))
    private User user;

    /**
     * 编辑时间
     */
    @Column(name = "edit_time", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editTime;

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
        if (editTime == null) {
            editTime = LocalDateTime.now();
        }
        if (isDelete == null) {
            isDelete = 0;
        }
        if (priority == null) {
            priority = 0;
        }
    }
}
