package com.lonevox.grapplinghookneo.client;

import com.lonevox.grapplinghookneo.blocks.modifierblock.GuiModifier;
import com.lonevox.grapplinghookneo.blocks.modifierblock.TileEntityGrappleModifier;
import com.lonevox.grapplinghookneo.common.CommonSetup;
import com.lonevox.grapplinghookneo.config.GrappleConfig;
import com.lonevox.grapplinghookneo.controllers.GrappleController;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import com.lonevox.grapplinghookneo.utils.GrapplemodUtils;
import com.lonevox.grapplinghookneo.utils.Vec;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientProxy extends ClientProxyInterface {
	public ResourceLocation doubleJumpSoundLoc = ResourceLocation.fromNamespaceAndPath("grappling_hook_neo", "doublejump");
	public ResourceLocation slideSoundLoc = ResourceLocation.fromNamespaceAndPath("grappling_hook_neo", "slide");

	public ClientProxy() {
	}
	
	@Override
	public void startRocket(Player player, GrappleCustomization custom) {
		ClientControllerManager.instance.startRocket(player, custom);
	}
	
	@Override
	public String getKeyname(McKeys keyenum) {
		KeyMapping binding = null;
		
		Options gs = Minecraft.getInstance().options;
		
		if (keyenum == McKeys.keyBindAttack) {
			binding = gs.keyAttack;
		} else if (keyenum == McKeys.keyBindBack) {
			binding = gs.keyDown;
		} else if (keyenum == McKeys.keyBindForward) {
			binding = gs.keyUp;
		} else if (keyenum == McKeys.keyBindJump) {
			binding = gs.keyJump;
		} else if (keyenum == McKeys.keyBindLeft) {
			binding = gs.keyLeft;
		} else if (keyenum == McKeys.keyBindRight) {
			binding = gs.keyRight;
		} else if (keyenum == McKeys.keyBindSneak) {
			binding = gs.keyShift;
		} else if (keyenum == McKeys.keyBindUseItem) {
			binding = gs.keyUse;
		}
		
		if (binding == null) {
			return "";
		}
		
		String displayname = binding.getTranslatedKeyMessage().getString();
		if (displayname.equals("Button 1")) {
			return "Left Click";
		} else if (displayname.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayname;
		}
	}

	@Override
	public void openModifierScreen(TileEntityGrappleModifier tileent) {
		Minecraft.getInstance().setScreen(new GuiModifier(tileent));

	}
	
	@Override
	public String localize(String string) {
		return I18n.get(string);
	}

	@Override
	public void playSlideSound(Entity entity) {
//		entity.playSound(new SoundEvent(this.slideSoundLoc), GrappleConfig.getClientConf().sounds.slide_sound_volume, 1.0F);
		this.playSound(this.slideSoundLoc, GrappleConfig.getClientConf().sounds.slide_sound_volume);
	}

	@Override
	public void playDoubleJumpSound(Entity entity) {
//		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F, 1.0F);
		this.playSound(this.doubleJumpSoundLoc, GrappleConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F);
	}

	@Override
	public void playWallrunJumpSound(Entity entity) {
//		entity.playSound(new SoundEvent(this.doubleJumpSoundLoc), GrappleConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F, 1.0F);
		this.playSound(this.doubleJumpSoundLoc, GrappleConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F);
	}
	
	List<ItemStack> grapplingHookVariants = null;

	@Override
	public void fillGrappleVariants(CreativeModeTab.Output items) {
		if (grapplingHookVariants == null) {
			grapplingHookVariants = new ArrayList<>();

			grapplingHookVariants.add(createHookVariant(custom -> custom.motor = true));
			grapplingHookVariants.add(createHookVariant(custom -> {
				custom.motor = true;
				custom.smartmotor = true;
			}));
			grapplingHookVariants.add(createHookVariant(custom -> {
				custom.motor = true;
				custom.doublehook = true;
			}));
			grapplingHookVariants.add(createHookVariant(custom -> custom.enderstaff = true));
			grapplingHookVariants.add(createHookVariant(custom -> custom.attract = true));
			grapplingHookVariants.add(createHookVariant(custom -> custom.rocket = true));
			grapplingHookVariants.add(createHookVariant(custom -> {
				custom.rocket = true;
				custom.doublehook = true;
			}));
		}

		items.acceptAll(grapplingHookVariants);
	}

	private ItemStack createHookVariant(Consumer<GrappleCustomization> customizer) {
		ItemStack stack = new ItemStack(CommonSetup.grapplingHookItem.get());
		GrappleCustomization custom = new GrappleCustomization();
		customizer.accept(custom);
		CommonSetup.grapplingHookItem.get().setCustomOnServer(stack, custom);
		return stack;
	}
	
	public Screen onConfigScreen(Screen screen) {
		return AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
	}

	@Override
	public void resetLauncherTime(int playerid) {
		ClientControllerManager.instance.resetLauncherTime(playerid);
	}

	@Override
	public void launchPlayer(Player player) {
		ClientControllerManager.instance.launchPlayer(player);
	}

	@Override
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		ClientControllerManager.instance.updateRocketRegen(rocket_active_time, rocket_refuel_ratio);
	}

	@Override
	public double getRocketFunctioning() {
		return ClientControllerManager.instance.getRocketFunctioning();
	}

	@Override
	public boolean isWallRunning(Entity entity, Vec motion) {
		return ClientControllerManager.instance.isWallRunning(entity, motion);
	}

	@Override
	public boolean isSliding(Entity entity, Vec motion) {
		return ClientControllerManager.instance.isSliding(entity, motion);
	}

	@Override
	public GrappleController createControl(int id, int hookEntityId, int entityid, Level world, Vec pos, BlockPos blockpos,
			GrappleCustomization custom) {
		return ClientControllerManager.instance.createControl(id, hookEntityId, entityid, world, pos, blockpos, custom);
	}

	@Override
	public boolean isKeyDown(GrappleKeys key) {
		if (key == ClientProxyInterface.GrappleKeys.key_boththrow) {return ClientSetup.key_boththrow.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_leftthrow) {return ClientSetup.key_leftthrow.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_rightthrow) {return ClientSetup.key_rightthrow.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_motoronoff) {return ClientSetup.key_motoronoff.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_jumpanddetach) {return ClientSetup.key_jumpanddetach.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_slow) {return ClientSetup.key_slow.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_climb) {return ClientSetup.key_climb.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_climbup) {return ClientSetup.key_climbup.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_climbdown) {return ClientSetup.key_climbdown.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_enderlaunch) {return ClientSetup.key_enderlaunch.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_rocket) {return ClientSetup.key_rocket.isDown();}
		else if (key == ClientProxyInterface.GrappleKeys.key_slide) {return ClientSetup.key_slide.isDown();}
		return false;
	}

	@Override
	public GrappleController unregisterController(int entityId) {
		return ClientControllerManager.unregisterController(entityId);
	}

	@Override
	public double getTimeSinceLastRopeJump(Level world) {
		return GrapplemodUtils.getTime(world) - ClientControllerManager.prevRopeJumpTime;
	}

	@Override
	public void resetRopeJumpTime(Level world) {
		ClientControllerManager.prevRopeJumpTime = GrapplemodUtils.getTime(world);
	}

	@Override
	public boolean isKeyDown(McKeys keyenum) {
		if (keyenum == McKeys.keyBindAttack) {
			return Minecraft.getInstance().options.keyAttack.isDown();
		} else if (keyenum == McKeys.keyBindBack) {
			return Minecraft.getInstance().options.keyDown.isDown();
		} else if (keyenum == McKeys.keyBindForward) {
			return Minecraft.getInstance().options.keyUp.isDown();
		} else if (keyenum == McKeys.keyBindJump) {
			return Minecraft.getInstance().options.keyJump.isDown();
		} else if (keyenum == McKeys.keyBindLeft) {
			return Minecraft.getInstance().options.keyLeft.isDown();
		} else if (keyenum == McKeys.keyBindRight) {
			return Minecraft.getInstance().options.keyRight.isDown();
		} else if (keyenum == McKeys.keyBindSneak) {
			return Minecraft.getInstance().options.keyShift.isDown();
		} else if (keyenum == McKeys.keyBindUseItem) {
			return Minecraft.getInstance().options.keyUse.isDown();
		}
		return false;
	}

	@Override
	public boolean isMovingSlowly(Entity entity) {
		if (entity instanceof LocalPlayer) {
			return ((LocalPlayer) entity).isMovingSlowly();
		}
		return false;
	}
	
	@Override
	public void playSound(ResourceLocation loc, float volume) {
		Player player = Minecraft.getInstance().player;
		Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(loc, SoundSource.PLAYERS, volume, 1.0F, RandomSource.create(),false, 0, SoundInstance.Attenuation.NONE, player.getX(), player.getY(), player.getZ(), false));
	}

	@Override
	public int getWallrunTicks() {
		return ClientControllerManager.instance.ticksWallRunning;
	}

	@Override
	public void setWallrunTicks(int newWallrunTicks) {
		ClientControllerManager.instance.ticksWallRunning = newWallrunTicks;
	}
}
