package io.github.strikerrocker.limitedspawners;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class LimitedSpawner implements ModInitializer {
    public static final String MODID = "limitedspawners";
    public static ModConfig config;

    static {
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void onInitialize() {

    }
}