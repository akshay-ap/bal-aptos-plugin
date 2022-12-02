package aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;

import static aptos.Utils.sendGetRequest;

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
            String result = sendGetRequest(endpoint);

            ObjectMapper objectMapper = new ObjectMapper();
            HashMap entity = objectMapper.readValue(result, HashMap.class);
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
