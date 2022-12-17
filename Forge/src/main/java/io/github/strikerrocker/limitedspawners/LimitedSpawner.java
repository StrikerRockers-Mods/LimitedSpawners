package io.github.strikerrocker.limitedspawners;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.strikerrocker.limitedspawners.Constants.MODID;
import static net.minecraftforge.eventbus.api.Event.Result.DENY;

@Mod(MODID)
public class LimitedSpawner {

    private static final ResourceLocation CAP = new ResourceLocation(MODID, "spawner");
    public static ForgeConfigSpec.ConfigValue<Integer> LIMIT;
    public static ForgeConfigSpec COMMON_CONFIG;
    public static Capability<LimitedSpawnerData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
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
        public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (!event.getLevel().isClientSide() && event.getSpawnReason() == MobSpawnType.SPAWNER) {
                BaseSpawner spawner = event.getSpawner();
                if (spawner.getSpawnerBlockEntity() instanceof SpawnerBlockEntity spawnerBlockEntity) {
                    spawnerBlockEntity.getCapability(INSTANCE).ifPresent(cap -> {
                        if (cap.getSpawned() >= LIMIT.get()) {
                            event.setResult(DENY);
                        } else {
                            cap.increaseSpawned();
                        }
                    });
                } else if (spawner.getSpawnerEntity() instanceof MinecartSpawner minecartSpawner) {
                    minecartSpawner.getCapability(INSTANCE).ifPresent(cap -> {
                        if (cap.getSpawned() >= LIMIT.get()) {
                            event.setResult(DENY);
                        } else {
                            cap.increaseSpawned();
                        }
                    });
                }
            }
        }
    }
}