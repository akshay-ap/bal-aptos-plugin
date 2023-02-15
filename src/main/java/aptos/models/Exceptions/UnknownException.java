package aptos.models.Exceptions;

public class UnknownException extends AptosClientException {

    public UnknownException(ErrorResponse response) {
        super(response);
    }
}
