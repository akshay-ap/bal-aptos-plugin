package blockchains.iaas.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import aptos.Event;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AptosClientTest {

    @Test
    void testSendTransaction() {
        String nodeUrl = "http://localhost:8080/v1";
        String faucetUrl = "http://localhost:8000";

        AptosClient client = new AptosClient(nodeUrl, faucetUrl);

        String publicKey = "0x3a61beaa7a390f22a0f8f9b11e080f921b61295a721ab5719dbdf434d75e5126";
        String privateKey = "0xba2563387585214194cfb2304a0ec24100a943bc4bd3280860a09bd55da2ef08";
        String accountAddress = "0x0bc42505a3fef42173fddc558f195725bc913c3b0b02087e2d92b6163081f2ff";

        Account account = new Account(accountAddress, publicKey, privateKey);
        client.setAccount(account);

        String address = "0bc42505a3fef42173fddc558f195725bc913c3b0b02087e2d92b6163081f2ff";
        String[] functionArgs = new String[]{"12345"};
        String[] typeArgs = new String[]{};

        String txHash = client.sendTransaction(address, "message", "set_message", typeArgs, functionArgs);

        assertNotNull(txHash);
    }

    @Test
    void testEventQuery() {

        Event e = new Event();
        String address = "";
        String eventHandle = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        String fieldName = "withdraw_events";
        try {
            Response r = e.queryEvent(address, eventHandle, fieldName);
                
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}