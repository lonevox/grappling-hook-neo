package com.lonevox.grapplinghookneo.client;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.mojang.blaze3d.platform.InputConstants;
import com.lonevox.grapplinghookneo.common.CommonSetup;
import com.lonevox.grapplinghookneo.controllers.AirfrictionController;
import com.lonevox.grapplinghookneo.controllers.ForcefieldController;
import com.lonevox.grapplinghookneo.entities.grapplehook.GrapplehookEntity;
import com.lonevox.grapplinghookneo.entities.grapplehook.RenderGrapplehookEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.ArrayList;

@EventBusSubscriber(modid = GrapplingHookNeo.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
	public static ClientSetup instance = null;
	
	public ClientSetup() {
	}

	public CrosshairRenderer crosshairRenderer;
	public ClientEventHandlers clientEventHandlers;
	public ClientControllerManager clientControllerManager;
	
	public static ArrayList<KeyMapping> keyBindings = new ArrayList<KeyMapping>();
	
	public static KeyMapping createKeyBinding(KeyMapping k) {
		keyBindings.add(k);
		return k;
	}
	
	public static KeyMapping key_boththrow = createKeyBinding(new NonConflictingKeyBinding("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grappling_hook_neo.category"));
	public static KeyMapping key_leftthrow = createKeyBinding(new NonConflictingKeyBinding("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grappling_hook_neo.category"));
	public static KeyMapping key_rightthrow = createKeyBinding(new NonConflictingKeyBinding("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grappling_hook_neo.category"));
	public static KeyMapping key_motoronoff = createKeyBinding(new NonConflictingKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grappling_hook_neo.category"));
	public static KeyMapping key_jumpanddetach = createKeyBinding(new NonConflictingKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grappling_hook_neo.category"));
	public static KeyMapping key_slow = createKeyBinding(new NonConflictingKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grappling_hook_neo.category"));
	public static KeyMapping key_climb = createKeyBinding(new NonConflictingKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grappling_hook_neo.category"));
	public static KeyMapping key_climbup = createKeyBinding(new NonConflictingKeyBinding("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grappling_hook_neo.category"));
	public static KeyMapping key_climbdown = createKeyBinding(new NonConflictingKeyBinding("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grappling_hook_neo.category"));
	public static KeyMapping key_enderlaunch = createKeyBinding(new NonConflictingKeyBinding("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grappling_hook_neo.category"));
	public static KeyMapping key_rocket = createKeyBinding(new NonConflictingKeyBinding("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grappling_hook_neo.category"));
	public static KeyMapping key_slide = createKeyBinding(new NonConflictingKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grappling_hook_neo.category"));

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
	    instance = new ClientSetup();
		// The onclientSetup method calls ItemProperties::register which is
		// not thread-safe, so enqueue it.
		event.enqueueWork(instance::onClientSetup);
	}
	
	private static class GrapplehookEntityRenderFactory implements EntityRendererProvider<GrapplehookEntity> {
	    @Override
	    public EntityRenderer<GrapplehookEntity> create(Context manager) {
	        return new RenderGrapplehookEntity<>(manager, CommonSetup.grapplingHookItem.get());
	    }
	}

	@SubscribeEvent
	public static void registerKeyBinding(RegisterKeyMappingsEvent event){
		keyBindings.forEach(event::register);
	}
	
	public void onClientSetup() {
//		// register all the key bindings
//		for (int i = 0; i < keyBindings.size(); ++i)
//		{
//		    ClientRegistry.registerKeyBinding(keyBindings.get(i));
//		}
		
	    EntityRenderers.register(CommonSetup.grapplehookEntityType.get(), new GrapplehookEntityRenderFactory());

		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
    		() -> (modContainer, screen) -> ((ClientProxy) ClientProxyInterface.proxy).onConfigScreen(Minecraft.getInstance(), screen));
		
	    this.registerPropertyOverride();
	    
		crosshairRenderer = new CrosshairRenderer();
		clientControllerManager = new ClientControllerManager();
		clientEventHandlers = new ClientEventHandlers();
	}
	
	public void registerPropertyOverride() {
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("rocket"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyRocket(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("double"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyDouble(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("motor"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyMotor(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("smart"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertySmart(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("enderstaff"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyEnderstaff(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("magnet"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyMagnet(stack, world, entity) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("attached"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && !(ClientControllerManager.controllers.get(entity.getId()) instanceof AirfrictionController)) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.forcefieldItem.get(), ResourceLocation.withDefaultNamespace("attached"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				if (entity == null) {return 0;}
				return (ClientControllerManager.controllers.containsKey(entity.getId()) && ClientControllerManager.controllers.get(entity.getId()) instanceof ForcefieldController) ? 1 : 0;
			}
		});
		ItemProperties.register(CommonSetup.grapplingHookItem.get(), ResourceLocation.withDefaultNamespace("hook"), new ItemPropertyFunction() {
			public float call(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
				return CommonSetup.grapplingHookItem.get().getPropertyHook(stack, world, entity) ? 1 : 0;
			}
		});
	}
}
