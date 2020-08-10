package com.kqp.tcrafting.network.recipe;

import com.kqp.tcrafting.network.base.BasePacketS2C;
import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.data.TRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class PrepRecipeManagerForLoadingS2C extends BasePacketS2C {
    public PrepRecipeManagerForLoadingS2C() {
        super("sync_recipe_manager_s2c");
    }

    public void sendToPlayer(ServerPlayerEntity player, TRecipeManager tRecipeManager) {
        Map<Identifier, TRecipe> recipes = tRecipeManager.getRecipes();

        this.sendToPlayer(player, buf -> {
            buf.writeInt(recipes.size());
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        int expectedSize = data.readInt();

        context.getTaskQueue().execute(() -> {
            loadRecipesToClientRecipeManager(expectedSize, context.getPlayer());
        });
    }

    @Environment(EnvType.CLIENT)
    private static void loadRecipesToClientRecipeManager(int expectedSize, PlayerEntity player) {
        TRecipeManager tRecipeManager = TRecipeManager.getFor(player.world);
        tRecipeManager.prepareForLoading(expectedSize);
    }
}
