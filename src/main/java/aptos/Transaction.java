package aptos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Transaction {
    String endpoint;
    String sequenceNumber;
    String maxGasAmount;
    String gasPrice;
    String expirationTimeInSeconds;
    String address, moduleName, functionName;
    String[] typeArguments, functionArguments;
    TransactionType transactionType;
    Signature signature;
    String sender;

    public void setSignature(Signature signature) {
        this.signature = signature;
    }


    public static class Signature {
        public enum SignatureType {
            ED25519_SIGNATURE {
                @Override
                public String toString() {
                    return "ed25519_signature";
                }
            };

            SignatureType() {

            }
        }

        SignatureType signatureType;
        String publicKey;
        String signature;

        public Signature(SignatureType signatureType, String publicKey, String signature) {
            this.signatureType = signatureType;
            this.publicKey = publicKey;
            this.signature = signature;
        }

        public SignatureType getSignatureType() {
            return signatureType;
        }

        public String getSignatureTypeAsString() {
            return String.valueOf(signatureType);
        }

        public void setSignatureType(SignatureType signatureType) {
            this.signatureType = signatureType;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    public enum TransactionType {
        EntryFunctionPayload {
            @Override
            public String toString() {
                return "entry_function_payload";
            }
        };

        TransactionType() {

        }
    }

    private Transaction(TransactionBuilder builder) {
        this.endpoint = builder.endpoint;
        this.sequenceNumber = builder.sequenceNumber;
        this.maxGasAmount = builder.maxGasAmount;
        this.gasPrice = builder.gasPrice;
        this.expirationTimeInSeconds = builder.expirationTimeInSeconds;
        this.address = builder.address;
        this.moduleName = builder.moduleName;
        this.functionName = builder.functionName;
        this.typeArguments = builder.typeArguments;
        this.functionArguments = builder.functionArguments;
        this.transactionType = builder.transactionType;
        this.sender = builder.sender;
    }

    private ObjectNode getUnsignedTransactionAsObjectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("sender", sender);

        objectNode.put("sequence_number", sequenceNumber);
        objectNode.put("max_gas_amount", maxGasAmount);
        objectNode.put("gas_unit_price", gasPrice);
        objectNode.put("expiration_timestamp_secs", expirationTimeInSeconds);

        ObjectNode payload = mapper.createObjectNode();

        payload.put("type", String.valueOf(transactionType));
        String function = String.format("%s::%s::%s", address, moduleName, functionName);
        payload.put("function", function);

        ArrayNode arrayTypeArguments = mapper.valueToTree(typeArguments);
        payload.putArray("type_arguments").addAll(arrayTypeArguments);

        ArrayNode arrayFunctionArguments = mapper.valueToTree(functionArguments);
        payload.putArray("arguments").addAll(arrayFunctionArguments);

        objectNode.set("payload", payload);
        return objectNode;
    }

    public String getUnsignedTransactionAsJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = getUnsignedTransactionAsObjectNode();

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSignedTransactionAsJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = getUnsignedTransactionAsObjectNode();
        ObjectNode objectNodeSignature = mapper.createObjectNode();
        objectNodeSignature.put("type", String.valueOf(signature.getSignatureType()));
        objectNodeSignature.put("public_key", signature.getPublicKey());
        objectNodeSignature.put("signature", signature.getSignature());

        objectNode.set("signature", objectNodeSignature);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class TransactionBuilder {
        String endpoint;
        String sequenceNumber;
        String sender;

        public String getMaxGasAmount() {
            return maxGasAmount;
        }

        public TransactionBuilder setMaxGasAmount(String maxGasAmount) {
            this.maxGasAmount = maxGasAmount;
            return this;
        }

        public String getGasPrice() {
            return gasPrice;
        }

        public TransactionBuilder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public String getExpirationTimeInSeconds() {
            return expirationTimeInSeconds;
        }

        public TransactionBuilder setExpirationTimeInSeconds(String expirationTimeInSeconds) {
            this.expirationTimeInSeconds = expirationTimeInSeconds;
            return this;
        }

        String maxGasAmount = "1500";
        String gasPrice = "1";
        String expirationTimeInSeconds = "32425224034";
        String address, moduleName, functionName;
        String[] typeArguments, functionArguments;
        TransactionType transactionType;

        public TransactionBuilder(String sender, String address, String moduleName, String functionName, String[] typeArguments, String[] functionArguments, String endpoint, String sequenceNumber) {
            this.endpoint = endpoint;
            this.address = address;
            this.moduleName = moduleName;
            this.functionName = functionName;
            this.typeArguments = typeArguments;
            this.functionArguments = functionArguments;
            this.sequenceNumber = sequenceNumber;
            this.transactionType = TransactionType.EntryFunctionPayload;
            this.sender = sender;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}