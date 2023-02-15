package aptos.models.Exceptions;

public class ResourceNotFoundException extends AptosClientException {

    public ResourceNotFoundException(ErrorResponse response) {
        super(response);
    }
}
