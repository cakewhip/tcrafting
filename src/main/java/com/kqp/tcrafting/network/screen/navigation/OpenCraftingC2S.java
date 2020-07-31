package com.kqp.tcrafting.network.screen.navigation;

import com.kqp.tcrafting.network.base.BasePacketC2S;
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

public class OpenCraftingC2S extends BasePacketC2S {
    public OpenCraftingC2S() {
        super("open_crafting_c2s");
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
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
        });
    }
}
