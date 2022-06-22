package net.glowstone.chunk;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
import java.util.OptionalInt;

@Data
@AllArgsConstructor
public class WorldGenBiome {

    private String precipitation;

    private float depth;

    private float temperature;

    private float scale;

    private float downfall;

    private String category;

    private Optional<String> temperatureModifier;

    private int skyColor;

    private int waterFogColor;

    private int fogColor;

    private int waterColor;

    private OptionalInt foliageColor;

    private OptionalInt grassColor;

    private Optional<String> grassColorModifier;

    private Optional<String> music;

    private Optional<String> ambientSound;

    private Optional<String> additionsSound;

    private Optional<String> moodSound;

    private float particleProbability;

    private String particleOptions;

}
