package com.example.orderservice.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Service for collecting API metrics.
 *
 * <p>This service uses Micrometer to track API request metrics, including:
 * <ul>
 *     <li>Total requests</li>
 *     <li>Successful requests</li>
 *     <li>Failed requests</li>
 * </ul>
 * Metrics are stored as counters and can be queried for monitoring and analysis.</p>
 */
@Component
public class ApiMetricsService {

    private final Counter totalRequests;
    private final Counter successfulRequests;
    private final Counter failedRequests;

    /**
     * Initializes the metrics counters using the provided {@link MeterRegistry}.
     *
     * @param meterRegistry the registry used to manage the metrics
     */
    public ApiMetricsService(MeterRegistry meterRegistry) {
        this.totalRequests = meterRegistry.counter("api.requests.total");
        this.successfulRequests = meterRegistry.counter("api.requests.success");
        this.failedRequests = meterRegistry.counter("api.requests.failed");
    }

    /**
     * Increments the counter for total API requests.
     */
    public void incrementTotalRequests() {
        totalRequests.increment();
    }

    /**
     * Increments the counter for successful API requests.
     */
    public void incrementSuccessfulRequests() {
        successfulRequests.increment();
    }

    /**
     * Increments the counter for failed API requests.
     */
    public void incrementFailedRequests() {
        failedRequests.increment();
    }
}
