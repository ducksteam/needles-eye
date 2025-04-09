package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;

/**
 * Represents the seed for a playthrough
 * @author skysourced
 */
public class Seed {
    private long seed;
    String seedString;

    enum SeedType {
        LONG,
        STRING,
        TIME
    }

    SeedType type;

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
        } else {
            // TODO: Java 23 will mean this can be replaced with `this()`
            this.seed = System.nanoTime();
            this.type = SeedType.TIME;
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
