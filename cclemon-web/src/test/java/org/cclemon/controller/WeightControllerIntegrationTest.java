package org.cclemon.controller;

import jakarta.persistence.EntityManager;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.cclemon.producer.KafkaProducerService;
import org.cclemon.repository.UserWeightLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WeightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserWeightLogRepository userWeightLogRepository;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    private CclemonUser testUser;

    @BeforeEach
    void setUp() {
        // Reset identity so the test user always gets ID = 1 (matches getCurrentUserId())
        entityManager.createNativeQuery("ALTER TABLE cclemon_user ALTER COLUMN id RESTART WITH 1")
                .executeUpdate();

        testUser = new CclemonUser();
        testUser.setUsername("test-user");
        entityManager.persist(testUser);
        entityManager.flush();
        assertThat(testUser.getId()).isEqualTo(1L);
    }

    @Test
    void shouldRecordWeight() throws Exception {
        String body = jsonMapper.writeValueAsString(
                WeightUpsertRequest.builder()
                        .measureDate(LocalDate.now())
                        .weightKg(new BigDecimal("72.50"))
                        .note("morning")
                        .build());

        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.measureDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.weightKg").value(72.50))
                .andExpect(jsonPath("$.note").value("morning"))
                .andExpect(jsonPath("$.logId").isNumber());

        List<UserWeightLog> logs = userWeightLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getWeightKg()).isEqualByComparingTo("72.50");
    }

    @Test
    void shouldUpsertExistingWeight() throws Exception {
        LocalDate today = LocalDate.now();

        // first record
        String first = jsonMapper.writeValueAsString(
                WeightUpsertRequest.builder()
                        .measureDate(today)
                        .weightKg(new BigDecimal("72.00"))
                        .build());
        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(first))
                .andExpect(status().isOk());

        // second record on the same date — should update, not insert
        String second = jsonMapper.writeValueAsString(
                WeightUpsertRequest.builder()
                        .measureDate(today)
                        .weightKg(new BigDecimal("73.00"))
                        .build());
        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(second))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightKg").value(73.00));

        List<UserWeightLog> logs = userWeightLogRepository
                .findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(
                        testUser.getId(), today, today);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getWeightKg()).isEqualByComparingTo("73.00");
    }

    @Test
    void shouldReturnBadRequest_whenFutureDate() throws Exception {
        String body = jsonMapper.writeValueAsString(
                WeightUpsertRequest.builder()
                        .measureDate(LocalDate.now().plusDays(1))
                        .weightKg(new BigDecimal("72.00"))
                        .build());

        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        assertThat(userWeightLogRepository.findAll()).isEmpty();
    }

    @Test
    void shouldListWeights() throws Exception {
        LocalDate base = LocalDate.now().minusDays(5);
        for (int i = 0; i < 3; i++) {
            persistWeightLog(base.plusDays(i), new BigDecimal("70.00").add(BigDecimal.valueOf(i)));
        }
        entityManager.flush();

        mockMvc.perform(get("/api/v1/weights")
                        .param("startDate", base.toString())
                        .param("endDate", base.plusDays(5).toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void shouldGetChartData() throws Exception {
        LocalDate base = LocalDate.now().minusDays(5);
        persistWeightLog(base, new BigDecimal("70.00"));
        persistWeightLog(base.plusDays(1), new BigDecimal("71.00"));
        persistWeightLog(base.plusDays(2), new BigDecimal("72.00"));
        entityManager.flush();

        mockMvc.perform(get("/api/v1/weights/chart")
                        .param("range", "custom")
                        .param("startDate", base.toString())
                        .param("endDate", base.plusDays(5).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startWeight").value(70.00))
                .andExpect(jsonPath("$.endWeight").value(72.00))
                .andExpect(jsonPath("$.change").value(2.00))
                .andExpect(jsonPath("$.dataPoints", hasSize(3)));
    }

    @Test
    void shouldSoftDeleteWeight() throws Exception {
        // create a record via API
        String body = jsonMapper.writeValueAsString(
                WeightUpsertRequest.builder()
                        .measureDate(LocalDate.now())
                        .weightKg(new BigDecimal("72.50"))
                        .build());

        String response = mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long logId = JsonMapper.builder().build()
                .readTree(response).get("logId").longValue();

        // soft-delete
        mockMvc.perform(delete("/api/v1/weights/{id}", logId))
                .andExpect(status().isNoContent());

        // verify soft-deleted in DB
        UserWeightLog deleted = userWeightLogRepository.findById(logId).orElseThrow();
        assertThat(deleted.getDeleted()).isTrue();
    }

    private void persistWeightLog(LocalDate date, BigDecimal weight) {
        UserWeightLog log = new UserWeightLog();
        log.setUser(testUser);
        log.setMeasureDate(date);
        log.setWeightKg(weight);
        log.setSource(WeightSource.MANUAL);
        log.setDeleted(false);
        entityManager.persist(log);
    }
}
