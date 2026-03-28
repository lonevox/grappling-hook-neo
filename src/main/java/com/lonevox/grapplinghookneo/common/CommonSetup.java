package com.lonevox.grapplinghookneo.common;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import com.lonevox.grapplinghookneo.blocks.modifierblock.BlockGrappleModifier;
import com.lonevox.grapplinghookneo.blocks.modifierblock.TileEntityGrappleModifier;
import com.lonevox.grapplinghookneo.client.ClientProxyInterface;
import com.lonevox.grapplinghookneo.entities.grapplehook.GrapplehookEntity;
import com.lonevox.grapplinghookneo.items.EnderStaffItem;
import com.lonevox.grapplinghookneo.items.ForcefieldItem;
import com.lonevox.grapplinghookneo.items.GrapplehookItem;
import com.lonevox.grapplinghookneo.items.LongFallBoots;
import com.lonevox.grapplinghookneo.items.upgrades.BaseUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.DoubleUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.ForcefieldUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.LimitsUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.MagnetUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.MotorUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.RocketUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.RopeUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.StaffUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.SwingUpgradeItem;
import com.lonevox.grapplinghookneo.items.upgrades.ThrowUpgradeItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CommonSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, GrapplingHookNeo.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, GrapplingHookNeo.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GrapplingHookNeo.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, GrapplingHookNeo.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GrapplingHookNeo.MODID);

    public static final DeferredHolder<Item, GrapplehookItem> grapplingHookItem = ITEMS.register("grapplinghook", GrapplehookItem::new);
    public static final DeferredHolder<Item, EnderStaffItem> enderStaffItem = ITEMS.register("launcheritem", EnderStaffItem::new);
    public static final DeferredHolder<Item, ForcefieldItem> forcefieldItem = ITEMS.register("repeller", ForcefieldItem::new);

    public static final DeferredHolder<Item, BaseUpgradeItem> baseUpgradeItem = ITEMS.register("baseupgradeitem", BaseUpgradeItem::new);
    public static final DeferredHolder<Item, DoubleUpgradeItem> doubleUpgradeItem = ITEMS.register("doubleupgradeitem", DoubleUpgradeItem::new);
    public static final DeferredHolder<Item, ForcefieldUpgradeItem> forcefieldUpgradeItem = ITEMS.register("forcefieldupgradeitem", ForcefieldUpgradeItem::new);
    public static final DeferredHolder<Item, MagnetUpgradeItem> magnetUpgradeItem = ITEMS.register("magnetupgradeitem", MagnetUpgradeItem::new);
    public static final DeferredHolder<Item, MotorUpgradeItem> motorUpgradeItem = ITEMS.register("motorupgradeitem", MotorUpgradeItem::new);
    public static final DeferredHolder<Item, RopeUpgradeItem> ropeUpgradeItem = ITEMS.register("ropeupgradeitem", RopeUpgradeItem::new);
    public static final DeferredHolder<Item, StaffUpgradeItem> staffUpgradeItem = ITEMS.register("staffupgradeitem", StaffUpgradeItem::new);
    public static final DeferredHolder<Item, SwingUpgradeItem> swingUpgradeItem = ITEMS.register("swingupgradeitem", SwingUpgradeItem::new);
    public static final DeferredHolder<Item, ThrowUpgradeItem> throwUpgradeItem = ITEMS.register("throwupgradeitem", ThrowUpgradeItem::new);
    public static final DeferredHolder<Item, LimitsUpgradeItem> limitsUpgradeItem = ITEMS.register("limitsupgradeitem", LimitsUpgradeItem::new);
    public static final DeferredHolder<Item, RocketUpgradeItem> rocketUpgradeItem = ITEMS.register("rocketupgradeitem", RocketUpgradeItem::new);

    public static final DeferredHolder<Item, LongFallBoots> longFallBootsItem = ITEMS.register("longfallboots", LongFallBoots::new);

    public static final DeferredHolder<Block, BlockGrappleModifier> grappleModifierBlock = BLOCKS.register("block_grapple_modifier", () -> new BlockGrappleModifier());
    public static final DeferredHolder<Item, BlockItem> grappleModifierBlockItem = ITEMS.register("block_grapple_modifier", () -> new BlockItem(grappleModifierBlock.get(), new Item.Properties().stacksTo(64)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityGrappleModifier>> grappleModifierTileEntityType = BLOCK_ENTITY_TYPES.register("block_grapple_modifier", () -> BlockEntityType.Builder.of(TileEntityGrappleModifier::new, grappleModifierBlock.get()).build(null));

    public static final DeferredHolder<EntityType<?>, EntityType<GrapplehookEntity>> grapplehookEntityType = ENTITY_TYPES.register("grapplehook", () -> EntityType.Builder.<GrapplehookEntity>of(GrapplehookEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .build(GrapplingHookNeo.MODID + ":grapplehook"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GRAPPLE_TAB = TABS.register("grappling_hook_neo", () -> CreativeModeTab.builder().displayItems(
            (itemDisplayParameters, output) -> {
                ITEMS.getEntries().forEach((entry) -> output.accept(new ItemStack(entry.get())));
                if (ClientProxyInterface.proxy != null) {
                    ClientProxyInterface.proxy.fillGrappleVariants(output);
                }
            }).icon(() -> new ItemStack(grapplingHookItem.get())).title(Component.translatable("itemGroup.grappling_hook_neo")).build());

    public static final CommonEventHandlers eventHandlers = new CommonEventHandlers();
}
