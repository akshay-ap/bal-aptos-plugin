package aptos.models.Exceptions;

public class ModuleNotFoundException extends AptosClientException {

    public ModuleNotFoundException(ErrorResponse response) {
        super(response);
    }
}
