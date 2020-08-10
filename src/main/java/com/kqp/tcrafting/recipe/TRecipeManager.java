package com.kqp.tcrafting.recipe;

import com.google.gson.*;
import com.kqp.tcrafting.api.DynamicRecipeRegistry;
import com.kqp.tcrafting.api.TRecipeTypeRegistry;
import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.mixin.accessor.MinecraftServerResourceAccessor;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.recipe.data.ComparableItemStack;
import com.kqp.tcrafting.recipe.data.Reagent;
import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.recipe.interf.MatchingStackProvider;
import com.kqp.tcrafting.recipe.interf.TRecipeManagerContainer;
import com.kqp.tcrafting.util.TimeUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all of the TCrafting recipes.
 */
public class TRecipeManager extends JsonDataLoader {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Map of recipe types ({@link RecipeType}) to a list of recipes.
     */
    private Map<Identifier, TRecipe> recipes = new HashMap();

    private final Optional<RecipeManager> vanillaRecipeManager;

    private final Map<Set<ComparableItemStack>, String> knownTags = new HashMap();

    @Environment(EnvType.CLIENT)
    private int expectedSize;

    /**
     * Creates the recipe manager.
     * If server, pass the vanilla recipe manager.
     * If client, pass null.
     *
     * @param vanillaRecipeManager
     */
    public TRecipeManager(RecipeManager vanillaRecipeManager) {
        super(GSON, "tcrafting_recipes");

        this.vanillaRecipeManager = Optional.ofNullable(vanillaRecipeManager);
    }

    public void addRecipe(Identifier identifier, Identifier recipeType, ItemStack output, HashMap<Reagent, Integer> reagents) {
        TRecipe recipe = new TRecipe(recipeType, output, reagents);
        recipes.put(identifier, recipe);
    }

    public void addRecipe(Identifier identifier, TRecipe recipe) {
        recipes.put(identifier, recipe);
    }

    /**
     * Returns a list of recipes for a given recipe type.
     *
     * @param recipeType RecipeType
     * @return List of corresponding recipes
     */
    private List<TRecipe> getRecipesForType(String recipeType) {
        ArrayList<TRecipe> recipeList = new ArrayList();

        for (TRecipe recipe : recipes.values()) {
            if (recipe.recipeType.equals(recipeType)) {
                recipeList.add(recipe);
            }
        }

        return recipeList;
    }

    /**
     * Returns a list of recipes that a passed list of item stacks can craft.
     *
     * @param availableRecipeTypes Recipe types to access
     * @param itemStacks           Input item stacks
     * @return List of possible recipes
     */
    public Set<TRecipe> getMatches(PlayerEntity player, Set<Identifier> availableRecipeTypes, List<ItemStack> itemStacks) {
        HashMap<ComparableItemStack, Integer> input = TRecipeManager.toComparableMap(itemStacks);
        Set<TRecipe> output = new HashSet();

        DynamicRecipeRegistry.getDynamicRecipes().values().forEach(dynamicRecipe ->
                output.addAll(dynamicRecipe.getPossibleRecipes(player, availableRecipeTypes, input)
                        .stream()
                        .filter(recipe -> availableRecipeTypes.contains(recipe.recipeType))
                        .filter(recipe -> recipe.matches(input))
                        .collect(Collectors.toList()))
        );

        output.addAll(recipes.values().parallelStream()
                .filter(recipe -> availableRecipeTypes.contains(recipe.recipeType))
                .filter(recipe -> recipe.matches(input))
                .collect(Collectors.toList())
        );

        return output;
    }

    /**
     * Returns a list of recipes that have the passed item stack as an input.
     *
     * @param itemStack Item stack input
     * @return List of recipes that have the passed item stack as an input
     */
    public List<TRecipe> getRecipesUsingItemStack(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Collections.emptyList();
        }

        ComparableItemStack comparableItemStack = new ComparableItemStack(itemStack);
        ArrayList<TRecipe> output = new ArrayList();

        output.addAll(recipes.values().parallelStream()
                .filter(recipe -> {
                    for (Reagent reagent : recipe.reagents.keySet()) {
                        if (reagent.matchingStacks.contains(comparableItemStack)) {
                            return true;
                        }
                    }

                    return false;
                })
                .collect(Collectors.toList())
        );

        // Sort for that hot UX
        output.sort(Comparator.comparing(TRecipe::getSortString));

