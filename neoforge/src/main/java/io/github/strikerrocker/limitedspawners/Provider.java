package io.github.strikerrocker.limitedspawners;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class Provider implements ICapabilitySerializable<CompoundTag> {

    LimitedSpawnerData instance = new LimitedSpawnerData();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return LimitedSpawner.INSTANCE.orEmpty(cap, LazyOptional.of(() -> instance));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("spawned", instance.getSpawned());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.setSpawned(nbt.getInt("spawned"));
    }
}
