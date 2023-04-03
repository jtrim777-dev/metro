package dev.jtrim777.metro.mixin;

import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin {
    @Inject(at = @At("RETURN"), method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void overridePlacement(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        boolean base = info.getReturnValue();
        BlockState upState = world.getBlockState(pos.up());
        boolean ncond = isStakeType(upState);
        info.setReturnValue(base || ncond);
    }

    @Redirect(
            method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
    )
    private void interceptOnLanded(BlockState state, World world, BlockPos pos) {
        if (!isStakeType(world.getBlockState(pos.up()))) {
            FarmlandBlock.setToDirt(state, world, pos);
        }
    }

    private static boolean isStakeType(BlockState state) {
        Block block = state.getBlock();
        RegistryEntry.Reference<Block> blockID = block.getRegistryEntry();

        return blockID.matchesId(StakeID) ||
                StakePlantClass.isAssignableFrom(block.getClass());
    }

    private static Class<?> StakePlantClass;
    private static Identifier StakeID = new Identifier("metro", "stake");

    static {
        try {
            StakePlantClass = Class.forName("dev.jtrim777.metro.block.StakedPlant");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
