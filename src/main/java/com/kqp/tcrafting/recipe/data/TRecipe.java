package com.kqp.tcrafting.recipe.data;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a TCrafting recipe with an ItemStack result and a map of reagents to their counts.
 */
public class TRecipe {
    public Identifier recipeType;
    public ItemStack result;
    public HashMap<Reagent, Integer> reagents;

    public Lazy<Integer> cachedHash = new Lazy(() -> Objects.hash(recipeType, new ComparableItemStack(result), reagents));

    /**
     * A pre-calculated map of what item stacks match with a given reagent.
     */
    public HashMap<ComparableItemStack, Reagent> itemStackReagentMap;

    private TRecipe(Identifier recipeType) {
        this.recipeType = recipeType;
        this.reagents = new HashMap();
        this.itemStackReagentMap = new HashMap();
    }

    public TRecipe(Identifier recipeType, ItemStack result, HashMap<Reagent, Integer> reagents) {
        this(recipeType);

        this.result = result;
        this.reagents = reagents;

        calcItemStackReagentMap();
    }

    public TRecipe(Identifier recipeType, ItemStack result, ItemStack... inputs) {
        this(recipeType);

        this.result = result;

        for (ItemStack itemStack : inputs) {
            reagents.put(new Reagent(itemStack), itemStack.getCount());
        }

        calcItemStackReagentMap();
    }

    private void calcItemStackReagentMap() {
        reagents.keySet().forEach(reagent -> {
            reagent.matchingStacks.forEach(matchingStack -> {
                itemStackReagentMap.put(matchingStack, reagent);
            });
        });
    }

    /**
     * If this recipe matches a passed map of item stacks and their counts.
     *
     * @param itemStacks Input map of item stacks
     * @return Whether this recipe matches the passed map
     */
    public boolean matches(HashMap<ComparableItemStack, Integer> itemStacks) {
        // Used to keep track of what matching reagents the passed map of item stacks has
        HashMap<Reagent, Integer> reagentMatchMap = new HashMap();

        // If the item stack matches a required reagent, it places it into the match map with a count.
        itemStacks.forEach((itemStack, count) -> {
            if (itemStackReagentMap.containsKey(itemStack)) {
                Reagent matchingReagent = itemStackReagentMap.get(itemStack);

                if (reagentMatchMap.containsKey(matchingReagent)) {
                    reagentMatchMap.replace(matchingReagent, count + reagentMatchMap.get(matchingReagent));
                } else {
                    reagentMatchMap.put(matchingReagent, count);
                }
            }
        });

        // Iterate through the required reagents and what this map has.
        // Returns false if it sees a required reagent is missing or doesn't have enough.
        for (Reagent reagent : reagents.keySet()) {
            if (!reagentMatchMap.containsKey(reagent) || reagentMatchMap.get(reagent) < reagents.get(reagent)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Called whenever this recipe is crafted.
     * Consumes required reagents from the passed screen.
     *
     * @param craftInv Inventory used to craft the recipe.
     */
    public void onCraft(Inventory craftInv) {
        HashMap<Reagent, Integer> reagentMatchMap = new HashMap();

        for (int i = 0; i < craftInv.size(); i++) {
            ItemStack itemStack = craftInv.getStack(i);
            ComparableItemStack comparableItemStack = new ComparableItemStack(itemStack);
            int count = itemStack.getCount();

            if (itemStackReagentMap.containsKey(comparableItemStack)) {
                Reagent matchingReagent = itemStackReagentMap.get(comparableItemStack);
                int already = reagentMatchMap.getOrDefault(matchingReagent, 0);
                int required = reagents.get(matchingReagent);

                if (already < required) {
                    int needed = required - already;

                    if (count > needed) {
                        itemStack.decrement(needed);
                        reagentMatchMap.put(matchingReagent, required);
                    } else if (count <= needed) {
                        reagentMatchMap.put(matchingReagent, already + count);
                        craftInv.setStack(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public void writeTo(PacketByteBuf buf) {
        buf.writeIdentifier(recipeType);
        buf.writeItemStack(result);
        buf.writeInt(reagents.size());

        reagents.forEach(((reagent, count) -> {
            buf.writeInt(reagent.matchingStacks.size());

            reagent.matchingStacks.forEach((comparableItemStack -> buf.writeItemStack(comparableItemStack.itemStack)));
            buf.writeInt(count);

            buf.writeString(reagent.customTooltipKey);
        }));
    }

    public static TRecipe readFrom(PacketByteBuf buf) {
        Identifier recipeType = buf.readIdentifier();
        ItemStack resultStack = buf.readItemStack();
        HashMap<Reagent, Integer> reagents = new HashMap();
        int nReagents = buf.readInt();

        for (int i = 0; i < nReagents; i++) {
            ArrayList<ItemStack> matchingStacks = new ArrayList();
            int nMatchingStacks = buf.readInt();

            for (int j = 0; j < nMatchingStacks; j++) {
                matchingStacks.add(buf.readItemStack());
            }

            int count = buf.readInt();

            String customTooltipKey = buf.readString();

            Reagent reagent = new Reagent(matchingStacks);
            reagent.setCustomTooltipKey(customTooltipKey);

            reagents.put(reagent, count);
        }

        return new TRecipe(recipeType, resultStack, reagents);
    }

    public HashMap<Reagent, Integer> getReagents() {
        return this.reagents;
    }

    public String getSortString() {
        return result.getTranslationKey();
    }

    @Override
    public int hashCode() {
        return cachedHash.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TRecipe tRecipe = (TRecipe) o;
        return recipeType.equals(tRecipe.recipeType) &&
                new ComparableItemStack(result).equals(new ComparableItemStack(tRecipe.result)) &&
                reagents.equals(tRecipe.reagents);
    }
}
