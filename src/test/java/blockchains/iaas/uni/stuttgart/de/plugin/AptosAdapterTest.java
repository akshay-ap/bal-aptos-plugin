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

    @BeforeEach
    public void before() {
        aptosAdapter = new AptosAdapter(nodeUrl);

    }

    @Test
    void testInvokeSmartContract() {

        String functionId = "0bc42505a3fef42173fddc558f195725bc913c3b0b02087e2d92b6163081f2ff/message";
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
        aptosAdapter.invokeSmartContract(functionId, methodName, parameters, outputParameters,0, 100000000000000000L);

//        aptosAdapter.invokeSmartContract(functionId, methodName, parameters, outputParameters,0, 100000000000000000L);

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