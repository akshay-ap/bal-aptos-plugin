package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;

public class AptosConnectionProfile extends AbstractConnectionProfile {


    public AptosConnectionProfile() {
    }

    private String nodeUrl;

    public AptosConnectionProfile(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }
}
