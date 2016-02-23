package LZMA;

import java.io.IOException;
import java.io.InputStream;

public class LzmaInputStream extends InputStream { // TODO: what is the mvn dependency for this package?

    public LzmaInputStream(InputStream inputStream) {

    }

    @Override
    public int read() throws IOException {
        return -1; // end of file
    }
}
