package io.github.strikerrocker.limitedspawners.mixin;

import io.github.strikerrocker.limitedspawners.LimitedSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseSpawner.class)
public class MixinBaseSpawner {
    public int spawnedCount = 0;

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
    public void updateCount(CallbackInfo ci) {
        spawnedCount++;
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/EntityType;by(Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Optional;"), cancellable = true)
    public void controlSpawn(CallbackInfo ci) {
        if (spawnedCount >= LimitedSpawner.config.limit) {
            ci.cancel();
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/nbt/CompoundTag;getShort(Ljava/lang/String;)S", ordinal = 0))
    public void readNbt(Level world, BlockPos pos, CompoundTag nbt, CallbackInfo ci) {
        this.spawnedCount = nbt.getInt("spawnerCount");
    }

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putShort(Ljava/lang/String;S)V", ordinal = 1))
    public void writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        nbt.putInt("spawnerCount", spawnedCount);
    }
}