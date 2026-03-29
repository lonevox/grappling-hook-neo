package com.lonevox.grapplinghookneo.items.upgrades;

import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.upgradeCategories category;
	boolean craftingRemaining = false;

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		
		this.category = theCategory;
		
		if (theCategory != null) {
			this.setCraftingRemainingItem();
		}
	}
	
	public void setCraftingRemainingItem() {
		craftingRemaining = true;
	}

	@Override
	public @NotNull ItemStack getCraftingRemainingItem(@NotNull ItemStack itemStack)
    {
        if (!this.craftingRemaining)
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(this);
    }
	
	@Override
	public boolean hasCraftingRemainingItem() {
		return this.craftingRemaining;
	}


	public BaseUpgradeItem() {
		this(64, null);
	}
}
