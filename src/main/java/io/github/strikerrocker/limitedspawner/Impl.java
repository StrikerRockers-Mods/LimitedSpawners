package io.github.strikerrocker.limitedspawner;

public class Impl implements ISpawner {
    int spawned = 0;

    @Override
    public int getSpawned() {
        return spawned;
    }

    @Override
    public void setSpawned(int spawned) {
        this.spawned = spawned;
    }

    @Override
    public void increaseSpawned() {
        spawned++;
    }
}
