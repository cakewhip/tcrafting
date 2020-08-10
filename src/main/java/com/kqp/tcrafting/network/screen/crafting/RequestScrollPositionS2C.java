package com.kqp.tcrafting.network.screen.crafting;

import com.kqp.tcrafting.client.screen.TCraftingScreen;
import com.kqp.tcrafting.network.base.BasePacketS2C;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class RequestScrollPositionS2C extends BasePacketS2C {
    public RequestScrollPositionS2C() {
        super("request_scroll_position_s2c");
    }

    public void sendToPlayer(ServerPlayerEntity player, TCraftingScreenHandler.View view) {
        this.sendToPlayer(player, buf -> {
            buf.writeInt(view.ordinal());
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        int viewOrdinal = data.readInt();

        context.getTaskQueue().execute(() -> {
            respondToRequest(viewOrdinal);
        });
    }

    @Environment(EnvType.CLIENT)
    private static void respondToRequest(int viewOrdinal) {
        if (MinecraftClient.getInstance().currentScreen instanceof TCraftingScreen) {
            TCraftingScreenHandler.View view = TCraftingScreenHandler.View.from(viewOrdinal);
            float scrollPos = 0F;

            TCraftingScreen screen = (TCraftingScreen) MinecraftClient.getInstance().currentScreen;

            switch (view) {
                case CRAFTING:
                    scrollPos = screen.craftingScrollPosition;
                    break;
                case LOOK_UP:
                    scrollPos = screen.lookUpScrollPosition;
                    break;
            }

            TCraftingNetwork.SYNC_SCROLL_POSITION_C2S.sendToServer(view, scrollPos);
        }
    }
}
