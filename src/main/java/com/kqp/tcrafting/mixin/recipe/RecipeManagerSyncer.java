package com.kqp.tcrafting.mixin.recipe;

import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.recipe.TRecipeManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used to sync level and recipe data to players.
 */
@Mixin(PlayerManager.class)
public class RecipeManagerSyncer {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo callbackInfo) {
        TRecipeManager tRecipeManager = TRecipeManager.getFor(player.world);
        TCraftingNetwork.PREP_RECIPE_MANAGER_FOR_LOADING_S2C.sendToPlayer(player, tRecipeManager);
    }
}
