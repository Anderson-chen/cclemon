package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "health_report")
@Data
public class HealthReport extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "health_report_user_id_foreign"))
    private CclemonUser user;

    @Column(name = "report_date", nullable = false)
    private String reportDate; // yyyy-MM-dd format

    @Column(name = "report_type", nullable = false)
    private String reportType; // weekly, monthly, quarterly, annual

    @Column(name = "date_range_start")
    private String dateRangeStart; // yyyy-MM-dd format

    @Column(name = "date_range_end")
    private String dateRangeEnd; // yyyy-MM-dd format

    @Column(nullable = false)
    private String title;

    @Lob
    private String summary;

    // Weight Analysis
    @Column(name = "avg_weight")
    private Double avgWeight;

    @Column(name = "weight_change")
    private Double weightChange;

    @Column(name = "weight_trend")
    private String weightTrend; // increasing, decreasing, stable

    // Blood Pressure Analysis
    @Column(name = "avg_systolic")
    private Double avgSystolic;

    @Column(name = "avg_diastolic")
    private Double avgDiastolic;

    @Column(name = "blood_pressure_status")
    private String bloodPressureStatus; // normal, high, low

    // Blood Sugar Analysis
    @Column(name = "avg_blood_sugar")
    private Double avgBloodSugar;

    @Column(name = "blood_sugar_status")
    private String bloodSugarStatus; // normal, high, low

    // Exercise Analysis
    @Column(name = "total_exercise_sessions")
    private Integer totalExerciseSessions;

    @Column(name = "total_calories_burned")
    private Double totalCaloriesBurned;

    // Overall Health Score
    @Column(name = "health_score")
    private Double healthScore; // 0-100

    @Column(name = "health_grade")
    private String healthGrade; // A, B, C, D, F

    @Lob
    @Column(name = "recommendations")
    private String recommendations; // JSON array of health recommendations

    @Lob
    @Column(name = "achievements")
    private String achievements; // JSON array of achievements during the period

    @Column(name = "report_status")
    private String reportStatus; // generated, reviewed, archived
}