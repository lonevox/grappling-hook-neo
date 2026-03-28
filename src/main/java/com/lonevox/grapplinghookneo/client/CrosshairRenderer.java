package com.lonevox.grapplinghookneo.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.lonevox.grapplinghookneo.common.CommonSetup;
import com.lonevox.grapplinghookneo.items.GrapplehookItem;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

public class CrosshairRenderer {
	protected static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");
	public Minecraft mc;
	
	float zLevel = -90.0F;
	
	public CrosshairRenderer() {
	    NeoForge.EVENT_BUS.register(this);
	    this.mc = Minecraft.getInstance();
	}
	
	@SubscribeEvent
	public void onRenderGameOverlayPost(RenderGuiLayerEvent.Post event) {
		PoseStack mStack = event.getGuiGraphics().pose();
		
        Options gamesettings = this.mc.options;
        if (!gamesettings.getCameraType().isFirstPerson()) return;
        if (this.mc.player.isSpectator()) return;
        if (this.mc.getDebugOverlay().showDebugScreen() && !gamesettings.hideGui && !this.mc.player.isReducedDebugInfo() && !gamesettings.reducedDebugInfo().get()) return;

		if (VanillaGuiLayers.CROSSHAIR.equals(event.getName())) {
			LocalPlayer player = this.mc.player;
			ItemStack grapplehookItemStack = null;
			if ((player.getItemInHand(InteractionHand.MAIN_HAND) != null && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem)) {
				grapplehookItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			} else if ((player.getItemInHand(InteractionHand.OFF_HAND) != null && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem)) {
				grapplehookItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
			}
			
			if (grapplehookItemStack != null) {
				GrappleCustomization custom = ((GrapplehookItem) CommonSetup.grapplingHookItem.get()).getCustomization(grapplehookItemStack);
            	double angle = Math.toRadians(custom.angle);
            	double verticalangle = Math.toRadians(custom.verticalthrowangle);
            	if (player.isCrouching()) {
            		angle = Math.toRadians(custom.sneakingangle);
            		verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
            	}
            	
            	if (!custom.doublehook) {
            		angle = 0;
            	}
            	
				Window resolution = this.mc.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();

            	double fov = Math.toRadians(gamesettings.fov().get());
            	fov *= player.getFieldOfViewModifier();
            	double l = ((double) h/2) / Math.tan(fov/2);
            	
            	if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
	            	int offset = (int) (Math.tan(angle) * l);
	            	int verticaloffset = (int) (-Math.tan(verticalangle) * l);
	            	
	            	drawCrosshair(event.getGuiGraphics(), w / 2 + offset, h / 2 + verticaloffset);
	                if (angle != 0) {
		            	drawCrosshair(event.getGuiGraphics(), w / 2 - offset, h / 2 + verticaloffset);
	                }
		        }
            	
            	if (custom.rocket && custom.rocket_vertical_angle != 0) {
	            	int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
	            	drawCrosshair(event.getGuiGraphics(), w / 2, h / 2 + verticaloffset);
            	}
			}

	    	double rocketFuel = ClientControllerManager.instance.rocketFuel;
	
	    	if (rocketFuel < 1) {
				Window resolution = this.mc.getWindow();
	            int w = resolution.getGuiScaledWidth();
	            int h = resolution.getGuiScaledHeight();
	            
	    		int totalbarlength = w / 8;

	            this.drawRect(event.getGuiGraphics(), w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
	            this.drawRect(event.getGuiGraphics(), w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);
	    	}
		}
	}
	
	private void drawCrosshair(GuiGraphics mStack, int x, int y) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		mStack.blit(GUI_ICONS_LOCATION, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 0, 0, 15, 15);
        RenderSystem.defaultBlendFunc();
	}

    public void drawRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int g, int a)
    {
		int color = ((a & 0xFF) << 24) | ((g & 0xFF) << 16) | ((g & 0xFF) << 8) | (g & 0xFF);
		guiGraphics.fill(x, y, x + width, y + height, color);
    }
}
