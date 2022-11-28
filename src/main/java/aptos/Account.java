package aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;

public class Account {
    private String publicKey;
    private String privateKey;
    private String accountAddress;

    public Account(String accountAddress, String publicKey, String privateKey) {
        this.accountAddress = accountAddress;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getSequenceNumber(String nodeUrl) {

        String endpoint = nodeUrl + "/accounts/" + this.accountAddress;

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            Request request = new Request.Builder()
                    .url(endpoint)
                    .method("GET", null)
                    .build();
            //  Response response = client.newCall(request).execute();

            ObjectMapper objectMapper = new ObjectMapper();
            ResponseBody responseBody = client.newCall(request).execute().body();
            assert responseBody != null;
            HashMap entity = objectMapper.readValue(responseBody.string(), HashMap.class);
            return (String) entity.get("sequence_number");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPublicKey() {
        return publicKey;
    }


    public String getAccountAddress() {
        return accountAddress;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
