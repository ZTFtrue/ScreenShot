package com.ztftrue.screen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaiduOCR {

    public static int imageType = 1;
    @Deprecated
    int imageUrl = 2;
    @Deprecated
    int imagePdf_file = 3;
    String accurateBasic = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
    String accessToken = "";
    long tokenExpireTime = 0;
    String clientId = "";
    String clientSecret = "";

    MainApplication mainApplication;

    public BaiduOCR(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }

    public String accurateBasic(int type, String filePath, String url) {
        url = Optional.ofNullable(url).orElseGet(() -> accurateBasic);
        try {
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            URI uri = URI.create(url + "?access_token=" + getToken());
            Map<String, String> parameters = new HashMap<>();
            parameters.put("paragraph", String.valueOf(true));
            parameters.put("probability", String.valueOf(false));
            parameters.put("language_type", "CHN_ENG");// _GRE
            if (type == imageType) {
                byte[] imgData = Files.readAllBytes(Path.of(filePath));
                String imgStr = Base64.getEncoder().encodeToString(imgData);
                parameters.put("image", imgStr);
            }
            String form = parameters.keySet().stream()
                    .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public JSONObject readToken() {
        JSONObject jsonObject = null;
        if (accessToken == null || "".equals(accessToken)) {
            try {
                String screenShotJson = Files.readString(Path.of(mainApplication.configPath));
                jsonObject = new JSONObject(screenShotJson);
                accurateBasic = jsonObject.optString("accurateBasic", accurateBasic);
                accessToken = jsonObject.optString("accessToken");
                tokenExpireTime = jsonObject.optLong("tokenExpireTime");
                clientId = jsonObject.optString("clientId", clientId);
                clientSecret = jsonObject.optString("clientSecret", clientSecret);
            } catch (IOException | JSONException ignored) {
                // 第一次如果没有 ScreenShot.json 就会出异常
            }
        }
        return jsonObject;
    }

    private String getToken() throws IOException, InterruptedException {
        JSONObject jsonObject = readToken();
        if (tokenExpireTime <= System.currentTimeMillis()) {
            String url = "https://aip.baidubce.com/oauth/2.0/token";
            HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "client_credentials");
            parameters.put("client_id", clientId);
            parameters.put("client_secret", clientSecret);
            String form = parameters.keySet().stream()
                    .map(key -> key + "=" + URLEncoder.encode(parameters.get(key), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            HttpRequest request = HttpRequest.newBuilder(URI.create(url + "?" + form))
                    .POST(HttpRequest.BodyPublishers.ofString(form)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObjectToken = new JSONObject(response.body());
            accessToken = jsonObjectToken.getString("access_token");
            tokenExpireTime = System.currentTimeMillis() / 1000 + 60 * 60 * 24 * 29;
            if (jsonObject == null) {
                jsonObject = new JSONObject();
                jsonObject.put("accurateBasic", accurateBasic);
                jsonObject.put("clientId", clientId);
                jsonObject.put("clientSecret", clientSecret);
            }
            jsonObject.put("accessToken", accessToken);
            jsonObject.put("tokenExpireTime", tokenExpireTime);
            Files.writeString(Path.of("./ScreenShot.json"), jsonObject.toString());
        }
        return accessToken;
    }
}
