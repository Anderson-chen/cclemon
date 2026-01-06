package org.cclemon.controller;

import org.cclemon.api.WeightHandler;
import org.cclemon.api.vo.command.RecordWeightCommand;
import org.cclemon.producer.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeightController.class)
class WeightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private WeightHandler weightHandler;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void upsertWeight_withValidRequest_shouldReturnOkAndSendApiLog() throws Exception {
        WeightUpsertRequest request = WeightUpsertRequest.builder()
                .weightKg(new BigDecimal("75.5"))
                .build();

        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 驗證業務邏輯被呼叫
        verify(weightHandler).record(any(RecordWeightCommand.class));
        // 驗證日誌訊息被發送
        verify(kafkaProducerService, times(1)).sendMessage(any(), anyString());
    }

    @Test
    void upsertWeight_withFutureDate_shouldReturnBadRequest() throws Exception {
        WeightUpsertRequest request = WeightUpsertRequest.builder()
                .measureDate(LocalDate.now().plusDays(1))
                .weightKg(new BigDecimal("75.5"))
                .build();

        mockMvc.perform(post("/api/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        
        // 即使請求失敗，日誌也應該被記錄
        verify(kafkaProducerService, times(1)).sendMessage(any(), anyString());
    }

    @Test
    void listWeights_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/weights"))
                .andExpect(status().isOk());
        
        verify(kafkaProducerService, times(1)).sendMessage(any(), anyString());
    }

    @Test
    void getChart_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/weights/chart"))
                .andExpect(status().isOk());
        
        verify(kafkaProducerService, times(1)).sendMessage(any(), anyString());
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/weights/1"))
                .andExpect(status().isNoContent());
        
        verify(kafkaProducerService, times(1)).sendMessage(any(), anyString());
    }
}
