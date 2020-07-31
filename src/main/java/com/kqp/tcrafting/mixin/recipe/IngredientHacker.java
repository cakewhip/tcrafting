package com.kqp.tcrafting.mixin.recipe;

import com.kqp.tcrafting.recipe.interf.MatchingStackProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

/**
 * Used to preemptively cache matching stacks and add an accessor.
 */
@Mixin(Ingredient.class)
@Implements(@Interface(iface = MatchingStackProvider.class, prefix = "vw$"))
public abstract class IngredientHacker implements MatchingStackProvider {
    @Shadow
    private ItemStack[] matchingStacks;

    @Inject(method = "<init>(Ljava/util/stream/Stream;)V", at = @At("RETURN"))
    private void cacheAtInit(Stream entries, CallbackInfo callbackInfo) {
        this.invokeCacheMatchingStacks();
    }

    @Invoker
    public abstract void invokeCacheMatchingStacks();

    @Override
    public ItemStack[] getMatchingStacks() {
        return this.matchingStacks;
    }
}
