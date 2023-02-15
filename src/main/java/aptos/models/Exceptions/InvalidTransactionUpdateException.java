package aptos.models.Exceptions;

public class InvalidTransactionUpdateException extends AptosClientException {

    public InvalidTransactionUpdateException(ErrorResponse response) {
        super(response);
    }
}
