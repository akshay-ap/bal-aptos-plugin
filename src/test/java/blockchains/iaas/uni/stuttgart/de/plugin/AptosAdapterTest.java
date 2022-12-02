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

        String functionId = "3570b4f94dc613de78c443a1abd3d001f05b2f6a297892c07c9ba51151a4604a/message";
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
        String value = result.getReturnValues().get(0).getValue();
        System.out.println(value + " " + result.getState());
    }

    @Test
    void subscribeToEvent() {
    }

    @Test
    void queryEvents() {
        String address = "0xd95447d2ad52363a34423490b8d70ec8312da735f8dedc1a763c79f4599dc5dc";
        String eventHandle = "0x1::coin::CoinStore<0x1::aptos_coin::AptosCoin>";
        List<Parameter> outputParameters = new ArrayList<>();
        TimeFrame timeFrame = new TimeFrame("0", "1669916205398352");
        String filter = "";
        CompletableFuture<QueryResult> result = aptosAdapter.queryEvents(address, eventHandle, outputParameters, "", timeFrame);
        assert result.isDone();
    }

    @Test
    void testConnection() {
    }
}