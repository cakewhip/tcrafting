package com.kqp.tcrafting.mixin.client.recipe;

import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.interf.TRecipeManagerContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used to initialize the TCrafting recipe manager on the client side.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class TClientRecipeManagerInitializer implements TRecipeManagerContainer {
    @Shadow
    private MinecraftClient client;

    private TRecipeManager tRecipeManager;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void construct(CallbackInfo callbackInfo) {
        this.tRecipeManager = new TRecipeManager(null);
    }

    @Override
    public TRecipeManager getTCraftingRecipeManager() {
        return tRecipeManager;
    }

    @Override
    public void setTCraftingRecipeManager(TRecipeManager tRecipeManager) {
        this.tRecipeManager = tRecipeManager;
    }
}
