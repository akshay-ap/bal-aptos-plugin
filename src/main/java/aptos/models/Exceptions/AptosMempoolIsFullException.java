package aptos.models.Exceptions;

public class AptosMempoolIsFullException extends AptosClientException {

    public AptosMempoolIsFullException(ErrorResponse response) {
        super(response);
    }
}
