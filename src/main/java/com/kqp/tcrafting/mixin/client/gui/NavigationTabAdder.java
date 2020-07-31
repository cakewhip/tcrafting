package com.kqp.tcrafting.mixin.client.gui;

import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.init.TCraftingClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

/**
 * Used to add the navigation tabs to reach the TCrafting crafting screen.
 */
@Mixin(InventoryScreen.class)
public abstract class NavigationTabAdder extends AbstractInventoryScreen<PlayerScreenHandler> {
    private static final Identifier TEXTURE = TCrafting.id("textures/gui/crafting.png");

    public NavigationTabAdder(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void catchMouseClickEvent(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> callbackInfo) {
        if (button == 0) {
            double aX = mouseX - this.x;
            double aY = mouseY - this.y;

            if (aX > 29 && aX < 57) {
                if (aY > -32 && aY < 0) {
                    TCraftingClient.triggerOpenCraftingMenu();

                    callbackInfo.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "drawBackground", at = @At("HEAD"))
    public void drawCraftingTab(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(matrices, this.x + 29, this.y - 28, 28, 166, 28, 32);

        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0F;
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = new ItemStack(Blocks.CRAFTING_TABLE);
        this.itemRenderer.renderGuiItemIcon(itemStack, this.x + 29 + (28 - 15) / 2, this.y - 28 + 10);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, this.x + 29 + (28 - 15) / 2, this.y - 28 + 10);
        this.itemRenderer.zOffset = 0.0F;
        this.setZOffset(0);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void drawPlayerTab(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(matrices, this.x, this.y - 28, 0, 198, 28, 32);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void drawTooltips(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        double aX = mouseX - this.x;
        double aY = mouseY - this.y;

        if (aY > -24 && aY < 0) {
            if (aX > 0 && aX < 28) {
                this.renderTooltip(matrices, Arrays.asList(new LiteralText("Player")), mouseX, mouseY);
            } else if (aX > 29 && aX < 57) {
                this.renderTooltip(matrices, Arrays.asList(new LiteralText("Crafting")), mouseX, mouseY);
            }
        }
    }
}
