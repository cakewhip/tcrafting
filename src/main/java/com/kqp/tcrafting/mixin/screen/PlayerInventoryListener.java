package com.kqp.tcrafting.mixin.screen;

import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Used to listen for item stack inserts, like when the player picks up an item.
 * For some reason, this event is propagated to clients when the crafting screen is open.
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryListener {
    @Inject(
            method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD")
    )
    private void listenForInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
        PlayerEntity player = ((PlayerInventory) (Object) this).player;

        if (!player.world.isClient) {
            if (player.currentScreenHandler instanceof TCraftingScreenHandler) {
                //TCraftingNetwork.SYNC_STACK_INSERT_S2C.sendToPlayer(
                //        (ServerPlayerEntity) player,
                //        stack
                //);
            }
        }
    }

    @Inject(
            method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("TAIL")
    )
    private void updatePostInsertion(ItemStack stack, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (callbackInfo.getReturnValue()) {
            PlayerEntity player = ((PlayerInventory) (Object) this).player;

            if (!player.world.isClient) {
                if (player.currentScreenHandler instanceof TCraftingScreenHandler) {
                    ((TCraftingScreenHandler) player.currentScreenHandler).updateCraftingResults();
                }
            }
        }
    }
}
