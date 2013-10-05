package weblog.examples.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class RandomNumberSerializer implements IRandomNumberSerializer {
    private GsonBuilder gsonBuilder;

    public RandomNumberSerializer() {
        gsonBuilder = new GsonBuilder();
    }

    @Override
    public String serializeRandomNumber(RandNumber number) {
        Gson gson = gsonBuilder.create();
        return gson.toJson(number);
    }

    @Override
    public RandNumber deserializeRandomNumber(String input) {
        Gson gson = gsonBuilder.create();
        return gson.fromJson(input, RandNumber.class);
    }

    @Override
    public void serializeRandomNumber(RandNumber number, OutputStream stream) {
        Gson gson = gsonBuilder.create();
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        gson.toJson(number, writer);
        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RandNumber deserializeRandomNumber(InputStream stream) {
        Gson gson = gsonBuilder.create();
        return gson.fromJson(new InputStreamReader(stream), RandNumber.class);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }
}
