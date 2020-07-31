package com.kqp.tcrafting.mixin.recipe;

import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.interf.TRecipeManagerContainer;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.RegistryTagManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public class KnownTagConverter {
    @Shadow
    @Final
    private RegistryTagManager registryTagManager;

    @Inject(
            method = "loadRegistryTags",
            at = @At("TAIL")
    )
    private void convertKnownTags(CallbackInfo callbackInfo) {
        TRecipeManager tRecipeManager = ((TRecipeManagerContainer) this).getTCraftingRecipeManager();

        tRecipeManager.clearKnownTags();
        registryTagManager.items().getEntries().forEach(tRecipeManager::loadTag);
        tRecipeManager.identifyKnownTags();
    }
}
