package org.cclemon.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "`user`")
@Data
public class User implements UserDetails {

    /**
     * 唯一識別使用者的 ID（可用 UUID）
     * 建議用於 sub 的來源，永不變動
     */
    @Id
    private String id;

    /**
     * 使用者名稱（本地帳號會使用，第三方登入可選擇映射）
     */
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    /**
     * 密碼（若為第三方登入帳號可以為 null）
     */
    @JsonIgnore
    private String password;

    /**
     * 使用者 Email，常用於登入、找回密碼、通知等
     */
    @Column(unique = true, length = 200)
    private String email;

    /**
     * 是否已驗證 Email（註冊流程中常見）
     */
    private boolean emailVerified = false;

    /**
     * 手機號碼（可選，若要支援 OTP 登入、二階段驗證）
     */
    @Column(length = 20)
    private String phoneNumber;

    /**
     * 是否已驗證手機
     */
    private boolean phoneNumberVerified = false;

    /**
     * 使用者來源：
     * - LOCAL：本地註冊
     * - GOOGLE / GITHUB / FACEBOOK ...：OIDC / OAuth2 登入
     */
    @Column(length = 50)
    private String provider;

    /**
     * 第三方登入的唯一識別子（例如 sub 或 id）
     * 若是本地帳號可以為 null
     */
    @Column(length = 200)
    private String providerId;

    /**
     * 使用者顯示名稱（可和 username 不同）
     */
    @Column(length = 100)
    private String displayName;

    /**
     * 使用者國家 / 地區（例如 "TW", "JP", "US"）
     */
    @Column(length = 10)
    private String country;

    /**
     * 頭像 URL（第三方登入通常會提供）
     */
    @Column(length = 500)
    private String avatarUrl;

    /**
     * 是否啟用帳號
     */
    private boolean enabled = true;

    /**
     * 是否被鎖定（例如連續登入失敗）
     */
    private boolean accountLocked = false;

    /**
     * 註冊時間
     */
    private Instant createdAt = Instant.now();

    /**
     * 最後登入時間（可用於安全監控與統計）
     */
    private Instant lastLoginAt;

    // ===== UserDetails 介面實作 =====

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }
}
