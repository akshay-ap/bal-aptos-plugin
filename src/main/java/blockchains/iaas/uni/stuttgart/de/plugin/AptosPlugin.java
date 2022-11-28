package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.IAdapterExtenstion;
import blockchains.iaas.uni.stuttgart.de.api.interfaces.BlockchainAdapter;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.util.Map;

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
    public static class AptosAdapterImpl implements IAdapterExtenstion {

        @Override
        public BlockchainAdapter getAdapter(Map<String, String> parameters) {
            String nodeUrl = parameters.get("nodeUrl");
            int averageBlockTimeSeconds = Integer.parseInt(parameters.get("averageBlockTimeSeconds"));
            return new AptosAdapter(nodeUrl);
        }

        @Override
        public String getBlockChainId() {
            return "ethereum";
        }

    }

}
