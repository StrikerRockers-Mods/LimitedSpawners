package io.github.strikerrocker.limitedspawner;

public class LimitedSpawnerData {
    int spawned = 0;

    public int getSpawned() {
        return spawned;
    }

    public void setSpawned(int spawned) {
        this.spawned = spawned;
    }

    public void increaseSpawned() {
        spawned++;
    }
}
