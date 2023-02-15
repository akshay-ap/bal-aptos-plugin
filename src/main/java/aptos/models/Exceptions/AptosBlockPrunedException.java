package aptos.models.Exceptions;

public class AptosBlockPrunedException extends AptosClientException {

    public AptosBlockPrunedException(ErrorResponse response) {
        super(response);
    }
}
