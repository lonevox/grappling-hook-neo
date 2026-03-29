package com.lonevox.grapplinghookneo.items;

import com.lonevox.grapplinghookneo.client.ClientProxyInterface;
import com.lonevox.grapplinghookneo.config.GrappleConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class LongFallBoots extends ArmorItem {
    public LongFallBoots() {
        super(ArmorMaterials.DIAMOND, Type.BOOTS, new Item.Properties().stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.@NotNull TooltipContext tooltipContext, @NotNull List<Component> list, @NotNull TooltipFlag par4) {
        if (!stack.isEnchanted()) {
            if (GrappleConfig.getConf().longfallboots.longfallbootsrecipe) {
                list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallbootsrecipe.desc")));
            }
        }
        list.add(Component.literal(ClientProxyInterface.proxy.localize("grappletooltip.longfallboots.desc")));
    }
}
