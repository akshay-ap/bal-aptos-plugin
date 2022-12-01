package blockchains.iaas.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import aptos.Event;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AptosClientTest {
    String nodeUrl = "http://localhost:8080/v1";
    String faucetUrl = "http://localhost:8000";

    @Test
    void testSendTransaction() {


        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        String publicKey = "0x6c29675ff84887b60322d6ae07e78e1c209ca652a524c02b1fc258ef0a174918";
        String privateKey = "0x509b3f4fa4bb7a525b7ded5fa0b93412c659784f597f2f553e0fb61ed17da9d5";
        String accountAddress = "0xab3302c28e34326897759e058124bbeebb1eeddb0a64e90430d8acc9688c0bbd";

        Account account = new Account(accountAddress, publicKey, privateKey);
        client.setAccount(account);

        String address = "ab3302c28e34326897759e058124bbeebb1eeddb0a64e90430d8acc9688c0bbd";
        String[] functionArgs = new String[]{"12345"};
        String[] typeArgs = new String[]{};

        String txHash = client.sendTransaction(address, "message", "set_message", typeArgs, functionArgs);

        assertNotNull(txHash);
    }

    @Test
    void testEventQuery() {
        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        Event e = new Event();
        String address = "0xd95447d2ad52363a34423490b8d70ec8312da735f8dedc1a763c79f4599dc5dc";
        String eventHandle = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        String fieldName = "withdraw_events";
        List<HashMap> r = client.queryEventInvocations(address, eventHandle, "0", "1669916205398352");
        assert r.size() != 0;
    }

}