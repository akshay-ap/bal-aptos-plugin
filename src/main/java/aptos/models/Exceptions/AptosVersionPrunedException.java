package aptos.models.Exceptions;

public class AptosVersionPrunedException extends AptosClientException {

    public AptosVersionPrunedException(ErrorResponse response) {
        super(response);
    }
}
