package aptos.models.Exceptions;

public class AptosVMErrorException extends AptosClientException {

    public AptosVMErrorException(ErrorResponse response) {
        super(response);
    }
}
