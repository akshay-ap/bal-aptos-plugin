package aptos.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LedgerInfo {
    @JsonProperty("chain_id")
    public int chainId;

    @JsonProperty("epoch")
    public String epoch;

    @JsonProperty("ledger_version")
    public String ledgerVersion;

}
