package com.lonevox.grapplinghookneo;

import com.lonevox.grapplinghookneo.common.CommonSetup;
import com.lonevox.grapplinghookneo.config.GrappleConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO
// Pull mobs
// Attach 2 things together
// wallrun on diagonal walls
// smart motor acts erratically when aiming above hook
// key events

@Mod(GrapplingHookNeo.MODID)
public class GrapplingHookNeo {
    public static final String MODID = "grappling_hook_neo";

    public static final Logger LOGGER = LogManager.getLogger();

    public GrapplingHookNeo(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, GrappleConfig.SERVER_SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, GrappleConfig.CLIENT_SPEC);
        bus.addListener(GrappleConfig::onModConfigLoading);
        bus.addListener(GrappleConfig::onModConfigReloading);

        CommonSetup.BLOCKS.register(bus);
        CommonSetup.ITEMS.register(bus);
        CommonSetup.ENTITY_TYPES.register(bus);
        CommonSetup.BLOCK_ENTITY_TYPES.register(bus);
        CommonSetup.TABS.register(bus);
    }
}
