package com.lonevox.grapplinghookneo.enchantments;

import com.lonevox.grapplinghookneo.GrapplingHookNeo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> WALLRUN = key("wallrun");
    public static final ResourceKey<Enchantment> DOUBLE_JUMP = key("double_jump");
    public static final ResourceKey<Enchantment> SLIDING = key("sliding");

    private ModEnchantments() {
    }

    public static int getWallrunLevel(Entity entity) {
        return getArmorEnchantmentLevel(entity, WALLRUN);
    }

    public static int getDoubleJumpLevel(Entity entity) {
        return getArmorEnchantmentLevel(entity, DOUBLE_JUMP);
    }

    public static int getSlidingLevel(Entity entity) {
        return getArmorEnchantmentLevel(entity, SLIDING);
    }

    public static boolean hasWallrunEnchantment(Entity entity) {
        return getWallrunLevel(entity) > 0;
    }

    public static boolean hasDoubleJumpEnchantment(Entity entity) {
        return getDoubleJumpLevel(entity) > 0;
    }

    public static boolean hasSlidingEnchantment(Entity entity) {
        return getSlidingLevel(entity) > 0;
    }

    private static int getArmorEnchantmentLevel(Entity entity, ResourceKey<Enchantment> enchantmentKey) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 0;
        }

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = livingEntity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> enchantmentHolder = enchantmentLookup.get(enchantmentKey).orElse(null);
        if (enchantmentHolder == null) {
            return 0;
        }

        int level = 0;
        for (ItemStack stack : livingEntity.getArmorSlots()) {
            if (stack != null && !stack.isEmpty()) {
                level = Math.max(level, EnchantmentHelper.getTagEnchantmentLevel(enchantmentHolder, stack));
            }
        }

        return level;
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(GrapplingHookNeo.MODID, path));
    }
}
