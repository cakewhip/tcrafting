package com.kqp.tcrafting.init;

import com.kqp.tcrafting.client.screen.TCraftingScreen;
import com.kqp.tcrafting.network.init.TCraftingClientNetwork;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class TCraftingClient implements ClientModInitializer {
    private static final Random RANDOM = new Random();

    public static final KeyBinding OPEN_CRAFTING_SCREEN_KEY_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "tcrafting.key.open_crafting_screen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.inventory"
    ));

    @Override
    public void onInitializeClient() {
        TCraftingClientNetwork.init();

        ScreenRegistry.<TCraftingScreenHandler, TCraftingScreen>register(
                TCrafting.TCRAFTING_SCREEN_HANDLER,
                (screenHandler, inv, text) -> new TCraftingScreen(screenHandler, inv)
        );

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (client.world != null && client.player != null) {
                if (client.overlay == null && (client.currentScreen == null || client.currentScreen.passEvents)) {
                    if (OPEN_CRAFTING_SCREEN_KEY_BIND.wasPressed()) {
                        triggerOpenCraftingMenu(false);
                    }
                }
            }
        });
    }

    /**
     * Begins the process of opening the crafting menu from the inventory screen.
     */
    public static void triggerOpenCraftingMenu(boolean moveMouse) {
        TCraftingNetwork.OPEN_CRAFTING_C2S.send(moveMouse);
    }
}
