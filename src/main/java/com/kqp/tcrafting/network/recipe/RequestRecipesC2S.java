package com.kqp.tcrafting.network.recipe;

import com.kqp.tcrafting.network.base.BasePacketC2S;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class RequestRecipesC2S extends BasePacketC2S {
    private static final int PACKET_RECIPE_LIMIT = 250;

    public RequestRecipesC2S() {
        super("request_recipes_c2s");
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        context.getTaskQueue().execute(() -> {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            TRecipeManager recipeManager = TRecipeManager.getFor(player.world);
            Map<Identifier, TRecipe> recipes = recipeManager.getRecipes();

            Map<Identifier, TRecipe> recipesToSend = new HashMap();
            int sent = 0;

            for (Map.Entry<Identifier, TRecipe> pair : recipes.entrySet()) {
                recipesToSend.put(pair.getKey(), pair.getValue());
                sent++;

                if (recipesToSend.size() >= PACKET_RECIPE_LIMIT) {
                    TCraftingNetwork.SEND_RECIPES_S2C.sendToPlayer(player, recipesToSend, sent == recipes.size());
                    recipesToSend = new HashMap();
                }
            }

            if (!recipesToSend.isEmpty()) {
                TCraftingNetwork.SEND_RECIPES_S2C.sendToPlayer(player, recipesToSend, true);
            }
        });
    }
}