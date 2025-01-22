package com.example.orderservice.presentation.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    private final MeterRegistry meterRegistry;

    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getCustomMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        metrics.put("total_requests", meterRegistry.counter("api.requests.total").count());
        metrics.put("successful_requests", meterRegistry.counter("api.requests.success").count());
        metrics.put("failed_requests", meterRegistry.counter("api.requests.failed").count());

        return ResponseEntity.ok(metrics);
    }
}
