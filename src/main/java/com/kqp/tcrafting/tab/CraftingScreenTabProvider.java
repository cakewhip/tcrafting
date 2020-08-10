package com.kqp.tcrafting.tab;

import com.kqp.inventorytabs.tabs.provider.TabProvider;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class CraftingScreenTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs) {
        for (int i = 0; i < tabs.size(); ++i) {
            Tab tab = tabs.get(i);

            if (tab instanceof CraftingScreenTab) {
                return;
            }
        }

        tabs.add(new CraftingScreenTab());
    }
}
