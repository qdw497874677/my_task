package com.qdw.task.domain.task.yt.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Slf4j
public class LoggingFilter {


    public static ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            // 记录请求方法和完整的 URL
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());

            // 记录请求头
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> log.info("Header: {}={}", name, value))
            );

            // 注意：记录请求体可能会消耗 body，需要小心处理。
            // 对于简单的 bodyValue，可以像下面这样记录。
            // 如果是复杂的流式 body，处理会更复杂。
            if (clientRequest.body() != null) {
                log.info("Request Body: {}", clientRequest.body());
            }

            return next.exchange(clientRequest);
        };
    }
}