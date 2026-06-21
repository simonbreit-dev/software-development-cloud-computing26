package de.quadflal.sdfccbackend.common.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class ApplicationTelemetryFilter extends OncePerRequestFilter {

    private static final AttributeKey<String> HTTP_METHOD = AttributeKey.stringKey("http.request.method");
    private static final AttributeKey<String> HTTP_ROUTE = AttributeKey.stringKey("http.route");
    private static final AttributeKey<Long> HTTP_STATUS_CODE = AttributeKey.longKey("http.response.status_code");
    private static final AttributeKey<String> ERROR_TYPE = AttributeKey.stringKey("error.type");

    private final LongCounter requestCounter;
    private final LongCounter errorCounter;
    private final DoubleHistogram requestDuration;

    public ApplicationTelemetryFilter(@Qualifier("openTelemetry") OpenTelemetry openTelemetry) {
        var meter = openTelemetry.getMeter("de.quadflal.sdfccbackend");
        this.requestCounter = meter.counterBuilder("sdfcc.http.server.requests")
                .setDescription("Number of HTTP requests handled by the SDFCC backend")
                .setUnit("{request}")
                .build();
        this.errorCounter = meter.counterBuilder("sdfcc.http.server.errors")
                .setDescription("Number of failed HTTP requests handled by the SDFCC backend")
                .setUnit("{error}")
                .build();
        this.requestDuration = meter.histogramBuilder("sdfcc.http.server.duration")
                .setDescription("Duration of HTTP requests handled by the SDFCC backend")
                .setUnit("ms")
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startedAt = System.nanoTime();
        Throwable failure = null;

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException ex) {
            failure = ex;
            throw ex;
        } finally {
            Attributes attributes = attributesFor(request, response, failure);
            requestCounter.add(1, attributes);

            int status = response.getStatus();
            if (failure != null || status >= 400) {
                errorCounter.add(1, attributes);
            }

            double durationMs = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startedAt) / 1_000.0;
            requestDuration.record(durationMs, attributes);
        }
    }

    private Attributes attributesFor(HttpServletRequest request, HttpServletResponse response, Throwable failure) {
        AttributesBuilder attributes = Attributes.builder()
                .put(HTTP_METHOD, request.getMethod())
                .put(HTTP_ROUTE, routeFor(request))
                .put(HTTP_STATUS_CODE, response.getStatus());

        if (failure != null) {
            attributes.put(ERROR_TYPE, failure.getClass().getSimpleName());
        } else if (response.getStatus() >= 500) {
            attributes.put(ERROR_TYPE, "5xx");
        } else if (response.getStatus() >= 400) {
            attributes.put(ERROR_TYPE, "4xx");
        }

        return attributes.build();
    }

    private String routeFor(HttpServletRequest request) {
        Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (route instanceof String pattern && !pattern.isBlank()) {
            return pattern;
        }
        return "UNMATCHED";
    }
}
