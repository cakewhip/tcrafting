package com.kqp.tcrafting.api;

import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.init.TCraftingConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TRecipeInterfaceRegistry {
    private static final Map<Identifier, TRecipeInterface> RECIPE_INTERFACES = new HashMap();

    public static final TRecipeInterface CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE, TRecipeTypeRegistry.CRAFTING_TABLE);
    public static final TRecipeInterface FURNACE = register(Blocks.FURNACE, TRecipeTypeRegistry.FURNACE);
    public static final TRecipeInterface ANVIL = register(Blocks.ANVIL, TRecipeTypeRegistry.ANVIL);
    public static final TRecipeInterface BLAST_FURNACE = register(Blocks.BLAST_FURNACE, TRecipeTypeRegistry.BLAST_FURNACE);
    public static final TRecipeInterface SMITHING_TABLE = register(Blocks.SMITHING_TABLE, TRecipeTypeRegistry.SMITHING_TABLE);
    public static final TRecipeInterface SMOKER = register(Blocks.SMOKER, TRecipeTypeRegistry.SMOKER);
    public static final TRecipeInterface CARTOGRAPHY_TABLE = register(Blocks.CARTOGRAPHY_TABLE, TRecipeTypeRegistry.CARTOGRAPHY_TABLE);
    public static final TRecipeInterface BREWING_STAND = register(Blocks.BREWING_STAND, TRecipeTypeRegistry.BREWING_STAND);
    public static final TRecipeInterface FLETCHING_TABLE = register(Blocks.FLETCHING_TABLE, TRecipeTypeRegistry.FLETCHING_TABLE);
    public static final TRecipeInterface CAULDRON = register(Blocks.CAULDRON, TRecipeTypeRegistry.CAULDRON);
    public static final TRecipeInterface STONECUTTER = register(Blocks.STONECUTTER, TRecipeTypeRegistry.STONECUTTER);
    public static final TRecipeInterface LOOM = register(Blocks.LOOM, TRecipeTypeRegistry.LOOM);
    public static final TRecipeInterface GRINDSTONE = register(Blocks.GRINDSTONE, TRecipeTypeRegistry.GRINDSTONE);

    public static void init() {
    }

    public static TRecipeInterface register(Block block, Identifier... types) {
        Identifier blockId = Registry.BLOCK.getId(block);

        return register(blockId, new TRecipeInterface(types));
    }

    public static TRecipeInterface register(Identifier id, TRecipeInterface TRecipeInterface) {
        RECIPE_INTERFACES.put(id, TRecipeInterface);

        return TRecipeInterface;
    }
    
    public static TRecipeInterface get(Identifier id) {
        if (RECIPE_INTERFACES.containsKey(id)) {
            return RECIPE_INTERFACES.get(id);
        }

        TCraftingConfig config = TCrafting.getConfig();
        Map<Identifier, TRecipeInterface> configInterfs = new HashMap();

        config.recipeInterfaces.forEach((interfId, types) -> {
            TRecipeInterface interf = new TRecipeInterface();

            for (String type : types) {
                interf.add(new Identifier(type));
            }

            configInterfs.put(new Identifier(interfId), interf);
        });

        return configInterfs.get(id);
    }
}
