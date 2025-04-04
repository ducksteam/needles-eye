package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;

/**
 * Represents the seed for a playthrough
 * @author skysourced
 */
public class Seed {
    private long seed;
    private String seedString;

    private enum SeedType {
        LONG,
        STRING,
        TIME
    }

    private SeedType type;

    public Seed(long seed) {
        this.seed = seed;
        this.type = SeedType.LONG;
        Gdx.app.debug("Seed", this.toString());
    }

    public Seed(String seed) {
        if (seed != null) {
            try{
                this.seed = Long.parseLong(seed);
                this.type = SeedType.LONG;
            } catch (NumberFormatException e) {
                this.seed = seed.hashCode();
                this.type = SeedType.STRING;
                this.seedString = seed;
            }
        }
        Gdx.app.debug("Seed", this.toString());
    }

    public Seed() {
        this.seed = System.nanoTime();
        this.type = SeedType.TIME;
        Gdx.app.debug("Seed", this.toString());
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public String toString() {
        return type.toString() + " " + seed + " " + (seedString != null ? ("(" + seedString +")") : "");
    }
}
