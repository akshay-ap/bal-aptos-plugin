package aptos.models.Exceptions;

public class AptosBCSNotSupportedException extends AptosClientException {

    public AptosBCSNotSupportedException(ErrorResponse response) {
        super(response);
    }
}
