package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.MedicalRecord;
import org.cclemon.repository.MedicalRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * 獲取用戶的所有醫療記錄
     */
    public Page<MedicalRecord> getUserMedicalRecords(CclemonUser user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return medicalRecordRepository.findByUserAndDeletedFalseOrderByDateDesc(user, pageable);
    }

    /**
     * 根據類型獲取醫療記錄
     */
    public List<MedicalRecord> getRecordsByType(CclemonUser user, String recordType) {
        return medicalRecordRepository.findByUserAndRecordTypeAndDeletedFalse(user, recordType);
    }

    /**
     * 根據日期範圍獲取醫療記錄
     */
    public List<MedicalRecord> getRecordsByDateRange(CclemonUser user, String startDate, String endDate) {
        return medicalRecordRepository.findByUserAndDateRangeAndDeletedFalse(user, startDate, endDate);
    }

    /**
     * 獲取活躍的醫療記錄
     */
    public List<MedicalRecord> getActiveRecords(CclemonUser user) {
        return medicalRecordRepository.findByUserAndIsActiveAndDeletedFalse(user, true);
    }

    /**
     * 獲取活躍的藥物記錄
     */
    public List<MedicalRecord> getActiveMedications(CclemonUser user) {
        return medicalRecordRepository.findActiveMedicationsByUser(user);
    }

    /**
     * 創建新醫療記錄
     */
    @Transactional
    public MedicalRecord createMedicalRecord(CclemonUser user, MedicalRecord record) {
        record.setUser(user);
        record.setDeleted(false);
        
        if (record.getDate() == null) {
            record.setDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        
        // 根據記錄類型設置默認嚴重程度
        if (record.getSeverityLevel() == null) {
            switch (record.getRecordType()) {
                case "allergy":
                case "surgery":
                    record.setSeverityLevel("high");
                    break;
                case "medication":
                case "vaccination":
                    record.setSeverityLevel("medium");
                    break;
                default:
                    record.setSeverityLevel("low");
                    break;
            }
        }

        log.info("創建醫療記錄: 用戶={}, 類型={}, 標題={}", user.getId(), record.getRecordType(), record.getTitle());
        return medicalRecordRepository.save(record);
    }

    /**
     * 更新醫療記錄
     */
    @Transactional
    public MedicalRecord updateMedicalRecord(Long recordId, MedicalRecord updatedRecord) {
        Optional<MedicalRecord> existingRecord = medicalRecordRepository.findById(recordId);
        if (existingRecord.isEmpty() || existingRecord.get().getDeleted()) {
            throw new RuntimeException("醫療記錄不存在: " + recordId);
        }

        MedicalRecord record = existingRecord.get();
        updateRecordFields(record, updatedRecord);

        log.info("更新醫療記錄: ID={}, 標題={}", recordId, record.getTitle());
        return medicalRecordRepository.save(record);
    }

    /**
     * 刪除醫療記錄（軟刪除）
     */
    @Transactional
    public void deleteMedicalRecord(Long recordId) {
        Optional<MedicalRecord> record = medicalRecordRepository.findById(recordId);
        if (record.isPresent() && !record.get().getDeleted()) {
            record.get().setDeleted(true);
            record.get().setIsActive(false);
            medicalRecordRepository.save(record.get());
            log.info("刪除醫療記錄: ID={}", recordId);
        }
    }

    /**
     * 停用醫療記錄
     */
    @Transactional
    public MedicalRecord deactivateRecord(Long recordId) {
        Optional<MedicalRecord> record = medicalRecordRepository.findById(recordId);
        if (record.isEmpty() || record.get().getDeleted()) {
            throw new RuntimeException("醫療記錄不存在: " + recordId);
        }

        record.get().setIsActive(false);
        log.info("停用醫療記錄: ID={}", recordId);
        return medicalRecordRepository.save(record.get());
    }

    /**
     * 重新啟用醫療記錄
     */
    @Transactional
    public MedicalRecord reactivateRecord(Long recordId) {
        Optional<MedicalRecord> record = medicalRecordRepository.findById(recordId);
        if (record.isEmpty() || record.get().getDeleted()) {
            throw new RuntimeException("醫療記錄不存在: " + recordId);
        }

        record.get().setIsActive(true);
        log.info("重新啟用醫療記錄: ID={}", recordId);
        return medicalRecordRepository.save(record.get());
    }

    /**
     * 根據類型獲取記錄統計
     */
    public long getRecordCountByType(CclemonUser user, String recordType) {
        return medicalRecordRepository.countByUserAndRecordTypeAndDeletedFalse(user, recordType);
    }

    /**
     * 獲取即將到期的藥物
     */
    public List<MedicalRecord> getExpiringMedications(CclemonUser user, int daysAhead) {
        // 這裡可以實現根據 medicationDuration 計算即將到期的藥物
        // 目前簡化為返回所有活躍藥物
        return getActiveMedications(user);
    }

    /**
     * 根據醫生獲取記錄
     */
    public List<MedicalRecord> getRecordsByDoctor(CclemonUser user, String doctorName) {
        return medicalRecordRepository.findByUserAndDeletedFalse(user).stream()
                .filter(record -> doctorName.equals(record.getDoctorName()))
                .toList();
    }

    /**
     * 根據醫院獲取記錄
     */
    public List<MedicalRecord> getRecordsByHospital(CclemonUser user, String hospitalName) {
        return medicalRecordRepository.findByUserAndDeletedFalse(user).stream()
                .filter(record -> hospitalName.equals(record.getHospitalName()))
                .toList();
    }

    /**
     * 更新記錄字段
     */
    private void updateRecordFields(MedicalRecord existing, MedicalRecord updated) {
        if (updated.getDate() != null) existing.setDate(updated.getDate());
        if (updated.getRecordType() != null) existing.setRecordType(updated.getRecordType());
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getDoctorName() != null) existing.setDoctorName(updated.getDoctorName());
        if (updated.getHospitalName() != null) existing.setHospitalName(updated.getHospitalName());
        if (updated.getMedicationName() != null) existing.setMedicationName(updated.getMedicationName());
        if (updated.getMedicationDosage() != null) existing.setMedicationDosage(updated.getMedicationDosage());
        if (updated.getMedicationFrequency() != null) existing.setMedicationFrequency(updated.getMedicationFrequency());
        if (updated.getMedicationDuration() != null) existing.setMedicationDuration(updated.getMedicationDuration());
        if (updated.getSeverityLevel() != null) existing.setSeverityLevel(updated.getSeverityLevel());
        if (updated.getAttachmentPaths() != null) existing.setAttachmentPaths(updated.getAttachmentPaths());
        if (updated.getNotes() != null) existing.setNotes(updated.getNotes());
        if (updated.getIsActive() != null) existing.setIsActive(updated.getIsActive());
    }
}