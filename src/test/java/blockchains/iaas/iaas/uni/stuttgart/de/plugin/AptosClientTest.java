package blockchains.iaas.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import aptos.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AptosClientTest {
    String nodeUrl = "http://localhost:8080/v1";
    String faucetUrl = "http://localhost:8000";

    @Test
    void testSendTransaction() throws Exception {
        String keyFile = this.getClass().getClassLoader().getResource("local_testnet.json").getFile();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> example = objectMapper.readValue(new File(keyFile), Map.class);
        String publicKey = example.get("public_key");
        String privateKey = example.get("private_key");
        String accountAddress = example.get("account");
        Account account = new Account(accountAddress, publicKey, privateKey);

        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        client.setAccount(account);

        String address = accountAddress;
        String[] functionArgs = new String[]{"12345"};
        String[] typeArgs = new String[]{};
        String seq = client.getSequenceNumber();
        String txHash = client.sendTransaction(address, "message", "set_message", typeArgs, functionArgs, seq);

        assertNotNull(txHash);
    }

    @Test
    void testEventQuery() {
        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        Event e = new Event();
        String address = "0x050467afdd25629e640c2445c2eef7b6d909c741dadd1e73487c9acf32bc016e";
        String eventHandle = "0x1::account::CoinRegisterEvent";
        String fieldName = "withdraw_events";
        List<HashMap> r = client.queryEventInvocations(address, eventHandle, "0", String.valueOf(System.currentTimeMillis() * 1000));
        assert r.size() != 0;
    }

    @Test
    void testEventQuery2() {
        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        Event e = new Event();
        String address = "0x9f709239a4caf988527df46b7dca3797b740e408e48aa713e79a87fe85a53c4d";
        String eventHandle = "0x1::account::CoinRegisterEvent";
        String fieldName = "withdraw_events";
        List<HashMap> r = client.queryEventInvocations(address, eventHandle, "0", String.valueOf(System.currentTimeMillis() * 1000));
        assert r.size() != 0;
    }

}