package com.kqp.tcrafting.network.screen.crafting;

import com.kqp.tcrafting.network.base.BasePacketC2S;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class RequestCraftingSessionSyncC2S extends BasePacketC2S {
    public RequestCraftingSessionSyncC2S() {
        super("request_crafting_session_sync_s2c");
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        context.getTaskQueue().execute(() -> {
            TCraftingScreenHandler screenHandler = (TCraftingScreenHandler) context.getPlayer().currentScreenHandler;

            screenHandler.craftingSession.syncToPlayer();
        });
    }
}
