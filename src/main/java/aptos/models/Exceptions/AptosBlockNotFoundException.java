package aptos.models.Exceptions;

public class AptosBlockNotFoundException extends AptosClientException {

    public AptosBlockNotFoundException(ErrorResponse response) {
        super(response);
    }
}
