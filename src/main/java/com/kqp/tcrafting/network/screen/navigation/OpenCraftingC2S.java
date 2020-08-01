package com.kqp.tcrafting.network.screen.navigation;

import com.kqp.tcrafting.network.base.BasePacketC2S;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import com.kqp.tcrafting.util.MouseUtil;
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

public class OpenCraftingC2S extends BasePacketC2S {
    public OpenCraftingC2S() {
        super("open_crafting_c2s");
    }

    public void send(boolean moveMouse) {
        super.sendToServer(buf -> {
            buf.writeBoolean(moveMouse);

            if (moveMouse) {
                buf.writeDouble(MouseUtil.getMouseX());
                buf.writeDouble(MouseUtil.getMouseY());
            }
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        boolean moveMouse = data.readBoolean();
        double mouseX = moveMouse ? data.readDouble() : 0D;
        double mouseY = moveMouse ? data.readDouble() : 0D;

        context.getTaskQueue().execute(() -> {
            ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeBlockPos(player.getBlockPos());
                }

                @Override
                public Text getDisplayName() {
                    // TODO: figure out what this is lmao
                    return new LiteralText("BEANS");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new TCraftingScreenHandler(syncId, inv, ScreenHandlerContext.create(player.world, player.getBlockPos()));
                }
            });

            if (moveMouse) {
                TCraftingNetwork.MOVE_MOUSE_S2C.sendToPlayer(player, mouseX, mouseY);
            }
        });
    }
}
