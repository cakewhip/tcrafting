package com.kqp.tcrafting.init;

import com.kqp.tcrafting.client.screen.TCraftingScreen;
import com.kqp.tcrafting.network.init.TCraftingClientNetwork;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

import java.util.Random;

public class TCraftingClient implements ClientModInitializer {
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        TCraftingClientNetwork.init();

        ScreenRegistry.<TCraftingScreenHandler, TCraftingScreen>register(
                TCrafting.TCRAFTING_SCREEN_HANDLER,
                (screenHandler, inv, text) -> new TCraftingScreen(screenHandler, inv)
        );
    }

    /**
     * Begins the process of opening the crafting menu from the inventory screen.
     * It is a lot more complicated than initially thought due to how screen handlers and screens need to be closed.
     */
    public static void triggerOpenCraftingMenu() {
        TCraftingNetwork.OPEN_CRAFTING_C2S.sendEmptyToServer();
    }
}
