package com.kqp.tcrafting.recipe.data;

import com.kqp.tcrafting.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a reagent of a recipe.
 * Multiple types of item stacks can fulfill this requirement, see {@link #matchingStacks}.
 * Also caches the hash code.
 */
public class Reagent {
    /**
     * What stacks can fulfill this reagent requirement.
     */
    public final HashSet<ComparableItemStack> matchingStacks;

    public final int hashCode;

    public String customTooltipKey = "";

    /**
     * Creates a reagent given item stacks.
     *
     * @param itemStacks ItemStacks
     */
    public Reagent(List<ItemStack> itemStacks) {
        this.matchingStacks = new HashSet();

        itemStacks.forEach(itemStack -> {
            matchingStacks.add(new ComparableItemStack(itemStack));
        });

        hashCode = Objects.hash(matchingStacks);
    }

    /**
     * Takes one item stack and creates a reagent.
     *
     * @param itemStack ItemStack
     */
    public Reagent(ItemStack itemStack) {
        this(Arrays.asList(itemStack));
    }

    public void setCustomTooltipKey(String customTooltipKey) {
        this.customTooltipKey = customTooltipKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reagent reagent = (Reagent) o;
        return Objects.equals(matchingStacks, reagent.matchingStacks)
                && Objects.equals(customTooltipKey, reagent.customTooltipKey);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Environment(EnvType.CLIENT)
    public String getTooltip() {
        if (customTooltipKey != null && !customTooltipKey.isEmpty()) {
            return I18n.translate(customTooltipKey);
        }

        List<String> list = matchingStacks.stream().map(ComparableItemStack::toString).collect(Collectors.toList());

        return StringUtil.commaSeparatedOr(list);
    }
}
