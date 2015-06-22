package net.glowstone.generator.populators;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.io.structure.StructureStorage;
import net.glowstone.io.structure.StructureStore;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class StructurePopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {

        if (world.canGenerateStructures()) {

            final int cx = source.getX();
            final int cz = source.getZ();

            random.setSeed(world.getSeed());
            final long xRand = random.nextLong();
            final long zRand = random.nextLong();

            boolean placed = false;
            for (int x = cx - 8; x <= cx + 8 && !placed; x++) {
                for (int z = cz - 8; z <= cz + 8 && !placed; z++) {
                    if (world.getChunkAt(x, z).isLoaded() || world.getChunkAt(x, z).load(true)) {
                        random.setSeed((long) x * xRand + (long) z * zRand ^ world.getSeed());
                        final Map<Integer, GlowStructure> structures = ((GlowWorld) world).getStructures();
                        final int key = new GlowChunk.Key(x, z).hashCode();
                        if (!structures.containsKey(key)) {
                            for (StructureStore<?> store : StructureStorage.getStructureStores()) {
                                final GlowStructure structure = store.createNewStructure((GlowWorld) world, random, x, z);
                                if (structure.shouldGenerate(random)) {
                                    structure.setDirty(true);
                                    structures.put(key, structure);
                                    GlowServer.logger.info("structure in chunk " + x + ',' + z);
                                    placed = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            final int x = cx << 4;
            final int z = cz << 4;
            final Iterator<Entry<Integer, GlowStructure>> it = ((GlowWorld) world).getStructures().entrySet().iterator();            
            while (it.hasNext()) {
                final GlowStructure structure = it.next().getValue();
                if (structure.getBoundingBox().intersectsWith(x, z, x + 15, z + 15)) {
                    final BlockStateDelegate delegate = new BlockStateDelegate();
                    if (structure.generate(random, x, z, delegate)) { // maybe later trigger a StructureGeneratedEvent event and cancel
                        delegate.updateBlockStates();
                    } else {
                        it.remove();
                    }
                }
            }
        }
    }
}
