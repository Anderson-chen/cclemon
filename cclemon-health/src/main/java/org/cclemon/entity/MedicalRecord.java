package org.cclemon.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medical_record")
@Data
public class MedicalRecord extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "medical_record_user_id_foreign"))
    private CclemonUser user;

    @Column(nullable = false)
    private String date; // yyyy-MM-dd format

    @Column(name = "record_type", nullable = false)
    private String recordType; // checkup, diagnosis, medication, surgery, allergy, vaccination

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "medication_name")
    private String medicationName;

    @Column(name = "medication_dosage")
    private String medicationDosage;

    @Column(name = "medication_frequency")
    private String medicationFrequency;

    @Column(name = "medication_duration")
    private String medicationDuration;

    @Column(name = "severity_level")
    private String severityLevel; // low, medium, high, critical

    @Column(name = "attachment_paths")
    private String attachmentPaths; // JSON array of file paths

    private String notes;

    @Column(name = "is_active")
    private Boolean isActive = true;
}