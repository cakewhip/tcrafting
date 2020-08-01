package com.kqp.tcrafting.api;

import net.minecraft.util.Identifier;

import java.util.*;

public class TRecipeTypeRegistry {
    private static final List<Identifier> RECIPE_TYPES = new ArrayList();

    public static final Identifier TWO_BY_TWO = register(new Identifier("two_by_two"));
    public static final Identifier CRAFTING_TABLE = register(new Identifier("crafting_table"));
    public static final Identifier FURNACE = register(new Identifier("furnace"));
    public static final Identifier ANVIL = register(new Identifier("anvil"));
    public static final Identifier BLAST_FURNACE = register(new Identifier("blast_furnace"));
    public static final Identifier SMITHING_TABLE = register(new Identifier("smithing_table"));
    public static final Identifier SMOKER = register(new Identifier("smoker"));
    public static final Identifier CARTOGRAPHY_TABLE = register(new Identifier("cartography_table"));
    public static final Identifier BREWING_STAND = register(new Identifier("brewing_stand"));
    public static final Identifier FLETCHING_TABLE = register(new Identifier("fletching_table"));
    public static final Identifier CAULDRON = register(new Identifier("cauldron"));
    public static final Identifier STONECUTTER = register(new Identifier("stonecutter"));
    public static final Identifier LOOM = register(new Identifier("loom"));
    public static final Identifier GRINDSTONE = register(new Identifier("grindstone"));

    public static final Identifier SMELTING = register(new Identifier("smelting"));
    public static final Identifier BLASTING = register(new Identifier("blasting"));
    public static final Identifier SMOKING = register(new Identifier("smoking"));
    public static final Identifier CAMPFIRE_COOKING = register(new Identifier("campfire_cooking"));

    public static void init() {
    }

    public static Identifier register(Identifier id) {
        RECIPE_TYPES.add(id);

        return id;
    }
}
