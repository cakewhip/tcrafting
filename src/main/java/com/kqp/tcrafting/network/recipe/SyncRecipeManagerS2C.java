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

import java.util.HashMap;
import java.util.Map;

public class SyncRecipeManagerS2C extends BasePacketS2C {
    public SyncRecipeManagerS2C() {
        super("sync_recipe_manager_s2c");
    }

    public void sendToPlayer(ServerPlayerEntity player, TRecipeManager tRecipeManager) {
        Map<Identifier, TRecipe> recipes = tRecipeManager.getRecipes();

        this.sendToPlayer(player, buf -> {
            buf.writeInt(recipes.size());

            for (Map.Entry<Identifier, TRecipe> entry : recipes.entrySet()) {
                buf.writeIdentifier(entry.getKey());
                entry.getValue().writeTo(buf);
            }
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        Map<Identifier, TRecipe> recipes = new HashMap();
        int size = data.readInt();

        for (int i = 0; i < size; i++) {
            Identifier id = data.readIdentifier();
            TRecipe recipe = TRecipe.readFrom(data);

            recipes.put(id, recipe);
        }

        context.getTaskQueue().execute(() -> {
            loadRecipesToClientRecipeManager(recipes, context.getPlayer());
        });
    }

    @Environment(EnvType.CLIENT)
    private static void loadRecipesToClientRecipeManager(Map<Identifier, TRecipe> recipes, PlayerEntity player) {
        TRecipeManager tRecipeManager = TRecipeManager.getFor(player.world);

        tRecipeManager.setRecipes(recipes);
    }
}
