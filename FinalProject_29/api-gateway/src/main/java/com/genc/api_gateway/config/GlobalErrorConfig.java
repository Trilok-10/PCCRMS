package com.genc.api_gateway.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class GlobalErrorConfig {

    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler globalErrorHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = "Internal Server Error";

            if (ex instanceof ResponseStatusException) {
                status = HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
                message = ((ResponseStatusException) ex).getReason();
            }

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String body = String.format(
                    "{\"error\": \"%s\", \"message\": \"%s\", \"status\": %d}",
                    status.getReasonPhrase(),
                    message != null ? message : ex.getMessage(),
                    status.value()
            );

            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(body.getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse().writeWith(Mono.just(buffer));
        };
    }
}

