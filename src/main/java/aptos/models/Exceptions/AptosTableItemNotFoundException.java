package aptos.models.Exceptions;

public class AptosTableItemNotFoundException extends AptosClientException {

    public AptosTableItemNotFoundException(ErrorResponse response) {
        super(response);
    }
}
