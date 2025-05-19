package anchovy.team.epialarm.zeus.client;

import anchovy.team.epialarm.zeus.models.AppAuthModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZeusApiClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient;
    private final String baseUrl;
    private String authToken;
    private final ObjectMapper objectMapper;

    public ZeusApiClient() {
        this.baseUrl = "https://zeus.ionis-it.com";
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CompletableFuture<String> authenticate(String accessToken) {
        AppAuthModel authModel = new AppAuthModel(accessToken);
        return postForString("/api/User/OfficeLogin", authModel)
                .thenApply(token -> {
                    this.authToken = token;
                    return token;
                });
    }

    public <T> CompletableFuture<T> get(String path, TypeReference<T> responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .get()
                .build();

        CompletableFuture<T> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response res = response) {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new IOException("Unexpected response code: "
                                + response.code()));
                        return;
                    }

                    String body = res.body() != null ? res.body().string() : "";
                    try {
                        T result = objectMapper.readValue(body, responseType);
                        future.complete(result);
                    } catch (JsonProcessingException e) {
                        future.completeExceptionally(
                                new RuntimeException("Failed to parse response", e));
                    }
                }
            }
        });

        return future;
    }

    public <T, R> CompletableFuture<R> post(String path, T body, TypeReference<R> responseType) {
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            CompletableFuture<R> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to serialize request body",
                    e));
            return future;
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .post(requestBody)
                .build();

        CompletableFuture<R> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response res = response) {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new IOException("Unexpected response code: "
                                + response.code()));
                        return;
                    }

                    String responseBody = res.body() != null ? res.body().string() : "";
                    try {
                        R result = objectMapper.readValue(responseBody, responseType);
                        future.complete(result);
                    } catch (JsonProcessingException e) {
                        future.completeExceptionally(
                                new RuntimeException("Failed to parse response", e));
                    }
                }
            }
        });

        return future;
    }

    public <T> CompletableFuture<String> postForString(String path, T body) {
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to serialize request body",
                    e));
            return future;
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .post(requestBody)
                .build();

        CompletableFuture<String> future = new CompletableFuture<>();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response res = response) {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new IOException("Unexpected response code: "
                                + response.code()));
                        return;
                    }

                    String responseBody = res.body() != null ? res.body().string() : "";
                    
                    if (responseBody.startsWith("\"") && responseBody.endsWith("\"")) {
                        responseBody = responseBody.substring(1, responseBody.length() - 1);
                    }
                    
                    future.complete(responseBody);
                }
            }
        });

        return future;
    }
}