package org.cclemon.health.internal.service;

import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.UserWeightLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeightServiceTest {

    @Mock
    private UserWeightLogRepository userWeightLogRepository;

    @Mock
    private CclemonUserRepository cclemonUserRepository;

    @InjectMocks
    private WeightService weightService;

    @Test
    void upsertManualWeight_whenNewRecord_shouldSaveNewLog() {
        // Arrange
        Long userId = 1L;
        LocalDate date = LocalDate.now();
        CclemonUser user = new CclemonUser();
        user.setId(userId);

        // Mock user repository to return a user
        when(cclemonUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock weight log repository to return empty (new record)
        when(userWeightLogRepository.findByUser_IdAndMeasureDateAndSourceAndDeletedFalse(any(), any(), any()))
                .thenReturn(Optional.empty());
        
        // Mock save to return the argument
        when(userWeightLogRepository.save(any(UserWeightLog.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        UserWeightLog result = weightService.upsertManualWeight(userId, date, new BigDecimal("70"), null, "note");

        // Assert
        assertThat(result.getUser().getId()).isEqualTo(userId);
        assertThat(result.getWeightKg()).isEqualByComparingTo("70");
        verify(userWeightLogRepository, times(1)).save(any(UserWeightLog.class));
    }

    @Test
    void softDelete_whenRecordExistsAndUserMatches_shouldMarkAsDeleted() {
        // Arrange
        Long userId = 1L;
        Long logId = 10L;
        
        CclemonUser user = new CclemonUser();
        user.setId(userId);

        UserWeightLog log = new UserWeightLog();
        log.setId(logId);
        log.setUser(user);
        log.setDeleted(false);

        // Update method name to match new repository method
        when(userWeightLogRepository.findByIdAndUser_Id(logId, userId)).thenReturn(Optional.of(log));

        // Act
        weightService.softDelete(userId, logId);

        // Assert
        verify(userWeightLogRepository, times(1)).save(any(UserWeightLog.class));
        // Assuming BaseEntity has isDeleted() or getDeleted() - checking implementation
        // Based on previous code it was isDeleted() or getDeleted() depending on Lombok
        // Let's assume standard getter for boolean field 'deleted'
        assertThat(log.getDeleted()).isTrue(); 
    }

    @Test
    void softDelete_whenRecordNotFound_shouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long logId = 10L;
        
        // Update method name to match new repository method
        when(userWeightLogRepository.findByIdAndUser_Id(logId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> weightService.softDelete(userId, logId))
                .isInstanceOf(NoSuchElementException.class);
        verify(userWeightLogRepository, never()).save(any());
    }
}
