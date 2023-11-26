package io.github.strikerrocker.limitedspawners;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import static io.github.strikerrocker.limitedspawners.Constants.MODID;

@Mod(MODID)
public class LimitedSpawner {

    private static final ResourceLocation CAP = new ResourceLocation(MODID, "spawner");
    public static ModConfigSpec.ConfigValue<Integer> LIMIT;
    public static ModConfigSpec COMMON_CONFIG;
    public static Capability<LimitedSpawnerData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    static {
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.push(MODID);
        LIMIT = COMMON_BUILDER.comment("Limit of mobs a spawner can spawn in total").define("limit", 100);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public LimitedSpawner() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(LimitedSpawnerData.class);
    }

    @Mod.EventBusSubscriber()
    public static class Event {

        @SubscribeEvent
        public static void onAttachCapEntity(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof MinecartSpawner) event.addCapability(CAP, new Provider());
        }

        @SubscribeEvent
        public static void onAttachCapTE(AttachCapabilitiesEvent<BlockEntity> event) {
            if (event.getObject() instanceof SpawnerBlockEntity) {
                event.addCapability(CAP, new Provider());
            }
        }

        @SubscribeEvent
        public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
            if (!event.getLevel().isClientSide() && event.getSpawnType() == MobSpawnType.SPAWNER) {
                BaseSpawner spawner = event.getSpawner();
                if (spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity) {
                    spawnerBlockEntity.getCapability(INSTANCE).ifPresent(cap -> {
                        if (cap.getSpawned() >= LIMIT.get()) {
                            event.setSpawnCancelled(true);
                        } else {
                            cap.increaseSpawned();
                        }
                    });
                } else if (spawner.getSpawnerEntity() instanceof MinecartSpawner minecartSpawner) {
                    minecartSpawner.getCapability(INSTANCE).ifPresent(cap -> {
                        if (cap.getSpawned() >= LIMIT.get()) {
                            event.setSpawnCancelled(true);
                        } else {
                            cap.increaseSpawned();
                        }
                    });
                }
            }
        }
    }
}