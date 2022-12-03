package blockchains.iaas.uni.stuttgart.de.plugin;

import aptos.Account;
import aptos.AptosClient;
import blockchains.iaas.uni.stuttgart.de.api.exceptions.*;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import blockchains.iaas.uni.stuttgart.de.api.model.*;
import blockchains.iaas.uni.stuttgart.de.api.utils.SmartContractPathParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
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
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

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
                                                              long timeout,
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
//        Observable<Occurrence> o = Observable.create(emitter -> {
//            while (!emitter.isDisposed()) {
//                Occurrence occurrence = new Occurrence();
//                ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
//                        ZoneId.systemDefault());
//
//                occurrence.setIsoTimestamp(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//
//                List<Parameter> parameters = new ArrayList<>();
//                Parameter p = new Parameter();
//                p.setName("Dummy");
//                p.setValue("DummyValue");
//                p.setType("DummyType");
//                parameters.add(p);
//                occurrence.setParameters(parameters);
//                emitter.onNext(occurrence);
//            }
//        });
        return Observable.interval(1, 1, TimeUnit.SECONDS).map((t) -> {
            Occurrence occurrence = new Occurrence();
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
                    ZoneId.systemDefault());

            occurrence.setIsoTimestamp(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            List<Parameter> parameters = new ArrayList<>();
            Parameter p = new Parameter();
            p.setName("Dummy");
            p.setValue("DummyValue");
            p.setType("DummyType");
            parameters.add(p);
            occurrence.setParameters(parameters);
            return occurrence;
        });
    }

    @Override
    public CompletableFuture<QueryResult> queryEvents(String smartContractAddress, String eventIdentifier,
                                                      List<Parameter> outputParameters, String filter, TimeFrame timeFrame) throws BalException {
        List<HashMap> invocationResult = this.aptosClient.queryEventInvocations(smartContractAddress, eventIdentifier, timeFrame.getFrom(), timeFrame.getTo());

        QueryResult result = new QueryResult();
        List<Occurrence> occurrences = new ArrayList<>();

        for (HashMap i : invocationResult) {
            Long timeinMillis = Long.valueOf((String) i.get("timestamp"));
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timeinMillis),
                    ZoneId.systemDefault());
            System.out.println(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            Occurrence o = new Occurrence();
            o.setIsoTimestamp(zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            List<Parameter> parameters = new ArrayList<>();
            HashMap<String, Object> events = (HashMap<String, Object>) ((ArrayList) i.get("events")).get(0);

            HashMap<String, Object> data = (HashMap<String, Object>) events.get("data");
            for (Map.Entry<String, Object> set :
                    data.entrySet()) {

                // Printing all elements of a Map
                System.out.println(set.getKey() + " = "
                        + set.getValue());
                Parameter p = new Parameter(set.getKey(), "string", set.getValue().toString());
                parameters.add(p);
            }
            o.setParameters(parameters);
            occurrences.add(o);

        }

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
                System.out.println(response.getProtocolVersion());              // HTTP/1.1
                System.out.println(response.getStatusLine().getStatusCode());   // 200
                System.out.println(response.getStatusLine().getReasonPhrase()); // OK
                System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK

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
}
