package com.kqp.tcrafting.network.init;

import com.kqp.tcrafting.network.base.BasePacketC2S;
import com.kqp.tcrafting.network.recipe.PrepRecipeManagerForLoadingS2C;
import com.kqp.tcrafting.network.recipe.RequestRecipesC2S;
import com.kqp.tcrafting.network.recipe.SendRecipesS2C;
import com.kqp.tcrafting.network.screen.crafting.*;
import com.kqp.tcrafting.network.screen.navigation.OpenCraftingC2S;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class TCraftingNetwork {
    public static final PrepRecipeManagerForLoadingS2C PREP_RECIPE_MANAGER_FOR_LOADING_S2C = new PrepRecipeManagerForLoadingS2C();
    public static final SendRecipesS2C SEND_RECIPES_S2C = new SendRecipesS2C();

    public static final RequestScrollPositionS2C REQUEST_SCROLL_POSITION_S2C = new RequestScrollPositionS2C();

    public static final SyncCraftingSessionS2C SYNC_CRAFTING_SESSION_S2C = new SyncCraftingSessionS2C();
    public static final SyncRecipeSlotS2C SYNC_RECIPE_SLOT_S2C = new SyncRecipeSlotS2C();

    public static final OpenCraftingC2S OPEN_CRAFTING_C2S = new OpenCraftingC2S();

    public static final SyncScrollPositionC2S SYNC_SCROLL_POSITION_C2S = new SyncScrollPositionC2S();

    public static final RequestCraftingSessionSyncC2S REQUEST_CRAFTING_SESSION_SYNC_C2S = new RequestCraftingSessionSyncC2S();

    public static final RequestRecipesC2S REQUEST_RECIPES_C2S = new RequestRecipesC2S();

    public static void init() {
        register(OPEN_CRAFTING_C2S);
        register(SYNC_SCROLL_POSITION_C2S);
        register(REQUEST_CRAFTING_SESSION_SYNC_C2S);
        register(REQUEST_RECIPES_C2S);
    }

    private static void register(BasePacketC2S packet) {
        ServerSidePacketRegistry.INSTANCE.register(packet.id, packet::accept);
    }
}
