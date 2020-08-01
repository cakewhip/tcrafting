package com.kqp.tcrafting.client.screen;

import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.recipe.data.Reagent;
import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import com.kqp.tcrafting.screen.slot.TRecipeSlot;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Screen for TCrafting's crafting system.
 */
@Environment(EnvType.CLIENT)
public class TCraftingScreen extends HandledScreen<TCraftingScreenHandler> {
    public static final String TITLE_TRANSLATION_KEY = Util.createTranslationKey("gui", TCrafting.id("crafting"));
    public static final String RECIPE_LOOK_UP_TRANSLATION_KEY = Util.createTranslationKey("gui", TCrafting.id("tcrafting_recipe_look_up"));
    private static final Identifier TEXTURE = TCrafting.id("textures/gui/crafting.png");

    /**
     * Position of the crafting outputs scroll bar.
     */
    public float craftingScrollPosition = 0.0F;

    /**
     * Position of the recipe look-up scroll bar.
     */
    public float lookUpScrollPosition = 0.0F;

    public TCraftingScreen(TCraftingScreenHandler screenHandler, PlayerInventory playerInventory) {
        super(screenHandler, playerInventory, new TranslatableText(TITLE_TRANSLATION_KEY));
        this.backgroundWidth = 256;
        this.backgroundHeight = 166;
        this.passEvents = false;

        TCraftingNetwork.REQUEST_CRAFTING_SESSION_SYNC_C2S.sendEmptyToServer();
        syncCraftingResultScrollbar();
    }

    /**
     * Overriden to render the tooltips for the tabs.
     *
     * @param matrices
     * @param mouseX
     * @param mouseY
     * @param delta
     */
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

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

    /**
     * Overriden to add the reagents needed to craft the hovered slot.
     *
     * @param matrices
     * @param stack
     * @param x
     * @param y
     */
    @Override
    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        List<StringRenderable> text = new ArrayList();
        text.addAll(this.getTooltipFromItem(stack));

        if (this.focusedSlot instanceof TRecipeSlot) {
            TRecipeSlot recipeSlot = (TRecipeSlot) this.focusedSlot;

            if (recipeSlot.view == TCraftingScreenHandler.View.CRAFTING) {
                List<Boolean> availabilities = getScreenHandler().craftingSession.craftingRecipeAvailabilities;

                if (recipeSlot.recipeIndex < availabilities.size()) {
                    boolean available = availabilities.get(recipeSlot.recipeIndex);

                    addRecipeTooltip(recipeSlot.getRecipe(), text, available);
                }
            } else {
                addRecipeTooltip(recipeSlot.getRecipe(), text, true);
            }
        }

