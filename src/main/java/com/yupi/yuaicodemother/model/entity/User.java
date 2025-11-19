package com.yupi.yuaicodemother.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
 * 用户 实体类。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @author Refactored for Australian Tech Stack - JPA/Hibernate
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"", indexes = {
    @Index(name = "idx_user_name", columnList = "user_name"),
    @Index(name = "idx_user_role", columnList = "user_role")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_account", columnNames = "user_account")
})
@SQLDelete(sql = "UPDATE \"user\" SET is_delete = 1 WHERE id = ?")
@SQLRestriction("is_delete = 0")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 256, message = "用户账号长度不能超过256个字符")
    @Column(name = "user_account", nullable = false, length = 256, unique = true)
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 512, message = "密码长度不能超过512个字符")
    @Column(name = "user_password", nullable = false, length = 512)
    private String userPassword;

    /**
     * 用户昵称
     */
    @Size(max = 256, message = "用户昵称长度不能超过256个字符")
    @Column(name = "user_name", length = 256)
    private String userName;

    /**
     * 用户头像
     */
    @Size(max = 1024, message = "用户头像URL长度不能超过1024个字符")
    @Column(name = "user_avatar", length = 1024)
    private String userAvatar;

    /**
     * 用户简介
     */
    @Size(max = 512, message = "用户简介长度不能超过512个字符")
    @Column(name = "user_profile", length = 512)
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @NotBlank(message = "用户角色不能为空")
    @Column(name = "user_role", nullable = false, length = 256, columnDefinition = "VARCHAR(256) DEFAULT 'user'")
    private String userRole;

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
        if (userRole == null || userRole.isBlank()) {
            userRole = "user";
        }
    }
}
