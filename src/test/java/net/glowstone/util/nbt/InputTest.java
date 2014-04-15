package net.glowstone.util.nbt;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * NBT Input Stream test.
 */
public class InputTest {

    @Test
    public void stuff() throws IOException {
        File f = new File("target/SpaceManiac.dat");
        NBTInputStream in = new NBTInputStream(new FileInputStream(f));
        Tag t = in.readCompound();

        System.out.println("==");
        System.out.println(t);
        System.out.println("==");
    }

}
