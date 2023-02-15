package aptos.models.Exceptions;

public class AptosWebFrameworkError extends AptosClientException {

    public AptosWebFrameworkError(ErrorResponse response) {
        super(response);
    }
}
