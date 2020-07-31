package com.kqp.tcrafting.network.screen.crafting;

import com.kqp.tcrafting.network.base.BasePacketC2S;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;

public class SyncScrollPositionC2S extends BasePacketC2S {
    public SyncScrollPositionC2S() {
        super("sync_scroll_position_s2c");
    }

    public void sendToServer(TCraftingScreenHandler.View view, float scrollPos) {
        this.sendToServer(buf -> {
            buf.writeInt(view.ordinal());
            buf.writeFloat(scrollPos);
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        int viewOrdinal = data.readInt();
        float scrollPos = data.readFloat();

        context.getTaskQueue().execute(() -> {
            TCraftingScreenHandler.View view = TCraftingScreenHandler.View.from(viewOrdinal);

            TCraftingScreenHandler screenHandler = (TCraftingScreenHandler) context.getPlayer().currentScreenHandler;

            switch (view) {
                case CRAFTING:
                    screenHandler.craftingSession.scrollCraftingResults(scrollPos);
                    break;
                case LOOK_UP:
                    screenHandler.craftingSession.scrollLookUpResults(scrollPos);
                    break;
            }
        });
    }
}
