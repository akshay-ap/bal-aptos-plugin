package aptos.models.Exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ErrorCode {
    @JsonProperty("account_not_found")
    ACCOUNT_NOT_FOUND,
    @JsonProperty("resource_not_found")

    RESOURCE_NOT_FOUND,
    @JsonProperty("module_not_found")

    MODULE_NOT_FOUND,
    @JsonProperty("struct_field_not_found")
    STRUCT_FIELD_NOT_FOUND,
    @JsonProperty("version_not_found")
    VERSION_NOT_FOUND,
    @JsonProperty("transaction_not_found")
    TRANSACTION_NOT_FOUND,
    @JsonProperty("table_item_not_found")
    TABLE_ITEM_NOT_FOUND,
    @JsonProperty("block_not_found")
    BLOCK_NOT_FOUND,

    @JsonProperty("version_pruned")
    VERSION_PRUNED,
    @JsonProperty("block_pruned")
    BLOCK_PRUNED,
    @JsonProperty("invalid_input")
    INVALID_INPUT,
    @JsonProperty("invalid_transaction_update")
    INVALID_TRANSACTION_UPDATE,
    @JsonProperty("sequence_number_too_old")
    SEQUENCE_NUMBER_TOO_OLD,
    @JsonProperty("vm_error")
    VM_ERROR,
    @JsonProperty("health_check_failed")
    HEALTH_CHECK_FAILED,
    @JsonProperty("mempool_is_full")
    MEMPOOL_IS_FULL,
    @JsonProperty("internal_error")
    INTERNAL_ERROR,
    @JsonProperty("web_framework_error")
    WEB_FRAMEWORK_ERROR,
    @JsonProperty("bcs_not_supported")
    BCS_NOT_SUPPORTED,
    @JsonProperty("api_disabled")
    API_DISABLED
}
