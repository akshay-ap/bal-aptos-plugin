package blockchains.iaas.uni.stuttgart.de.plugin;

import blockchains.iaas.uni.stuttgart.de.api.model.Parameter;
import org.junit.jupiter.api.Test;

public class TypeMapperTest {
    @Test
    public void testArrayType() {
        String value = "[false, false, false]";
        String type = "{\"type\": \"array\",\"items\": \"bool\"}";
        String name = "test";
        Parameter p = new Parameter();
        p.setName(name);
        p.setType(type);
        p.setValue(value);

        Object o = AptosTypeMapper.getValue(p);
    }
}
