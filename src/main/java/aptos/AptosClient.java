package aptos;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
// import java.util.HexFormat;

import java.io.IOException;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class AptosClient {
    private final String nodeUrl;
    private String faucetUrl;
    private Account account;

    public AptosClient(String nodeUrl, String faucetUrl) {
        this.nodeUrl = nodeUrl;
        this.faucetUrl = faucetUrl;
    }


    public void setAccount(Account account) {
        this.account = account;
    }

    public String sendTransaction(String address, String moduleName, String functionName, String[] typeArguments, String[] functionArguments) {

        String endpoint = this.nodeUrl + "/transactions";
        String sequenceNumber = this.account.getSequenceNumber(this.nodeUrl);
        String maxGasAmount = "10000";
        String gasPrice = "100";
        String expirationTimeInSeconds = "32425224034";

        Transaction t = new Transaction.TransactionBuilder(account.getAccountAddress(), address, moduleName, functionName, typeArguments, functionArguments, endpoint, sequenceNumber)
                .setGasPrice(gasPrice)
                .setMaxGasAmount(maxGasAmount)
                .setExpirationTimeInSeconds(expirationTimeInSeconds)
                .build();

        String bodyContent = t.getUnsignedTransactionAsJsonString();
        String encodedData = this.encodeData(bodyContent);

        String signature = this.signMessage(encodedData, account.getPrivateKey());

        Transaction.Signature s = new Transaction.Signature(Transaction.Signature.SignatureType.ED25519_SIGNATURE, account.getPublicKey(), signature);
        t.setSignature(s);

        String signedBodyContent = t.getSignedTransactionAsJsonString();
        try {
            Response response = post(endpoint, signedBodyContent);
            assert response.isSuccessful();
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseBody responseBody = response.body();
            assert responseBody != null;
            Map<String, Object> entity = objectMapper.readValue(responseBody.string(), HashMap.class);
            return (String) entity.get("hash");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Response post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json");

        RequestBody body = RequestBody.create(json, null);
        Request request = new Request.Builder()
                .addHeader("Content-type", "application/json")
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public String signMessage(String data, String privateKey) {
        privateKey = privateKey.substring(2);
        Security.addProvider(new BouncyCastleProvider());

        byte[] privateKeyEncoded = Hex.decode(privateKey);

        data = data.substring(2);
        byte[] message = hexToByteArray(data);
        //byte[] message = HexFormat.of().parseHex(data);

        Ed25519PrivateKeyParameters privateKeyParameters = new Ed25519PrivateKeyParameters(privateKeyEncoded, 0);

        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, privateKeyParameters);
        signer.update(message, 0, message.length);
        byte[] signature = signer.generateSignature();
        return "0x".concat(Hex.toHexString(signature));
    }

    public String encodeData(String data) {
        try {
            String endpoint = this.nodeUrl + "/transactions/encode_submission";

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(endpoint)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            assert response.code() == 200 && response.body() != null;
            String encodedData = response.body().string();
            return encodedData.replace("\"", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] hexToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
