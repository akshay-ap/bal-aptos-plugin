package aptos.models.Exceptions;

public class AptosApiDisabledException extends AptosClientException {

    public AptosApiDisabledException(ErrorResponse response) {
        super(response);
    }
}
