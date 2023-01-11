package blockchains.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class AptosAdapter implements BlockchainAdapter {

    private String nodeUrl;
    private AptosClient aptosClient;
    private static final Logger logger = LoggerFactory.getLogger(AptosAdapter.class.getName());


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
                                                              long timeoutMillis,
                                                              String signature,
                                                              String signer,
                                                              List<String> signers,
                                                              long minimumNumberOfSignatures) throws BalException {
        String[] path = SmartContractPathParser.parse(smartContractPath).getSmartContractPathSegments();
        assert (path.length == 2);

        ArrayList<String> arguments = new ArrayList<>();

        for (Parameter a : inputs) {
            arguments.add(a.getValue());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String seq = aptosClient.getSequenceNumber();
                String txHash = aptosClient.sendTransaction(path[0], path[1], functionIdentifier, typeArguments.toArray(new String[0]), arguments.toArray(new String[0]), seq);
                logger.info("Transaction hash: " + txHash);
                return CompletableFuture.completedFuture(txHash);
            } catch (Exception e) {
                throw new CompletionException(wrapAtposExceptions(e));
            }
        }).thenApply((txhash) -> {
            Transaction tx = new Transaction();
            tx.setState(TransactionState.CONFIRMED);
            tx.setReturnValues(new ArrayList<>());
            return tx;
        }).exceptionally(e -> {
            logger.info("Invocation failed with exception : " + e.getMessage());
            throw wrapAtposExceptions(e);
        });
        // tx.complete();
    }

    private static CompletionException wrapAtposExceptions(Throwable e) {
        return new CompletionException(mapAptosException(e));
    }

    @Override
    public Observable<Occurrence> subscribeToEvent(String smartContractAddress, String eventIdentifier,
                                                   List<Parameter> outputParameters, double degreeOfConfidence, String filter) throws BalException {
        return Observable.interval(0, 10, TimeUnit.SECONDS).map((t) -> {
            String key = smartContractAddress + "::" + eventIdentifier;
            String start = aptosClient.getEventSubscriptionMappingValue(key);
            String[] path = SmartContractPathParser.parse(smartContractAddress).getSmartContractPathSegments();
            assert (path.length == 2);
            long end = Long.parseLong(aptosClient.getLedgerVersion());
            List<HashMap> result = aptosClient.queryUserEventInvocationsByBlock(path[0], path[1], eventIdentifier,
                    Long.parseLong(start), end);
            aptosClient.setEventSubscriptionMappingValue(key, String.valueOf(end));

            List<Occurrence> occurrences = transformInvocationResultToOccurrences(result);
            return occurrences;
        }).flatMapIterable(x -> x);
    }

    @Override
    public CompletableFuture<QueryResult> queryEvents(String smartContractAddress, String eventIdentifier, List<String> typeArguments,
                                                      List<Parameter> outputParameters, String filter, TimeFrame timeFrame) throws BalException {

        String[] path = SmartContractPathParser.parse(smartContractAddress).getSmartContractPathSegments();
        assert (path.length == 2);

        List<HashMap> invocationResult = this.aptosClient.queryUserEventInvocations(path[0], path[1], eventIdentifier, timeFrame.getFrom(), timeFrame.getTo());

        QueryResult result = new QueryResult();
        List<Occurrence> occurrences = transformInvocationResultToOccurrences(invocationResult);

        result.setOccurrences(occurrences);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public String testConnection() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        try {

            HttpGet request = new HttpGet(nodeUrl + "/-/healthy");

            // add request headers
            request.addHeader("custom-key", "mkyong");
            request.addHeader(HttpHeaders.USER_AGENT, "Googlebot");

            CloseableHttpResponse response = httpClient.execute(request);

            try {
                // Get HttpResponse Status
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    result = EntityUtils.toString(entity);
                    logger.info("Test connection: " + result);
                }

            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public CompletableFuture<Transaction> tryReplaceInvocation(String correlationId, String smartContractPath,
                                                               String functionIdentifier,
                                                               List<String> typeArguments,
                                                               List<Parameter> inputs,
                                                               List<Parameter> outputs,
                                                               double requiredConfidence,
                                                               String signature,
                                                               String signer,
                                                               List<String> signers,
                                                               long minimumNumberOfSignatures) {
        return null;
    }

    @Override
    public boolean tryCancelInvocation(String s) {
        return false;
    }

    private static BalException mapAptosException(Throwable e) {
        BalException result;

        if (e instanceof BalException)
            result = (BalException) e;
        else if (e.getCause() instanceof BalException)
            result = (BalException) e.getCause();
        else if (e.getCause() instanceof IOException)
            result = new BlockchainNodeUnreachableException(e.getMessage());
        else if (e instanceof IllegalArgumentException || e instanceof OperationNotSupportedException)
            result = new InvokeSmartContractFunctionFailure(e.getMessage());
        else if (e.getCause() instanceof RuntimeException)
            result = new InvalidTransactionException(e.getMessage());
        else {
            logger.error("Unexpected exception was thrown!");
            result = new InvalidTransactionException(e.getMessage());
        }

        return result;
    }

    private List<Occurrence> transformInvocationResultToOccurrences(List<HashMap> invocationResult) {
        List<Occurrence> occurrences = new ArrayList<>();
        for (HashMap i : invocationResult) {
            Long timeinMillis = Long.valueOf((String) i.get("timestamp"));
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeinMillis),
                    ZoneId.systemDefault());
            System.out.println(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            Occurrence o = new Occurrence();
            o.setIsoTimestamp(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            List<Parameter> parameters = new ArrayList<>();
            try {
                HashMap<String, Object> data = (HashMap<String, Object>) i.get("data");
                for (Map.Entry<String, Object> set :
                        data.entrySet()) {
                    Parameter p = new Parameter(set.getKey(), "string", set.getValue().toString());
                    parameters.add(p);
                }
                o.setParameters(parameters);
                occurrences.add(o);
            } catch (IndexOutOfBoundsException e) {
                logger.error("Skipping events n tx: " + i.get("hash"), e);
            }
        }
        return occurrences;
    }
}
