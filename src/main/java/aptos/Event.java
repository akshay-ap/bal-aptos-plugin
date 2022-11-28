package aptos;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Event {

    public Response queryEvent(String address, String eventHandle, String fieldName) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("https://fullnode.devnet.aptoslabs.com/v1/accounts/%s/events/%s/%s",
                address, eventHandle, fieldName);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        return client.newCall(request).execute();
    }
}
