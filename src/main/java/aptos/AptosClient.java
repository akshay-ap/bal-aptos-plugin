package aptos;

import aptos.models.LedgerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aptos.Utils.sendGetRequest;

public class AptosClient {
    private final String nodeUrl;
    private String faucetUrl;
    private Account account;

    private HashMap<String, String> eventSubscriptionMapping = new HashMap();
    private static final Logger logger = LoggerFactory.getLogger(AptosClient.class.getName());

    public AptosClient(String nodeUrl, String faucetUrl) {
        this.nodeUrl = nodeUrl;
        this.faucetUrl = faucetUrl;
    }

    public String getSequenceNumber() {
        return account.getSequenceNumber(this.nodeUrl);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String sendTransaction(String address, String moduleName, String functionName, String[] typeArguments, Object[] functionArguments, String sequenceNumber) throws Exception {

        String endpoint = this.nodeUrl + "/transactions";
        String maxGasAmount = "10000";
        String gasPrice = "100";
        String expirationTimeInSeconds = "32425224034";

        Transaction t = new Transaction.TransactionBuilder(account.getAccountAddress(), address, moduleName, functionName, typeArguments, functionArguments, endpoint, sequenceNumber)
                .setGasPrice(gasPrice)
                .setMaxGasAmount(maxGasAmount)
                .setExpirationTimeInSeconds(expirationTimeInSeconds)
                .build();

        String bodyContent = t.getUnsignedTransactionAsJsonString();

        logger.info("Aptos unsigned transaction body: [{}]", bodyContent);

        String encodedData = this.encodeData(bodyContent);

        String signature = this.signMessage(encodedData, account.getPrivateKey());

        Transaction.Signature s = new Transaction.Signature(Transaction.Signature.SignatureType.ED25519_SIGNATURE, account.getPublicKey(), signature);
        t.setSignature(s);

        String signedBodyContent = t.getSignedTransactionAsJsonString();
        try {
            String response = post(endpoint, signedBodyContent);
            // assert response.isSuccessful();
            ObjectMapper objectMapper = new ObjectMapper();
            // ResponseBody responseBody = response.body();
            // assert responseBody != null;
            Map<String, Object> entity = objectMapper.readValue(response, HashMap.class);
            return (String) entity.get("hash");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private String post(String url, String json) throws Exception {
//        OkHttpClient client = new OkHttpClient();
//        okhttp3.MediaType JSON = okhttp3.MediaType.get("application/json");
//
//        RequestBody body = RequestBody.create(json, null);
//        Request request = new Request.Builder()
//                .addHeader("Content-type", "application/json")
//                .url(url)
//                .post(body)
//                .build();
//        return client.newCall(request).execute();
        String result = "";
        HttpPost post = new HttpPost(url);
        post.addHeader("content-type", "application/json");

        // send a JSON data
        post.setEntity(new StringEntity(json.toString()));

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            logger.debug("POST API [{}] response code [{}]", url, response.getStatusLine().getStatusCode());
            result = EntityUtils.toString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 202) {
                throw new Exception(result);
            }
            return result;

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

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

    public String encodeData(String data) throws Exception {
        try {
            String endpoint = this.nodeUrl + "/transactions/encode_submission";

            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient().newBuilder()
                    .build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(endpoint)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                throw new Exception(response.body().string());
            }
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

    public void queryFunctionInvocations(String accountAddress, String functionIdentifier, long start, long end) {

    }

    public List<HashMap> queryEventInvocationsByAccount(String accountAddress, String eventIdentifier, String start, String end) {
        BigInteger endTime = new BigInteger(end);
        BigInteger startTime = new BigInteger(start);

        ArrayList<HashMap> results = new ArrayList<>();
        String url = this.nodeUrl + "/accounts/" + accountAddress + "/transactions";
        int limit = 100;
        int start_count = 0;
        boolean breakLoop = false;
        try {
            while (true) {
                String endpoint = url + "?limit=" + limit + "&start=" + start_count;
                String result = sendGetRequest(endpoint);
                ObjectMapper objectMapper = new ObjectMapper();

                HashMap[] response_entity = objectMapper.readValue(result, HashMap[].class);

                for (HashMap r : response_entity) {
                    BigInteger timeStamp = new BigInteger(r.get("timestamp").toString());

                    if (startTime.compareTo(timeStamp) > 0) {
                        continue;
                    }

                    if (endTime.compareTo(timeStamp) < 0) {
                        breakLoop = true;
                        break;
                    }
                    results.add(r);
                }
                start_count = start_count + limit;
                if (breakLoop || response_entity.length < limit) {
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return results;

    }

    public List<HashMap> queryUserEventInvocations(String accountAddress, String module, String eventIdentifier, String start, String end) {
        BigInteger endTime = new BigInteger(end);
        BigInteger startTime = new BigInteger(start);

        ArrayList<HashMap> results = new ArrayList<>();
        String url = this.nodeUrl + "/transactions";
        int limit = 100;

        // TODO: Create local cache of last timestamp and block number mapping
        int start_count = 0;
        boolean breakLoop = false;

        String type = accountAddress + "::" + module + "::" + eventIdentifier;

        try {
            while (true) {
                String endpoint = url + "?limit=" + limit + "&start=" + start_count;
                logger.debug("Querying transactions from [{}] to [{}]", start, start + limit);
                String result = sendGetRequest(endpoint);
                ObjectMapper objectMapper = new ObjectMapper();

                HashMap[] response_entity = objectMapper.readValue(result, HashMap[].class);

                for (HashMap r : response_entity) {

                    if (r.get("timestamp") != null) {
                        BigInteger timeStamp = new BigInteger(r.get("timestamp").toString());
                        if (startTime.compareTo(timeStamp) > 0) {
                            continue;
                        }

                        if (endTime.compareTo(timeStamp) < 0) {
                            breakLoop = true;
                            break;
                        }
                    }

                    if (!r.get("type").equals("user_transaction")) continue;

                    ArrayList e = ((ArrayList) r.get("events"));
                    if (e.size() == 0) continue;
                    HashMap<String, Object> events = (HashMap<String, Object>) ((ArrayList) r.get("events")).get(0);

                    HashMap<String, Object> data = (HashMap<String, Object>) events.get("data");
                    if (events.get("type").equals(type)) {
                        HashMap<String, Object> h = new HashMap<>();
                        h.put("data", data);
                        h.put("timestamp", r.get("timestamp"));
                        results.add(h);
                    }
                }
                start_count = start_count + limit;
                if (breakLoop || response_entity.length < limit) {
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public List<HashMap> queryUserEventInvocationsByBlock(String accountAddress, String module, String eventIdentifier, long start, long end) {

        ArrayList<HashMap> results = new ArrayList<>();
        String url = this.nodeUrl + "/transactions";
        int limit = 100;
        long count = start;

        String type = accountAddress + "::" + module + "::" + eventIdentifier;

        try {
            while (true) {
                String endpoint = url + "?limit=" + limit + "&start=" + count;
                logger.debug("Querying transactions from [{}] to [{}]", count, count + limit);
                String result = sendGetRequest(endpoint);
                ObjectMapper objectMapper = new ObjectMapper();

                HashMap[] response_entity = objectMapper.readValue(result, HashMap[].class);

                for (HashMap r : response_entity) {

                    if (!r.get("type").equals("user_transaction")) continue;


                    ArrayList e = ((ArrayList) r.get("events"));
                    if (e.size() == 0) continue;
                    HashMap<String, Object> events = (HashMap<String, Object>) ((ArrayList) r.get("events")).get(0);

                    HashMap<String, Object> data = (HashMap<String, Object>) events.get("data");
                    if (events.get("type").equals(type)) {
                        HashMap<String, Object> h = new HashMap<>();
                        h.put("data", data);
                        h.put("timestamp", r.get("timestamp"));
                        results.add(h);
                    }
                }
                count = count + limit;

                if (end < count || response_entity.length < limit) {
                    break;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public String getEventSubscriptionMappingValue(String key) {
        if (!eventSubscriptionMapping.containsKey(key)) {
            eventSubscriptionMapping.put(key, this.getLedgerVersion());

        }
        return eventSubscriptionMapping.get(key);
    }

    public void setEventSubscriptionMappingValue(String key, String value) {
        this.eventSubscriptionMapping.put(key, value);
    }

    public String getLedgerVersion() {
        String result = Utils.sendGetRequest(this.nodeUrl);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LedgerInfo ledgerInfo = null;
        try {
            ledgerInfo = mapper.readValue(result, LedgerInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ledgerInfo.ledgerVersion;
    }
}