        this.renderTooltip(matrices, text, x, y);
    }

    private void addRecipeTooltip(TRecipe recipe, List<StringRenderable> text, boolean available) {
        text.add(new LiteralText(""));

        if (!available) {
            text.add(new LiteralText("Not available").formatted(Formatting.RED));
            text.add(new LiteralText(""));
        }

        String recipeTypeKey = "tcrafting.recipe_type." + recipe.recipeType.getPath() + ".tooltip";
        if (I18n.hasTranslation(recipeTypeKey)) {
            text.add(new TranslatableText(recipeTypeKey));
        }

        text.add(new LiteralText("To Craft: "));
        for (Reagent reagent : recipe.reagents.keySet()) {
            String reagentLine = recipe.reagents.get(reagent) + " x " + reagent.getTooltip();
            List<StringRenderable> split = this.textRenderer.wrapLines(new LiteralText(reagentLine), 126);

            for (StringRenderable splitLine : split) {
                text.add(splitLine);
            }
        }
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, 8.0F, 8.0F, 4210752);
        this.textRenderer.draw(matrices, new TranslatableText(RECIPE_LOOK_UP_TRANSLATION_KEY), 186.0F, 8.0F, 4210752);
        this.textRenderer.draw(matrices, this.playerInventory.getDisplayName(), 8.0F, (float) (this.backgroundHeight - 96 + 4), 4210752);

        renderAvailabilities(matrices);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);

        this.drawTexture(matrices, i, j - 28, 0, 166, 28, 32);

        this.drawTexture(matrices, i, j, 0, 0, 176, 166);

        this.drawTexture(matrices, i + 176 + 2, j, 178, 0, 78, 166);

        this.drawTexture(matrices, i + 156, j + 18 + (int) ((float) (52 - 15) * this.craftingScrollPosition), 56 + (this.hasOutputsScrollbar() ? 0 : 12), 166, 12, 15);

        this.drawTexture(matrices, i + 242, j + 48 + (int) ((float) (106 - 11) * this.lookUpScrollPosition), 80 + (this.hasRecipeLookUpScrollbar() ? 0 : 6), 166, 6, 11);

        this.drawTexture(matrices, i + 29, j - 28, 28, 198, 28, 32);

        this.setZOffset(100);
        this.itemRenderer.zOffset = 100.0F;
        RenderSystem.enableRescaleNormal();
        ItemStack itemStack = new ItemStack(Blocks.CRAFTING_TABLE);
        this.itemRenderer.renderGuiItemIcon(itemStack, this.x + 29 + (28 - 15) / 2, this.y - 28 + 10);
        this.itemRenderer.renderGuiItemOverlay(this.textRenderer, itemStack, this.x + 29 + (28 - 15) / 2, this.y - 28 + 10);
        this.itemRenderer.zOffset = 0.0F;
        this.setZOffset(0);
    }

    private void renderAvailabilities(MatrixStack matrices) {
        List<Boolean> availabilities = getScreenHandler().craftingSession.craftingRecipeAvailabilities;

        if (availabilities.size() > 0) {
            int oX = (this.width - this.backgroundWidth) / 2;
            int oY = (this.height - this.backgroundHeight) / 2;

            MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
            this.setZOffset(200);
            RenderSystem.enableBlend();

            int counter = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 8; j++) {
                    TRecipeSlot craftingSlot = (TRecipeSlot) getScreenHandler().getSlot(counter++);

                    if (craftingSlot.recipeIndex != -1) {
                        if (craftingSlot.recipeIndex < availabilities.size() && !availabilities.get(craftingSlot.recipeIndex)) {
                            int x = 7 + j * 18;
                            int y = 17 + i * 18;

                            this.drawTexture(matrices, x, y, 56, 181, 18, 18);
                        }
                    }
                }
            }

            RenderSystem.disableBlend();
            this.setZOffset(0);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        double aX = mouseX - this.x;
        double aY = mouseY - this.y;

        if (aX >= 0 && aY >= 0 && aX < 176 && aY < this.backgroundHeight) {
            if (this.hasOutputsScrollbar()) {
                int i = (this.getScreenHandler().craftingSession.craftingItemStacks.size() + 8 - 1) / 8 - 3;
                this.craftingScrollPosition = (float) ((double) this.craftingScrollPosition - amount / (double) i);
                this.craftingScrollPosition = MathHelper.clamp(this.craftingScrollPosition, 0.0F, 1.0F);
                syncCraftingResultScrollbar();

                return true;
            }
        } else if (aX >= 176 + 2 && aY >= 0 && aX < 176 + 2 + 78 && aY < this.backgroundHeight) {
            if (this.hasRecipeLookUpScrollbar()) {
                int i = (this.getScreenHandler().craftingSession.lookUpItemStacks.size() + 3 - 1) / 3 - 6;
                this.lookUpScrollPosition = (float) ((double) this.lookUpScrollPosition - amount / (double) i);
                this.lookUpScrollPosition = MathHelper.clamp(this.lookUpScrollPosition, 0.0F, 1.0F);
                syncLookUpResultScrollbar();

                return true;
            }
        }

        return false;
    }

    @Override
    protected void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType) {
        if (this.focusedSlot instanceof TRecipeSlot) {
            if (((TRecipeSlot) this.focusedSlot).view == TCraftingScreenHandler.View.LOOK_UP) {
                return;
            }
        }

        super.onMouseClick(slot, invSlot, clickData, actionType);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double aX = mouseX - this.x;
            double aY = mouseY - this.y;

            if (aX > 0 && aX < 28) {
                if (aY > -32 && aY < 0) {
                    ClientPlayerEntity player = (ClientPlayerEntity) playerInventory.player;
                    MinecraftClient.getInstance().openScreen(new InventoryScreen(player));
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean hasOutputsScrollbar() {
        return this.getScreenHandler().shouldShowCraftingScrollbar();
    }

    private boolean hasRecipeLookUpScrollbar() {
        return this.getScreenHandler().shouldShowLookUpScrollbar();
    }

    /**
     * Sends the server the position of the crafting scroll bar.
     */
    public void syncCraftingResultScrollbar() {
        TCraftingNetwork.SYNC_SCROLL_POSITION_C2S.sendToServer(TCraftingScreenHandler.View.CRAFTING, craftingScrollPosition);
    }


    /**
     * Sends the server the position of the look-up scroll bar.
     */
    public void syncLookUpResultScrollbar() {
        getScreenHandler().craftingSession.scrollLookUpResults(lookUpScrollPosition);
    }
}
