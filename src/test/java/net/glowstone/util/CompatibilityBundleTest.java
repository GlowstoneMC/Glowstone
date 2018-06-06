package net.glowstone.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CompatibilityBundleTest {
    @Test
    public void fromConfigTest() {
        assertConfigValueMatchesBundle(null, CompatibilityBundle.CRAFTBUKKIT);
        assertConfigValueMatchesBundle("", CompatibilityBundle.CRAFTBUKKIT);
        assertConfigValueMatchesBundle("     ", CompatibilityBundle.CRAFTBUKKIT);

        assertConfigValueMatchesBundle("CRAFTBUKKIT", CompatibilityBundle.CRAFTBUKKIT);
        assertConfigValueMatchesBundle("craftbukkit", CompatibilityBundle.CRAFTBUKKIT);
        assertConfigValueMatchesBundle("cRaFtBuKkIt", CompatibilityBundle.CRAFTBUKKIT);

        assertConfigValueMatchesBundle("NONE", CompatibilityBundle.NONE);
        assertConfigValueMatchesBundle("none", CompatibilityBundle.NONE);
        assertConfigValueMatchesBundle("NoNe", CompatibilityBundle.NONE);

        assertConfigValueMatchesBundle("unknown", null);
        assertConfigValueMatchesBundle(" CRAFTBUKKIT ", null);
        assertConfigValueMatchesBundle("NONE.", null);
    }

    private void assertConfigValueMatchesBundle(String configValue, CompatibilityBundle expected) {
        CompatibilityBundle actual = CompatibilityBundle.fromConfig(configValue);
        Assert.assertEquals(actual, expected);
    }
}
