package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.exceptions.ParameterException;
import blockchains.iaas.uni.stuttgart.de.api.model.Parameter;
import blockchains.iaas.uni.stuttgart.de.api.utils.MathUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.math.BigInteger;

public class AptosTypeMapper {

    public static Object getValue(Parameter parameter) throws ParameterException {
        String type = parameter.getType();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode actualObj = mapper.readTree(type);
            String dataType = actualObj.get("type").textValue();
            String rawValue = parameter.getValue();

            switch (dataType) {
                case "string":
                    return rawValue;
                case "boolean":
                    return handleBooleanType(rawValue);
                case "integer":
                    return handleIntegerType(rawValue, actualObj);
                case "array":
                    return handleArrayType(rawValue, actualObj);
            }
        } catch (JsonProcessingException e) {
            throw new ParameterException("Invalid value in type");
        }

        throw new ParameterException("Unrecognized type: " + type);
    }

    private static boolean handleBooleanType(String value) {
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        }
        throw new ParameterException("Unrecognized boolean type");
    }

    private static String handleIntegerType(String value, JsonNode jsonObject) throws ArithmeticException {
        if (jsonObject.has("minimum") && jsonObject.has("maximum")) {
            BigInteger minimum = new BigInteger(jsonObject.get("minimum").textValue());
            BigInteger maximum = new BigInteger(jsonObject.get("maximum").textValue());

            if (minimum.equals(BigInteger.ZERO)) {
                if (maximum.compareTo(BigInteger.ZERO) > 0) {
                    int m = MathUtils.log2(maximum.add(BigInteger.ONE));
                    if (m % 8 == 0) {
                        return value;
                    }
                }
            }
        }

        throw new ParameterException("Unrecognized integer type!");
    }

    private static Object[] handleArrayType(String rawValue, JsonNode jsonObject) {
        if (jsonObject.has("items")) {

            // get the "items" schema, tuples are not yet supported!

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = null;

            try {
                arrayNode = (ArrayNode) mapper.readTree(rawValue);
            } catch (JsonProcessingException e) {
                throw new ParameterException("Unrecognized array value");
            }

            System.out.println(arrayNode);
            Object[] result = new Object[arrayNode.size()];
            for (int i = 0; i < arrayNode.size(); i++) {
                result[i] = arrayNode.get(i);
            }
            return result;
        }

        throw new ParameterException("Unrecognized array type!");
    }

}
