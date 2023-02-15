package aptos.models.Exceptions;

public class StructFieldNotFoundException extends AptosClientException {

    public StructFieldNotFoundException(ErrorResponse response) {
        super(response);
    }
}
