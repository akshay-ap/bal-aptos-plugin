package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                aptosAdapter.invokeSmartContract(functionId, methodName, typeArguments, parameters, outputParameters, 0, 0L, "", "", signers, minimumNumberOfSigners).get();
        assert result.getState() == TransactionState.CONFIRMED;
    }

    @Test
    void testInvokeSmartContract2() throws ExecutionException, InterruptedException, JsonProcessingException {

        String stringType = "{\"type\": \"string\"}";
        String integerType = "{\"type\": \"integer\", \"minimum\": \"0\", \"maximum\":\"18446744073709551615\"}";
        String booleanArrayType = "{\"type\": \"array\", \"items\": \"boolean\"}";


        String functionId = "0x3/token";
        String methodName = "create_collection_script";

        List<Parameter> parameters = new ArrayList<>();

        Parameter name = new Parameter();
        name.setName("name");
        name.setType(stringType);
        name.setValue("test message");
        parameters.add(name);

        Parameter description = new Parameter();
        description.setName("description");
        description.setType(stringType);
        description.setValue("test message");
        parameters.add(description);

        Parameter uri = new Parameter();
        uri.setName("uri");
        uri.setType(stringType);
        uri.setValue("test message");
        parameters.add(uri);

        Parameter supply = new Parameter();
        supply.setName("supply");
        supply.setType(integerType);
        supply.setValue("123");
        parameters.add(supply);


        Parameter mutateSettings = new Parameter();
        mutateSettings.setName("mutate_setting");
        mutateSettings.setType(booleanArrayType);

        List<Boolean> list = new ArrayList<Boolean>();
        list.add(false);
        list.add(false);
        list.add(false);
        final ObjectMapper mapper = new ObjectMapper();
        mutateSettings.setValue(mapper.writeValueAsString(list));

        parameters.add(mutateSettings);

        List<Parameter> outputParameters = new ArrayList<>();

        List<String> typeArguments = new ArrayList<>();
        List<String> signers = new ArrayList<>();
        long minimumNumberOfSigners = 0;
        Transaction result =
                aptosAdapter.invokeSmartContract(functionId, methodName, typeArguments, parameters, outputParameters, 0, 0L, "", "", signers, minimumNumberOfSigners).get();
        assert result.getState() == TransactionState.CONFIRMED;
    }

    @Test
    void testSubscribeToEvent() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                aptosAdapter.subscribeToEvent("0x3/token", "CreateCollectionEvent", null, 1, "").subscribe(o -> {
                    System.out.println(o.getIsoTimestamp());
                });
            }
        });

        t.start();

        Thread.sleep(30000);

    }

    @Test
    void queryEvents() throws ExecutionException, InterruptedException {
        String address = "0x3/token";
        String eventHandle = "CreateCollectionEvent";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", String.valueOf(System.currentTimeMillis() * 1000));
        String filter = "";
        List<String> typeArguments = new ArrayList<>();

        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, typeArguments, outputParameters, "", timeFrame);
        assert result.isDone();
        assert result.get().getOccurrences().size() != 0;
    }

    @Test
    void queryEvents2() {
        String address = "0x1/coin";
        String eventHandle = "CoinStore<0x1::aptos_coin::AptosCoin>";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", String.valueOf(System.currentTimeMillis() * 1000));
        String filter = "";
        List<String> typeArguments = new ArrayList<>();

        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, typeArguments, outputParameters, "", timeFrame);
        assert result.isDone();
    }

    @Test
    void queryEvents3() {
        String address = "0x3/token";
        String eventHandle = "CreateTokenDataEvent";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", String.valueOf(System.currentTimeMillis() * 1000));
        String filter = "";
        List<String> typeArguments = new ArrayList<>();

        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, typeArguments, outputParameters, "", timeFrame);
        assert result.isDone();
    }


    @Test
    void testConnection() {

        String result = aptosAdapter.testConnection();
        assertNotNull(result);
        assertEquals("{\"message\":\"aptos-node:ok\"}", result);
    }
}