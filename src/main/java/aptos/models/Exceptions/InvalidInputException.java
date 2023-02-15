package aptos.models.Exceptions;

public class InvalidInputException extends AptosClientException {

    public InvalidInputException(ErrorResponse response) {
        super(response);
    }
}
