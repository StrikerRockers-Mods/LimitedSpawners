package io.github.strikerrocker.limitedspawner;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class Provider implements ICapabilityProvider {


    final LazyOptional<ISpawner> containerGetter = LazyOptional.of(SpawnerCapability.Impl::new);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == SpawnerCapability.INSTANCE)
            return containerGetter.cast();
        return LazyOptional.empty();
    }
}
