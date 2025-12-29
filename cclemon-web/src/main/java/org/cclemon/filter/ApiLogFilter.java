package org.cclemon.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.producer.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ApiLogFilter extends OncePerRequestFilter {

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ApiLogFilter(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        ApiLog.ApiLogBuilder builder = ApiLog.builder()
                // ===== Trace / Request =====
                .traceId(getTraceId())
                .requestMethod(request.getMethod())
                .requestUri(request.getRequestURI())
                .queryString(request.getQueryString())
                // ===== Client =====
                .clientIp(resolveClientIp(request)).userAgent(request.getHeader("User-Agent"));
        try {

            stopWatch.start();

            filterChain.doFilter(request, response);

            // ===== Result =====
            builder.httpStatus(response.getStatus())
                    .success(response.getStatus() < 400);

        } catch (Exception ex) {
            builder.httpStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .success(false)
                    .errorCode(ex.getClass().getSimpleName());
            throw ex;
        } finally {
            try {
                stopWatch.stop();
                ApiLog apiLog = builder.durationMs(stopWatch.getTotalTimeMillis()).build();
                kafkaProducerService.sendMessage(apiLog, "CCLEMON_HEALTH_API_LOG");
                log.info("API Log saved: {}", apiLog);
            } catch (Exception e) {
                log.error("Failed to save API log", e);
            }
        }
    }

    /**
     * TraceId：可與 MDC / Sleuth / OTel 整合
     */
    private String getTraceId() {
        return org.slf4j.MDC.get("traceId");
    }

    /**
     * Client IP：考慮反向代理
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Builder
    @Data
    public static class ApiLog {
        // ===== Trace / Request =====
        private String traceId; // MDC / Sleuth / OpenTelemetry

        private String requestMethod;

        private String requestUri;

        private String queryString;

        // ===== Response =====
        private Integer httpStatus;

        private Long durationMs;

        // ===== Client =====
        private String clientIp;

        private String userAgent;

        // ===== Result =====
        private Boolean success;

        private String errorCode;
    }

}

