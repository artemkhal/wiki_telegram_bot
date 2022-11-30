package com.khaliullov.telegrambot;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class Jwiki {
    final String BASE_URL="https://ru.wikipedia.org/api/rest_v1/page/summary/";
    String subject;

    String extractText;

    public Jwiki(String subject)
    {
        this.subject = subject;
        getData();

    }


    private void getData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + subject)
                .get()
                .build();
        try {
            Response response=client.newCall(request).execute();

            String data = response.body().string();
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(data);


            extractText = (String)jsonObject.get("extract");

        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public String getExtractText() {
        return extractText;
    }
}
