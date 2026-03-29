package com.lonevox.grapplinghookneo.common;

import com.lonevox.grapplinghookneo.config.GrappleConfig;
import com.lonevox.grapplinghookneo.entities.grapplehook.GrapplehookEntity;
import com.lonevox.grapplinghookneo.items.GrapplehookItem;
import com.lonevox.grapplinghookneo.items.LongFallBoots;
import com.lonevox.grapplinghookneo.network.GrappleDetachMessage;
import com.lonevox.grapplinghookneo.network.LoggedInMessage;
import com.lonevox.grapplinghookneo.server.ServerControllerManager;
import com.lonevox.grapplinghookneo.utils.GrapplemodUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.HashSet;

public class CommonEventHandlers {
    public CommonEventHandlers() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBreak(BreakEvent event) {
        Player player = event.getPlayer();
		ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		Item item = stack.getItem();
		if (item instanceof GrapplehookItem) {
			event.setCanceled(true);
		}
	}

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide) {
            Entity entity = event.getEntity();
            int id = entity.getId();
            boolean isConnected = ServerControllerManager.allGrapplehookEntities.containsKey(id);
            if (isConnected) {
                HashSet<GrapplehookEntity> grapplehookEntities = ServerControllerManager.allGrapplehookEntities.get(id);
                for (GrapplehookEntity hookEntity : grapplehookEntities) {
                    hookEntity.removeServer();
                }
                grapplehookEntities.clear();

                ServerControllerManager.attached.remove(id);

                GrapplehookItem.grapplehookEntitiesLeft.remove(entity);
                GrapplehookItem.grapplehookEntitiesRight.remove(entity);

                GrapplemodUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entity.level());
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack armor : player.getArmorSlots()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) {
                    if (event.getSource() == event.getEntity().level().damageSources().flyIntoWall()) {
                        event.setNewDamage(0.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFallEvent(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack armor : player.getArmorSlots()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStart(ServerStartedEvent event) {
        if (GrappleConfig.getConf().other.override_allowflight) {
            event.getServer().setFlightAllowed(true);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            NetworkSetup.sendToPlayer(player, new LoggedInMessage(GrappleConfig.getConf()));
        }
    }
}
