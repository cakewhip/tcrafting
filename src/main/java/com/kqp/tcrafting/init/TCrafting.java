package com.kqp.tcrafting.init;

import com.kqp.tcrafting.api.TRecipeInterfaceRegistry;
import com.kqp.tcrafting.api.TRecipeTypeRegistry;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TCrafting implements ModInitializer {
    public static final String MOD_NAME = "TCrafting";
    public static Logger LOGGER = LogManager.getLogger();

    public static final ExtendedScreenHandlerType TCRAFTING_SCREEN_HANDLER = (ExtendedScreenHandlerType) ScreenHandlerRegistry.registerExtended(
            id("tcrafting_screen_handler"),
            (syncId, inv, buf) -> new TCraftingScreenHandler(syncId, inv, ScreenHandlerContext.create(inv.player.world, buf.readBlockPos()))
    );

    @Override
    public void onInitialize() {
        TRecipeTypeRegistry.init();
        TRecipeInterfaceRegistry.init();

        TCraftingNetwork.init();
    }

    public static Identifier id(String path) {
        return new Identifier("tcrafting", path);
    }

    public static void info(String message) {
        LOGGER.log(Level.INFO, "[" + MOD_NAME + "] " + message);
    }

    public static void warn(String message) {
        LOGGER.log(Level.WARN, "[" + MOD_NAME + "] " + message);
    }

    public static void error(String message) {
        LOGGER.log(Level.ERROR, "[" + MOD_NAME + "] " + message);
    }
}
