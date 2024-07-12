package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public class BaseEntity {
    /**
     * Desc:PK
     * Column Name:id, Column Type:BIGINT, Nullable:N
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Desc:版本
     * Column Name:version, Column Type:INT, Nullable:N
     */
    @Version
    @Column(name = "version")
    private Integer version;

    /**
     * Desc:建立者
     * Column Name:create_user_id, Column Type:BIGINT, Nullable:N
     */
    @CreatedBy
    @Column(name = "create_user_id")
    private Long createUserId;

    /**
     * Desc:建立時間
     * Column Name:create_time, Column Type:DATETIME, Nullable:N
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * Desc:修改者
     * Column Name:last_modified_user_id, Column Type:INT, Nullable:N
     */
    @LastModifiedBy
    @Column(name = "last_modified_user_id")
    private Long lastModifiedUserId;

    /**
     * Desc:修改時間
     * Column Name:last_modified_time, Column Type:DATETIME, Nullable:N
     */
    @LastModifiedDate
    @Column(name = "last_modified_time")
    private LocalDateTime LastModifiedTime;

    /**
     * Desc:是否刪除
     * Column Name:deleted, Column Type:BIT, Nullable:N
     */
    @Column(name = "deleted")
    private Boolean deleted;
}
