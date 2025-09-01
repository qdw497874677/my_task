package com.qdw.task.domain.task.yt.gateway;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class YoutubeDownloaderGatewayImpl implements YoutubeDownloaderGateway {

    private final WebClient webClient;

    @Value("${youtube.downloader.api.url:http://100.112.248.109:18000}")
    private String apiUrl;

    @Value("${youtube.downloader.api.key:}")
    private String apiKey;

    public YoutubeDownloaderGatewayImpl(WebClient.Builder webClientBuilder) {
        final int size = 4 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        this.webClient = webClientBuilder
                .exchangeStrategies(strategies)
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .filter(LoggingFilter.logRequest())
                .build();
    }

    @Override
    public YoutubeDownloaderResponse createTask(String url, String format) {
        log.info("createTask url: {}", url);
        
        // Build request payload
        YoutubeDownloaderRequest request = YoutubeDownloaderRequest.builder()
                .url(url)
                .format(format)
                .quality(false)
                .output_path("./downloads")
                .build();

        try {
            // Make HTTP POST request to the YouTube downloader API
            Mono<YoutubeDownloaderResponse> responseMono = webClient.post()
                    .uri(apiUrl + "/download")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(YoutubeDownloaderResponse.class);
            // Block and get response (synchronous for this implementation)
            YoutubeDownloaderResponse apiResponse = responseMono.block();
            
            if (apiResponse != null && apiResponse.getTask_id() != null) {
                log.info("createTask success taskId: {}", apiResponse.getTask_id());
                return apiResponse;
            } else {
                log.error("createTask failed");
                throw new RuntimeException("createTask failed");
            }
        } catch (Exception e) {
            log.error("createTask error url: {}", url, e);
            throw new RuntimeException("createTask error", e);
        }
    }

    @Override
    public CheckTaskResponse checkTask(String taskId) {
        log.info("checkTask taskId: {}", taskId);

        try {
            // Make HTTP POST request to the YouTube downloader API
            Mono<CheckTaskResponse> responseMono = webClient.get()
                    .uri(apiUrl + "/task/" + taskId)
                    .header("Authorization", "Bearer " + apiKey)
                    .retrieve()
                    .bodyToMono(CheckTaskResponse.class);
            // Block and get response (synchronous for this implementation)
            CheckTaskResponse checkTaskResponse = responseMono.block();

            if (checkTaskResponse != null && "success".equals(checkTaskResponse.getStatus())) {
                log.info("checkTask success checkTaskResponse:{}", JSONObject.toJSONString(checkTaskResponse));
                return checkTaskResponse;
            } else {
                log.error("checkTask failed taskId:{}", taskId);
                throw new RuntimeException("checkTask failed");
            }
        } catch (Exception e) {
            log.error("checkTask error taskId:{}", taskId, e);
            throw new RuntimeException("checkTask error", e);
        }
    }

    @Override
    public String getDownloadUrl(String taskId) {
        return apiUrl + "/download/" + taskId + "/file";
    }


}
