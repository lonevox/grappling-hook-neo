package com.lonevox.grapplinghookneo.items;

import com.lonevox.grapplinghookneo.client.ClientProxyInterface;
import com.lonevox.grapplinghookneo.controllers.GrappleController;
import com.lonevox.grapplinghookneo.utils.GrapplemodUtils;
import com.lonevox.grapplinghookneo.utils.Vec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class ForcefieldItem extends Item {
	public ForcefieldItem() {
		super(new Item.Properties().stacksTo(1));
	}
	
	public void doRightClick(ItemStack stack, Level worldIn, Player player) {
		if (worldIn.isClientSide) {
			int playerid = player.getId();
			GrappleController oldController = ClientProxyInterface.proxy.unregisterController(playerid);
			if (oldController == null || oldController.controllerId == GrapplemodUtils.AIRID) {
				ClientProxyInterface.proxy.createControl(GrapplemodUtils.REPELID, -1, playerid, worldIn, new Vec(0,0,0), null, null);
			}
		}
	}

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.doRightClick(stack, worldIn, playerIn);
        
    	return InteractionResultHolder.success(stack);
	}
    
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag par4) {
		list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.repelleritem.desc")));
		list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.repelleritem2.desc")));
		list.add(Component.literal(""));
		list.add(Component.literal(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindUseItem) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemon.desc")));
		list.add(Component.literal(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindUseItem) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemoff.desc")));
		list.add(Component.literal(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindSneak) + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemslow.desc")));
		list.add(Component.literal(ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindForward) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindLeft) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindBack) + ", " +
				ClientProxyInterface.proxy.getKeyname(ClientProxyInterface.McKeys.keyBindRight) +
				" " + ClientProxyInterface.proxy.localize("grappletooltip.repelleritemmove.desc")));
	}
}
