package aptos.models.Exceptions;

import aptos.models.Exceptions.ErrorCode;

public abstract class AptosClientException extends Exception {
    private final String message;
    // final String errorCode;
    private final int vmErrorCode;

    final private ErrorCode errorCode;

    public AptosClientException(ErrorResponse errorResponse) {
        this.message = errorResponse.message;
        this.vmErrorCode = errorResponse.vmErrorCode;
        this.errorCode = errorResponse.errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getVmErrorCode() {
        return vmErrorCode;
    }


    private ErrorCode maptoEnum(String errorCode) {
        switch (errorCode) {

            case "account_not_found":
                return ErrorCode.ACCOUNT_NOT_FOUND;
            case "resource_not_found":
                return ErrorCode.RESOURCE_NOT_FOUND;
            case "module_not_found":
                return ErrorCode.MODULE_NOT_FOUND;
            case "struct_field_not_found":
                return ErrorCode.STRUCT_FIELD_NOT_FOUND;
            case "version_not_found":
                return ErrorCode.VERSION_NOT_FOUND;
            case "transaction_not_found":
                return ErrorCode.TRANSACTION_NOT_FOUND;
            case "table_item_not_found":
                return ErrorCode.TABLE_ITEM_NOT_FOUND;
            case "block_not_found":
                return ErrorCode.BLOCK_NOT_FOUND;
            case "version_pruned":
                return ErrorCode.VERSION_PRUNED;
            case "block_pruned":
                return ErrorCode.BLOCK_PRUNED;
            case "invalid_input":
                return ErrorCode.INVALID_INPUT;
            case "invalid_transaction_update":
                return ErrorCode.INVALID_TRANSACTION_UPDATE;
            case "sequence_number_too_old":
                return ErrorCode.SEQUENCE_NUMBER_TOO_OLD;
            case "vm_error":
                return ErrorCode.VM_ERROR;
            case "health_check_failed":
                return ErrorCode.HEALTH_CHECK_FAILED;
            case "mempool_is_full":
                return ErrorCode.MEMPOOL_IS_FULL;
            case "internal_error":
                return ErrorCode.INTERNAL_ERROR;
            case "web_framework_error":
                return ErrorCode.WEB_FRAMEWORK_ERROR;
            case "bcs_not_supported":
                return ErrorCode.BCS_NOT_SUPPORTED;
            case "api_disabled":
                return ErrorCode.API_DISABLED;
            default:
                return ErrorCode.INTERNAL_ERROR;
        }
    }
}

