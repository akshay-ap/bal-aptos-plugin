package blockchains.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.BalException;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.InvalidTransactionException;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.NotSupportedException;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AptosAdapter implements BlockchainAdapter {

    private String nodeUrl;
    private AptosClient aptosClient;
    private static Logger logger = Logger.getLogger(AptosAdapter.class.getName());

    public AptosAdapter(String nodeUrl, String keyFile) {
        this.nodeUrl = nodeUrl;
        this.aptosClient = new AptosClient(nodeUrl, keyFile);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> example = objectMapper.readValue(new File(keyFile), Map.class);
            String publicKey = example.get("public_key");
            String privateKey = example.get("private_key");
            String accountAddress = example.get("account");
            Account account = new Account(accountAddress, publicKey, privateKey);
            this.aptosClient.setAccount(account);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public CompletableFuture<Transaction> submitTransaction(String s, BigDecimal bigDecimal, double v) throws InvalidTransactionException, NotSupportedException {
        return null;
    }

    @Override
    public Observable<Transaction> receiveTransactions(String s, double v) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<TransactionState> ensureTransactionState(String s, double v) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<TransactionState> detectOrphanedTransaction(String s) throws NotSupportedException {
        return null;
    }

    @Override
    public CompletableFuture<Transaction> invokeSmartContract(String smartContractPath,
                                                              String functionIdentifier,
                                                              List<String> typeArguments,
                                                              List<Parameter> inputs,
                                                              List<Parameter> outputs,
                                                              double requiredConfidence,
                                                              long timeout,
                                                              List<String> signers,
                                                              long minimumNumberOfSignatures) throws BalException {
        String[] path = SmartContractPathParser.parse(smartContractPath).getSmartContractPathSegments();
        assert (path.length == 2);

        ArrayList<String> arguments = new ArrayList<>();

        for (Parameter a : inputs) {
            arguments.add(a.getValue());
        }

        String txHash = aptosClient.sendTransaction(path[0], path[1], functionIdentifier, typeArguments.toArray(new String[0]), arguments.toArray(new String[0]));
        logger.info("Transaction hash: " + txHash);
        return null;
    }

    @Override
    public Observable<Occurrence> subscribeToEvent(String smartContractAddress, String eventIdentifier,
                                                   List<Parameter> outputParameters, double degreeOfConfidence, String filter) throws BalException {
        return null;
    }

    @Override
    public CompletableFuture<QueryResult> queryEvents(String smartContractAddress, String eventIdentifier,
                                                      List<Parameter> outputParameters, String filter, TimeFrame timeFrame) throws BalException {
        return null;
    }

    @Override
    public String testConnection() {
        return null;
    }

    @Override
    public boolean signInvocation(String s, String s1) {

        return false;
    }

    @Override
    public List<Transaction> getPendingInvocations() {
        return null;
    }

    @Override
    public CompletableFuture<Transaction> tryReplaceInvocation(String s, String s1, String s2, List<String> list, List<Parameter> list1, List<Parameter> list2, double v, List<String> list3, long l) {
        return null;
    }

    @Override
    public void tryCancelInvocation(String s) {

    }
}