        return output;
    }

    public Map<Identifier, TRecipe> getRecipes() {
        return this.recipes;
    }

    /**
     * Converts the passed list of item stacks to a map of comparable item stack objects to their counts.
     *
     * @param input List of item stacks to convert
     * @return Map of comparable item stacks to their counts
     */
    public static HashMap<ComparableItemStack, Integer> toComparableMap(List<ItemStack> input) {
        HashMap<ComparableItemStack, Integer> ret = new HashMap();

        input.stream().forEach(itemStack -> {
            ComparableItemStack key = new ComparableItemStack(itemStack);

            if (ret.containsKey(key)) {
                ret.replace(key, ret.get(key) + itemStack.getCount());
            } else {
                ret.put(key, itemStack.getCount());
            }
        });

        return ret;
    }

    /**
     * Deserializes a map of JSON objects into TCrafting recipe objects.
     *
     * @param loader
     * @param manager
     * @param profiler
     */
    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        this.recipes.clear();

        TCrafting.info("Loading TCrafting recipes");

        TimeUtil.profile(
                () -> {
                    loader.forEach((id, element) -> {
                        JsonObject json = element.getAsJsonObject();

                        Identifier type = new Identifier(json.get("type").getAsString());
                        JsonObject recipeNode = json.getAsJsonObject("recipe");

                        JsonObject outputNode = recipeNode.getAsJsonObject("output");
                        ItemStack output = new ItemStack(
                                Registry.ITEM.get(new Identifier(outputNode.get("item").getAsString())),
                                outputNode.get("count") != null ? outputNode.get("count").getAsInt() : 1
                        );

                        JsonArray reagentsNode = recipeNode.getAsJsonArray("reagents");
                        HashMap<Reagent, Integer> reagents = new HashMap();

                        reagentsNode.forEach(jsonElement -> {
                            JsonObject reagentNode = jsonElement.getAsJsonObject();
                            ArrayList<ItemStack> matchingStacks = new ArrayList();

                            if (reagentNode.get("item") != null) {
                                String itemName = reagentNode.get("item").getAsString();
                                Item item = Registry.ITEM.get(new Identifier(itemName));

                                if (item == Items.AIR) {
                                    throw new IllegalStateException("Couldn't find item " + itemName + " while loading recipe " + id);
                                }

                                matchingStacks.add(new ItemStack(item));
                            } else {
                                JsonArray matchingArray = reagentNode.get("matching").getAsJsonArray();
                                matchingArray.forEach(matchingNode -> {
                                    JsonObject matchingStackNode = matchingNode.getAsJsonObject();
                                    Item item = Registry.ITEM.get(new Identifier(matchingStackNode.get("item").getAsString()));

                                    matchingStacks.add(new ItemStack(item));
                                });
                            }

                            JsonElement countNode = reagentNode.get("count");
                            int count = countNode != null ? countNode.getAsInt() : 1;

                            Reagent reagent = new Reagent(matchingStacks);
                            reagents.put(reagent, count);
                        });

                        this.addRecipe(
                                id,
                                new TRecipe(type, output, reagents)
                        );
                    });
                },
                time -> TCrafting.info("Loading of TCrafting recipes took " + time + "ms")
        );

        vanillaRecipeManager.ifPresent(this::addVanillaRecipes);
    }

    /**
     * Converts vanilla recipes to TCrafting recipes.
     *
     * @param recipeManager The vanilla recipe manager
     */
    public void addVanillaRecipes(RecipeManager recipeManager) {
        List<Recipe> recipes = recipeManager.keys()
                .map(recipeManager::get)
                .filter(optionalRecipe -> optionalRecipe.isPresent())
                .map(optionalRecipe -> optionalRecipe.get())
                .collect(Collectors.toList());

        for (Recipe recipe : recipes) {
            if (recipe instanceof ShapedRecipe
                    || recipe instanceof ShapelessRecipe
                    || recipe instanceof SmeltingRecipe
                    || recipe instanceof StonecuttingRecipe
                    || recipe instanceof BlastingRecipe
                    || recipe instanceof SmokingRecipe
                    || recipe instanceof CampfireCookingRecipe) {
                List<Ingredient> ingredientList = recipe.getPreviewInputs();
                HashMap<Reagent, Integer> reagents = new HashMap();

                for (Ingredient ingredient : ingredientList) {
                    if (!ingredient.isEmpty()) {
                        Reagent reagent = new Reagent(Arrays.asList(((MatchingStackProvider) (Object) ingredient).getMatchingStacks()));
                        int currentCount = reagents.getOrDefault(reagent, 0);

                        reagents.put(reagent, currentCount + 1);
                    }
                }

                if (reagents.isEmpty()) {
                    TCrafting.warn("Recipe for " + recipe.getOutput() + " has no reagents, ignoring");
                } else if (recipe.getOutput() == null) {
                    TCrafting.warn("Output not found for vanilla recipe, ignoring");
                } else {
                    Identifier recipeType;

                    if (recipe instanceof ShapedRecipe) {
                        if (((ShapedRecipe) recipe).getWidth() <= 2 && ((ShapedRecipe) recipe).getHeight() <= 2) {
                            recipeType = TRecipeTypeRegistry.TWO_BY_TWO;
                        } else {
                            recipeType = TRecipeTypeRegistry.CRAFTING_TABLE;
                        }
                    } else if (recipe instanceof ShapelessRecipe) {
                        if (reagents.keySet().size() <= 4) {
                            recipeType = TRecipeTypeRegistry.TWO_BY_TWO;
                        } else {
                            recipeType = TRecipeTypeRegistry.CRAFTING_TABLE;
                        }
                    } else if (recipe instanceof SmeltingRecipe) {
                        recipeType = TRecipeTypeRegistry.SMELTING;
                    } else if (recipe instanceof StonecuttingRecipe) {
                        recipeType = TRecipeTypeRegistry.STONECUTTER;
                    } else if (recipe instanceof BlastingRecipe) {
                        recipeType = TRecipeTypeRegistry.BLASTING;
                    } else if (recipe instanceof SmokingRecipe) {
                        recipeType = TRecipeTypeRegistry.SMOKING;
                    } else if (recipe instanceof CampfireCookingRecipe) {
                        recipeType = TRecipeTypeRegistry.CAMPFIRE_COOKING;
                    } else {
                        throw new IllegalStateException("Couldn't determine TCrafting recipe type for vanilla recipe: " + recipe);
                    }

                    this.addRecipe(
                            recipe.getId(),
                            recipeType,
                            recipe.getOutput(),
                            reagents
                    );
                }
            }
        }
    }

    public void clearKnownTags() {
        this.knownTags.clear();
    }

    public void loadTag(Identifier id, Tag<Item> tag) {
        knownTags.put(tag.values()
                        .stream()
                        .map(ItemStack::new)
                        .map(ComparableItemStack::new)
                        .collect(Collectors.toSet()),
                id.getNamespace() + ".tag." + id.getPath() + ".tooltip"
        );
    }

    public void identifyKnownTags() {
        this.recipes.values().stream()
                .map(TRecipe::getReagents)
                .flatMap(map -> map.keySet().stream())
                .forEach(this::checkIfKnownReagent);
    }

    private void checkIfKnownReagent(Reagent reagent) {
        if (knownTags.containsKey(reagent.matchingStacks)) {
            reagent.setCustomTooltipKey(knownTags.get(reagent.matchingStacks));
        }
    }

    public void prepareForLoading(int expectedSize) {
        this.expectedSize = expectedSize;
        this.recipes.clear();

        TCrafting.info("Preparing to load recipes from server");
        TCrafting.info("Expecting " + expectedSize + " recipes");

        TCraftingNetwork.REQUEST_RECIPES_C2S.sendEmptyToServer();
    }

    @Environment(EnvType.CLIENT)
    public void addRecipes(Map<Identifier, TRecipe> recipes, boolean done) {
        this.recipes.putAll(recipes);

        if (this.recipes.size() == expectedSize) {
            TCrafting.info("Finished loading " + this.recipes.size() + " recipes from server");
        } else if (this.recipes.size() > expectedSize) {
            throw new RuntimeException("Received too many recipes! Something's wrong, I can feel it");
        } else if (this.recipes.size() < expectedSize && done) {
            throw new RuntimeException("Didn't receive enough recipes! Prepare for unforeseen consequences");
        }
    }

    public static TRecipeManager getFor(World world) {
        if (!world.isClient) {
            return ((TRecipeManagerContainer) ((MinecraftServerResourceAccessor) world.getServer()).getServerResourceManager()).getTCraftingRecipeManager();
        } else {
            return ((TRecipeManagerContainer) MinecraftClient.getInstance().getNetworkHandler()).getTCraftingRecipeManager();
        }
    }
}
