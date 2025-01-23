package com.example.orderservice.presentation.controller.impl;

import com.example.orderservice.presentation.controller.MetricsControllerApi;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MetricsController implements MetricsControllerApi {

    private final MeterRegistry meterRegistry;

    @Override
    public ResponseEntity<Map<String, Object>> getCustomMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("total_requests", meterRegistry.counter("api.requests.total").count());
        metrics.put("successful_requests", meterRegistry.counter("api.requests.success").count());
        metrics.put("failed_requests", meterRegistry.counter("api.requests.failed").count());

        return ResponseEntity.ok(metrics);
    }
}
