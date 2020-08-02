package com.kqp.tcrafting.init;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

import java.util.HashMap;
import java.util.Map;

@Config(name = "tcrafting")
public class TCraftingConfig implements ConfigData {
    @Comment("Here you can define your own recipe interfaces.\n" +
             "The format is a key (ie mod_id:block_name) followed by\n" +
             "an array of strings representing recipe types.\n" +
             "See the TRecipeTypeRegistry class in Git repo for all pre-defined recipe types.\n" +
             "Example config making cobblestone expose the crafting_table recipe type:\n" +
             "  \"recipeInterfaces\": {\n" +
             "    \"minecraft:cobblestone\": [\n" +
             "      \"minecraft:crafting_table\"\n" +
             "    ]\n" +
             "  }"
    )
    public Map<String, String[]> recipeInterfaces = new HashMap();
}
