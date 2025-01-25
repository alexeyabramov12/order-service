package com.example.orderservice.infrastructure.metrics;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A filter for collecting API request metrics.
 *
 * <p>This filter intercepts HTTP requests and responses to track metrics such as total requests,
 * successful requests, and failed requests. The metrics are updated in {@link ApiMetricsService}.</p>
 */
@Component
@RequiredArgsConstructor
public class MetricsFilter implements Filter {

    private final ApiMetricsService apiMetricsService;

    /**
     * Intercepts HTTP requests and responses to collect metrics.
     *
     * <p>The method increments the total requests counter for each request. Depending on the response
     * status, it also increments either the successful or failed requests counters.</p>
     *
     * @param request  the {@link ServletRequest} object that contains the client request
     * @param response the {@link ServletResponse} object that contains the response
     * @param chain    the {@link FilterChain} to pass the request and response to the next filter
     * @throws IOException      if an input or output error occurs while processing the request
     * @throws ServletException if the request cannot be handled
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest &&
                response instanceof HttpServletResponse httpResponse) {

            // Игнорируем метрики для эндпоинта /metrics
            String requestURI = httpRequest.getRequestURI();
            if ("/metrics".equals(requestURI)) {
                chain.doFilter(request, response);
                return;
            }

            apiMetricsService.incrementTotalRequests();

            try {
                chain.doFilter(request, response);

                if (httpResponse.getStatus() >= 200 && httpResponse.getStatus() < 400) {
                    apiMetricsService.incrementSuccessfulRequests();
                } else {
                    apiMetricsService.incrementFailedRequests();
                }
            } catch (Exception e) {
                apiMetricsService.incrementFailedRequests();
                throw e;
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
