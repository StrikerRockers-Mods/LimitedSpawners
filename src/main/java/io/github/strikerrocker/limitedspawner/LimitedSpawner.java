package io.github.strikerrocker.limitedspawner;

import net.minecraft.entity.SpawnReason;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.strikerrocker.limitedspawner.LimitedSpawner.MODID;

@Mod(MODID)
public class LimitedSpawner {


    public static final String MODID = "limitedspawners";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation CAP = new ResourceLocation(MODID, "spawner");
    public static int limit = 3;
    public static ForgeConfigSpec.ConfigValue<Integer> LIMIT;
    public static ForgeConfigSpec SERVER_CONFIG;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        SERVER_BUILDER.comment("SERVER CONFIG").push(MODID);
        LIMIT = SERVER_BUILDER.comment("Limit of mobs a spawner can spawn in total").define("limit", 100);
        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public LimitedSpawner() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {

        SpawnerCapability.register();
    }

    @Mod.EventBusSubscriber()
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
            if (!event.getWorld().isRemote() && event.getSpawnReason() == SpawnReason.SPAWNER) {
                BlockPos spawnerPos = event.getSpawner().getSpawnerPosition();
                World world = event.getEntity().world;
                if (world.getTileEntity(spawnerPos) instanceof MobSpawnerTileEntity) {
                    MobSpawnerTileEntity te = (MobSpawnerTileEntity) world.getTileEntity(spawnerPos);
                    te.getCapability(SpawnerCapability.INSTANCE).ifPresent(cap -> {
                        int spawned = cap.getSpawned();
                        if (spawned >= limit) {
                            event.setResult(Event.Result.DENY);
                        } else {
                            event.setResult(Event.Result.DEFAULT);
                            cap.increaseSpawned();
                        }
                    });
                }
            }
        }

        @SubscribeEvent
        public static void onAttachCapTE(AttachCapabilitiesEvent<TileEntity> event) {
            if (event.getObject() instanceof MobSpawnerTileEntity) {
                event.addCapability(CAP, new Provider());
            }
        }

        /*@SubscribeEvent
        public static void onAttachCapEntity(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof SpawnerMinecartEntity)
                event.addCapability(CAP, new Provider());
        }*/
        //TODO implement for minecart spawner
    }

}