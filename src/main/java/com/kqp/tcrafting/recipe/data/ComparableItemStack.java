package com.kqp.tcrafting.recipe.data;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Data class used to fast-compare item stacks.
 * Only compares using the Item and the CompoundTag.
 * Also caches the hash code.
 */
public class ComparableItemStack {
    public final ItemStack itemStack;
    public final Item item;
    public final CompoundTag tag;
    public final int hashCode;

    public ComparableItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.item = itemStack.getItem();
        this.tag = itemStack.getTag();
        this.hashCode = Objects.hash(item, tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparableItemStack that = (ComparableItemStack) o;
        return item == that.item &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return I18n.translate(item.getTranslationKey());
    }
}
