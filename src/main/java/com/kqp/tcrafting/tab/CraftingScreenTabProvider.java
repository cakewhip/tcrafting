package com.kqp.tcrafting.tab;

import com.kqp.inventorytabs.tabs.provider.TabProvider;
import com.kqp.inventorytabs.tabs.tab.Tab;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public class CraftingScreenTabProvider implements TabProvider {
    @Override
    public void addAvailableTabs(ClientPlayerEntity clientPlayerEntity, List<Tab> list) {
        if (list.size() == 1 || !(list.get(1) instanceof CraftingScreenTab)) {
            list.add(1, new CraftingScreenTab());
        }
    }
}
