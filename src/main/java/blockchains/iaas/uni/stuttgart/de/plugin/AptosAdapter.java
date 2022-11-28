package blockchains.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.BalException;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.InvalidTransactionException;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.NotSupportedException;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import io.reactivex.Observable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AptosAdapter implements BlockchainAdapter {

    private String nodeUrl;
    private AptosClient aptosClient;
    private static Logger logger = Logger.getLogger(AptosAdapter.class.getName());
    ;

    public AptosAdapter(String nodeUrl) {
        this.nodeUrl = nodeUrl;
        this.aptosClient = new AptosClient(nodeUrl, null);

        // TODO: Read from file
        String publicKey = "0x3a61beaa7a390f22a0f8f9b11e080f921b61295a721ab5719dbdf434d75e5126";
        String privateKey = "0xba2563387585214194cfb2304a0ec24100a943bc4bd3280860a09bd55da2ef08";
        String accountAddress = "0x0bc42505a3fef42173fddc558f195725bc913c3b0b02087e2d92b6163081f2ff";

        Account account = new Account(accountAddress, publicKey, privateKey);
        this.aptosClient.setAccount(account);

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
                                                              List<Parameter> inputs, List<Parameter> outputs,
                                                              double requiredConfidence,
                                                              long timeoutMillis) throws BalException {
        String[] path = SmartContractPathParser.parse(smartContractPath).getSmartContractPathSegments();
        assert (path.length == 2);

        ArrayList<String> arguments = new ArrayList<>();

        for (Parameter a : inputs) {
            arguments.add(a.getValue());
        }

        ArrayList<String> typeArguments = new ArrayList<>();


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
}
