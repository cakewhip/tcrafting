package com.kqp.tcrafting.mixin.recipe;

import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.interf.TRecipeManagerContainer;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Used to initialize the TCrafting recipe manager and add vanilla recipes to it.
 */
@Mixin(ServerResourceManager.class)
public class TRecipeManagerInitializer implements TRecipeManagerContainer {
    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Shadow
    @Final
    private RecipeManager recipeManager;

    private TRecipeManager tRecipeManager;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void construct(CallbackInfo callbackInfo) {
        this.tRecipeManager = new TRecipeManager(recipeManager);

        resourceManager.registerListener(this.tRecipeManager);
    }

    @Override
    public TRecipeManager getTCraftingRecipeManager() {
        return tRecipeManager;
    }

    @Override
    public void setTCraftingRecipeManager(TRecipeManager tRecipeManager) {
        this.tRecipeManager = tRecipeManager;
    }
}
