package aptos.models.Exceptions;

public class AptosNodeInternalError extends AptosClientException {

    public AptosNodeInternalError(ErrorResponse response) {
        super(response);
    }
}
