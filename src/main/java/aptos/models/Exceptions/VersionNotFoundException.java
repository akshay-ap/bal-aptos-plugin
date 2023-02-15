package aptos.models.Exceptions;

public class VersionNotFoundException extends AptosClientException {

    public VersionNotFoundException(ErrorResponse response) {
        super(response);
    }
}
