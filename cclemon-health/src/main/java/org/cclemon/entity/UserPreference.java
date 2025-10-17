package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_preference", uniqueConstraints = {
        @UniqueConstraint(name = "user_preference_user_id_unique", columnNames = "user_id")
})
@Data
public class UserPreference extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_preference_user_id_foreign"))
    private CclemonUser user;

    // Display Preferences
    @Column(name = "language")
    private String language = "zh-TW"; // zh-TW, en-US, ja-JP

    @Column(name = "theme")
    private String theme = "light"; // light, dark, auto

    @Column(name = "date_format")
    private String dateFormat = "yyyy-MM-dd"; // yyyy-MM-dd, MM/dd/yyyy, dd/MM/yyyy

    @Column(name = "time_format")
    private String timeFormat = "24h"; // 12h, 24h

    // Unit Preferences
    @Column(name = "weight_unit")
    private String weightUnit = "kg"; // kg, lbs

    @Column(name = "height_unit")
    private String heightUnit = "cm"; // cm, ft

    @Column(name = "temperature_unit")
    private String temperatureUnit = "celsius"; // celsius, fahrenheit

    @Column(name = "distance_unit")
    private String distanceUnit = "km"; // km, miles

    // Notification Preferences
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;

    @Column(name = "reminder_sound")
    private Boolean reminderSound = true;

    @Column(name = "vibration")
    private Boolean vibration = true;

    // Privacy Preferences
    @Column(name = "data_sharing")
    private Boolean dataSharing = false;

    @Column(name = "analytics_tracking")
    private Boolean analyticsTracking = true;

    @Column(name = "profile_visibility")
    private String profileVisibility = "private"; // public, private, friends

    // Health Preferences
    @Column(name = "auto_sync")
    private Boolean autoSync = true;

    @Column(name = "backup_enabled")
    private Boolean backupEnabled = true;

    @Column(name = "goal_reminders")
    private Boolean goalReminders = true;

    @Column(name = "weekly_reports")
    private Boolean weeklyReports = true;

    @Column(name = "monthly_reports")
    private Boolean monthlyReports = true;

    // Custom Settings (JSON)
    @Lob
    @Column(name = "custom_settings")
    private String customSettings; // JSON object for additional custom preferences
}