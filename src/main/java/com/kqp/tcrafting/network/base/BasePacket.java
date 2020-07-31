package com.kqp.tcrafting.network.base;

import com.kqp.tcrafting.init.TCrafting;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public abstract class BasePacket {
    public final Identifier id;

    public BasePacket(String name) {
        this.id = TCrafting.id(name);
    }

    public abstract void accept(PacketContext context, PacketByteBuf data);

    public static PacketByteBuf buf() {
        return new PacketByteBuf(Unpooled.buffer());
    }
}
