package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.model.Parameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    void testInvokeSmartContract() {

        String functionId = "ab3302c28e34326897759e058124bbeebb1eeddb0a64e90430d8acc9688c0bbd/message";
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
        aptosAdapter.invokeSmartContract(functionId, methodName, typeArguments, parameters, outputParameters, 0, 0L, signers, minimumNumberOfSigners);
        
    }

    @Test
    void subscribeToEvent() {
    }

    @Test
    void queryEvents() {
    }

    @Test
    void testConnection() {
    }
}