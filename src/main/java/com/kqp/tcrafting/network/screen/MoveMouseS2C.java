package com.kqp.tcrafting.network.screen;

import com.kqp.tcrafting.network.base.BasePacketS2C;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class MoveMouseS2C extends BasePacketS2C {
    public MoveMouseS2C() {
        super("move_mouse_s2c");
    }

    public void sendToPlayer(ServerPlayerEntity player, double mouseX, double mouseY) {
        this.sendToPlayer(player, buf -> {
            buf.writeDouble(mouseX);
            buf.writeDouble(mouseY);
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        double mouseX = data.readDouble();
        double mouseY = data.readDouble();

        context.getTaskQueue().execute(() -> {
            moveMouse(mouseX, mouseY);
        });
    }

    @Environment(EnvType.CLIENT)
    private static void moveMouse(double mouseX, double mouseY) {
        InputUtil.setCursorParameters(MinecraftClient.getInstance().getWindow().getHandle(), 212993, mouseX, mouseY);
    }
}
