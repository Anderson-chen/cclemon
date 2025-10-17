package org.cclemon.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.MedicalRecord;
import org.cclemon.service.CclemonUserService;
import org.cclemon.service.MedicalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final CclemonUserService userService;

    /**
     * 獲取用戶的所有醫療記錄
     */
    @GetMapping
    public ResponseEntity<Page<MedicalRecord>> getUserMedicalRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Page<MedicalRecord> records = medicalRecordService.getUserMedicalRecords(currentUser, page, size);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("獲取用戶醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取醫療記錄
     */
    @GetMapping("/type/{recordType}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByType(@PathVariable String recordType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> records = medicalRecordService.getRecordsByType(currentUser, recordType);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("根據類型獲取醫療記錄失敗: {}", recordType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據日期範圍獲取醫療記錄
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<MedicalRecord>> getRecordsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> records = medicalRecordService.getRecordsByDateRange(currentUser, startDate, endDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("根據日期範圍獲取醫療記錄失敗: {} - {}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取活躍的醫療記錄
     */
    @GetMapping("/active")
    public ResponseEntity<List<MedicalRecord>> getActiveRecords() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> records = medicalRecordService.getActiveRecords(currentUser);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("獲取活躍醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取活躍的藥物記錄
     */
    @GetMapping("/medications/active")
    public ResponseEntity<List<MedicalRecord>> getActiveMedications() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> medications = medicalRecordService.getActiveMedications(currentUser);
            return ResponseEntity.ok(medications);
        } catch (Exception e) {
            log.error("獲取活躍藥物記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取即將到期的藥物
     */
    @GetMapping("/medications/expiring")
    public ResponseEntity<List<MedicalRecord>> getExpiringMedications(
            @RequestParam(defaultValue = "7") int daysAhead) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> medications = medicalRecordService.getExpiringMedications(currentUser, daysAhead);
            return ResponseEntity.ok(medications);
        } catch (Exception e) {
            log.error("獲取即將到期藥物失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據醫生獲取記錄
     */
    @GetMapping("/doctor/{doctorName}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByDoctor(@PathVariable String doctorName) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> records = medicalRecordService.getRecordsByDoctor(currentUser, doctorName);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("根據醫生獲取記錄失敗: {}", doctorName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據醫院獲取記錄
     */
    @GetMapping("/hospital/{hospitalName}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByHospital(@PathVariable String hospitalName) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<MedicalRecord> records = medicalRecordService.getRecordsByHospital(currentUser, hospitalName);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            log.error("根據醫院獲取記錄失敗: {}", hospitalName, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 創建新醫療記錄
     */
    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord record) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            MedicalRecord createdRecord = medicalRecordService.createMedicalRecord(currentUser, record);
            return ResponseEntity.ok(createdRecord);
        } catch (Exception e) {
            log.error("創建醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新醫療記錄
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord record) {
        try {
            MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(id, record);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException e) {
            log.error("更新醫療記錄失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刪除醫療記錄
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        try {
            medicalRecordService.deleteMedicalRecord(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("刪除醫療記錄失敗: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 停用醫療記錄
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<MedicalRecord> deactivateRecord(@PathVariable Long id) {
        try {
            MedicalRecord record = medicalRecordService.deactivateRecord(id);
            return ResponseEntity.ok(record);
        } catch (RuntimeException e) {
            log.error("停用醫療記錄失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("停用醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 重新啟用醫療記錄
     */
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<MedicalRecord> reactivateRecord(@PathVariable Long id) {
        try {
            MedicalRecord record = medicalRecordService.reactivateRecord(id);
            return ResponseEntity.ok(record);
        } catch (RuntimeException e) {
            log.error("重新啟用醫療記錄失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("重新啟用醫療記錄失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取記錄統計
     */
    @GetMapping("/stats/{recordType}")
    public ResponseEntity<Long> getRecordCountByType(@PathVariable String recordType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            long count = medicalRecordService.getRecordCountByType(currentUser, recordType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("獲取記錄統計失敗: {}", recordType, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}