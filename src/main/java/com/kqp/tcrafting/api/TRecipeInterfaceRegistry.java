package com.kqp.tcrafting.api;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

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
        return RECIPE_INTERFACES.get(id);
    }
}
