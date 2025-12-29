package org.cclemon.entity.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.cclemon.entity.base.BaseEntity;

@Entity
@Table(name = "API_LOG")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLog extends BaseEntity {

    // ===== Trace / Request =====
    @Column(name = "TRACE_ID", length = 32)
    private String traceId; // MDC / Sleuth / OpenTelemetry

    @Column(name = "REQUEST_METHOD", length = 10)
    private String requestMethod;

    @Column(name = "REQUEST_URI", length = 512)
    private String requestUri;

    @Column(name = "QUERY_STRING", length = 1024)
    private String queryString;

    // ===== Response =====
    @Column(name = "HTTP_STATUS")
    private Integer httpStatus;

    @Column(name = "DURATION_MS")
    private Long durationMs;

    // ===== Client =====
    @Column(name = "CLIENT_IP", length = 45)
    private String clientIp;

    @Column(name = "USER_AGENT")
    private String userAgent;

    // ===== Result =====
    @Column(name = "SUCCESS")
    private Boolean success;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;
}


