package io.github.strikerrocker.limitedspawner;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class SpawnerCapability {
    @CapabilityInject(ISpawner.class)
    public static Capability<ISpawner> INSTANCE;

    public static void register() {
        CapabilityManager.INSTANCE.register(ISpawner.class, new Storage(), Impl::new);
    }

    private static class Storage implements Capability.IStorage<ISpawner> {
        @Override
        public INBT writeNBT(Capability<ISpawner> capability, ISpawner instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("spawned", instance.getSpawned());
            return nbt;
        }

        @Override
        public void readNBT(Capability<ISpawner> capability, ISpawner instance, Direction side, INBT nbt) {
            CompoundNBT tag = (CompoundNBT) nbt;
            instance.setSpawned(tag.getInt("spawned"));
        }
    }

    public static class Impl implements ISpawner {
        private int spawned = 0;

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
}
