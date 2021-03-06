package com.kqp.tcrafting.mixin.accessor;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {
    @Mutable
    @Accessor
    void setScreenHandlerSyncId(int screenHandlerSyncId);
}
