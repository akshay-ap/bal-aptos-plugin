package aptos.models.Exceptions;

public class AptosTransactionNotFoundException extends AptosClientException {

    public AptosTransactionNotFoundException(ErrorResponse response) {
        super(response);
    }
}
