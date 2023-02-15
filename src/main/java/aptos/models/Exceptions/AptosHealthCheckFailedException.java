package aptos.models.Exceptions;

public class AptosHealthCheckFailedException extends AptosClientException {

    public AptosHealthCheckFailedException(ErrorResponse response) {
        super(response);
    }
}
