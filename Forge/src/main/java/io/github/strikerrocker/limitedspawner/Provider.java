package io.github.strikerrocker.limitedspawner;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

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
