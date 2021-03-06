package com.kqp.tcrafting.network.init;

import com.kqp.tcrafting.network.base.BasePacketS2C;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class TCraftingClientNetwork {
    public static void init() {
        register(TCraftingNetwork.PREP_RECIPE_MANAGER_FOR_LOADING_S2C);
        register(TCraftingNetwork.SEND_RECIPES_S2C);
        register(TCraftingNetwork.SYNC_CRAFTING_SESSION_S2C);
        register(TCraftingNetwork.REQUEST_SCROLL_POSITION_S2C);
        register(TCraftingNetwork.SYNC_RECIPE_SLOT_S2C);
    }

    private static void register(BasePacketS2C packet) {
        ClientSidePacketRegistry.INSTANCE.register(packet.id, packet::accept);
    }
}
