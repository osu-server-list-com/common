package de.marcandreher.common.helpers;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.google.gson.Gson;

import de.marcandreher.fusionkit.core.FusionKit;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TurnstileCaptcha {
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
    private static final Logger logger = FusionKit.getLogger(TurnstileCaptcha.class);
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();

    public static class TurnstileResponse {
        public boolean success;
        public String[] messages;
        public String hostname;
        public String[] errorCodes;
    }

    public static @NotNull TurnstileResponse verifyCaptcha(String captchaResponse, String captchaSecret) {
        RequestBody formBody = new FormBody.Builder()
                .add("secret", captchaSecret)
                .add("response", captchaResponse)
                .build();

        Request request = new Request.Builder()
                .url(VERIFY_URL)
                .post(formBody)
                .build();

        try {
            String response = client.newCall(request).execute().body().string();
            TurnstileResponse turnstileResponse = gson.fromJson(response, TurnstileResponse.class);

            return turnstileResponse;
        } catch (Exception e) {
            logger.error("Error while verifying captcha", e);
        }

        TurnstileResponse captchaResp = new TurnstileResponse();
        captchaResp.success = false;
        return captchaResp;
    }
}

