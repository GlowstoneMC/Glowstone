package net.glowstone.block;

import org.bukkit.Material;

/**
 * MaterialValueManager provides easily access to {@link Material} related values (e.g. block hardness).
 */
public interface MaterialValueManager {
    enum GlowMaterial {
        DEFAULT(new GlowMaterialBuilder()),
        AIR(new GlowMaterialBuilder()
                .setLightOpacity(0)),
        STONE(new GlowMaterialBuilder()
                .setBlastResistance(30)),
        GRASS(new GlowMaterialBuilder()
                .setBlastResistance(3)),
        DIRT(new GlowMaterialBuilder()
                .setBlastResistance(2.5f)),
        COBBLESTONE(new GlowMaterialBuilder()
                .setBlastResistance(30)),
        WOOD(new GlowMaterialBuilder()
                .setFlameResistance(5)
                .setFireResistance(20)),
        SAPLING(new GlowMaterialBuilder()
                .setHardness(0)
                .setLightOpacity(0))
        ;


        private final float hardness;
        private final float blastResistance;
        private final int lightOpacity;
        private final int flameResistance;
        private final int fireResistance;
        private final boolean randomTicks;

        private static class GlowMaterialBuilder {
            private float hardness = 1;
            private float blastResistance;
            private int lightOpacity = 255;
            private int flameResistance = -1;
            private int fireResistance = -1;
            private boolean randomTicks;

            GlowMaterialBuilder() {
            }

            float getHardness() {
                return hardness;
            }

            GlowMaterialBuilder setHardness(float hardness) {
                this.hardness = hardness;
                return this;
            }

            float getBlastResistance() {
                return blastResistance;
            }

            GlowMaterialBuilder setBlastResistance(float blastResistance) {
                this.blastResistance = blastResistance;
                return this;
            }

            int getLightOpacity() {
                return lightOpacity;
            }

            GlowMaterialBuilder setLightOpacity(int lightOpacity) {
                this.lightOpacity = lightOpacity;
                return this;
            }

            int getFlameResistance() {
                return flameResistance;
            }

            GlowMaterialBuilder setFlameResistance(int flameResistance) {
                this.flameResistance = flameResistance;
                return this;
            }

            int getFireResistance() {
                return fireResistance;
            }

            GlowMaterialBuilder setFireResistance(int fireResistance) {
                this.fireResistance = fireResistance;
                return this;
            }

            boolean doRandomTicks() {
                return randomTicks;
            }

            public GlowMaterialBuilder setRandomTicks(boolean randomTicks) {
                this.randomTicks = randomTicks;
                return this;
            }
        }

        GlowMaterial(GlowMaterialBuilder builder) {
            this.hardness = builder.getHardness();
            this.blastResistance = builder.getBlastResistance();
            this.lightOpacity = builder.getLightOpacity();
            this.flameResistance = builder.getFlameResistance();
            this.fireResistance = builder.getFireResistance();
            this.randomTicks = builder.doRandomTicks();
        }

        public float getHardness() {
            return hardness;
        }

        public float getBlastResistance() {
            return blastResistance;
        }

        public int getLightOpacity() {
            return lightOpacity;
        }

        public int getFlameResistance() {
            return flameResistance;
        }

        public int getFireResistance() {
            return fireResistance;
        }

        public boolean doRandomTicks() {
            return randomTicks;
        }
    }

    static GlowMaterial getValues(Material material) {
        GlowMaterial vanillaMaterial = GlowMaterial.valueOf(material.name());
        if (vanillaMaterial != null) {
            return vanillaMaterial;
        }
        return GlowMaterial.DEFAULT;
    }
}
