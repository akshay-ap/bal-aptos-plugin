package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class AptosAdapterTest {

    private AptosAdapter aptosAdapter;
    String nodeUrl = "http://localhost:8080/v1";
    String keyFile = this.getClass().getClassLoader().getResource("local_testnet.json").getFile();

    @BeforeEach
    public void before() {
        aptosAdapter = new AptosAdapter(nodeUrl, keyFile);

    }

    @Test
    void testInvokeSmartContract() throws ExecutionException, InterruptedException {

        String functionId = "9f709239a4caf988527df46b7dca3797b740e408e48aa713e79a87fe85a53c4d/message";
        String methodName = "set_message";

        List<Parameter> parameters = new ArrayList<>();

        Parameter parameter = new Parameter();
        parameter.setName("message");
        parameter.setType("string");
        parameter.setValue("test message");

        parameters.add(parameter);

        List<Parameter> outputParameters = new ArrayList<>();

        //        Parameter outputParameter = new Parameter();
        //        outputParameter.setName("test");
        //        outputParameter.setType("string");
        //        outputParameter.setValue("test message");
        //        outputParameters.add(outputParameter);

        List<String> typeArguments = new ArrayList<>();
        List<String> signers = new ArrayList<>();
        long minimumNumberOfSigners = 0;
        Transaction result =
                aptosAdapter.invokeSmartContract(functionId, methodName, typeArguments, parameters, outputParameters, 0, 0L, signers, minimumNumberOfSigners).get();
        assert result.getState() == TransactionState.CONFIRMED;
    }

    @Test
    void testSubscribeToEvent() throws InterruptedException {
        aptosAdapter.subscribeToEvent("", null, null, 1, "").subscribe(o -> {
            System.out.println(o.getIsoTimestamp());
        });

    }

    @Test
    void queryEvents() {
        String address = "0x050467afdd25629e640c2445c2eef7b6d909c741dadd1e73487c9acf32bc016e";
        String eventHandle = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", String.valueOf(System.currentTimeMillis() * 1000));
        String filter = "";
        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, outputParameters, "", timeFrame);
        assert result.isDone();
    }

    @Test
    void queryEvents2() {
        String address = "0x9f709239a4caf988527df46b7dca3797b740e408e48aa713e79a87fe85a53c4d";
        String eventHandle = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", String.valueOf(System.currentTimeMillis() * 1000));
        String filter = "";
        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, outputParameters, "", timeFrame);
        assert result.isDone();
    }

    @Test
    void testConnection() {

        String result = aptosAdapter.testConnection();
        assertNotNull(result);
        assertEquals("{\"message\":\"aptos-node:ok\"}", result);
    }
}