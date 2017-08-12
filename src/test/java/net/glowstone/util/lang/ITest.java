package net.glowstone.util.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import lombok.Cleanup;

public class ITest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMissingTranslation() {
        thrown.expect(MissingTranslationException.class);
        I.tr("nonexisting.translation");
    }

    @Test
    public void testFallbackLocaleExists() {
        assertThat(I.doesLocaleExist(I.FALLBACK_LOCALE), is(true));
    }

    @Test
    public void testTranslationKeysFormat() {
        for (String key : I.getTranslationKeys(I.getDefaultLocale())) {
            assertThat(key.equals(key.toLowerCase()), is(true));
        }
    }

    @Test
    public void testCommandUsage() {
        for (String key : I.getTranslationKeys(I.getDefaultLocale())) {
            // Skip if generic or not usage
            if (key.equalsIgnoreCase("command.generic.usage") || !key.startsWith("command.minecraft") || !key.contains("usage")) {
                continue;
            }
            assertThat("Expected translation result of key \"" + key + "\" to contain command \"" + "/" + key.split("\\.")[2] + "\".", I.tr(key).contains("/" + key.split("\\.")[2]), is(true));
        }
    }

    @Test
    public void testUnusedTranslationKeys() throws IOException {
        File scanFolder = new File(new File(ITest.class.getResource("/").getPath()).getParentFile().getParentFile().getPath());
        scanFolder = new File(scanFolder, "src");
        scanFolder = new File(scanFolder, "main");
        scanFolder = new File(scanFolder, "java");

        // Store the result from the key lookup as <key, found>
        HashMap<String, Boolean> result = new HashMap<>();
        for (String key : I.getTranslationKeys(I.getDefaultLocale())) {
            result.put(key, false); // Add all keys as default to false
        }

        // Recursively walk through all source files
        List<File> files = new ArrayList<>();
        iterateFiles(files, scanFolder);

        // Check all files for the apparance of a translation key
        for (File file : files) {
            @Cleanup BufferedReader br = new BufferedReader(new FileReader(file));
            for (String line; (line = br.readLine()) != null; ) {
                for (String key : result.keySet()) {
                    if(line.contains(key)) {
                        result.put(key, true);
                    }
                }
            }
        }

        // Fail if a key wasn't found in source code
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            assertThat("Found unused translation key \"" + entry.getKey() + "\". Is it typed correctly both in the source code and in the language file?", entry.getValue(), is(true));
        }
        
    }

    private void iterateFiles(List<File> result, File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
           if (file.isDirectory()) {
               iterateFiles(result, file);
           } else {
               result.add(file);
            }
        }
    }

}
