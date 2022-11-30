package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.IAdapterExtension;
import blockchains.iaas.uni.stuttgart.de.api.connectionprofiles.AbstractConnectionProfile;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

public class AptosPlugin extends Plugin {
    public AptosPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Extension
    public static class AptosAdapterImpl implements IAdapterExtension {

        @Override
        public BlockchainAdapter getAdapter(AbstractConnectionProfile abstractConnectionProfile) {
            AptosConnectionProfile aptosConnectionProfile = (AptosConnectionProfile) abstractConnectionProfile;

            String nodeUrl = aptosConnectionProfile.getNodeUrl();
            String keyFile = aptosConnectionProfile.getKeyFile();

            // int averageBlockTimeSeconds = Integer.parseInt(parameters.get("averageBlockTimeSeconds"));
            return new AptosAdapter(nodeUrl, keyFile);
        }


        @Override
        public Class<? extends AbstractConnectionProfile> getConnectionProfileClass() {
            return AptosConnectionProfile.class;
        }

        @Override
        public String getConnectionProfileNamedType() {
            return null;
        }

        @Override
        public String getBlockChainId() {
            return "aptos";
        }

    }

}
