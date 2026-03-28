package com.lonevox.grapplinghookneo.blocks.modifierblock;

import com.lonevox.grapplinghookneo.common.CommonSetup;
import com.lonevox.grapplinghookneo.common.NetworkSetup;
import com.lonevox.grapplinghookneo.network.GrappleModifierMessage;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization;
import com.lonevox.grapplinghookneo.utils.GrappleCustomization.upgradeCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileEntityGrappleModifier extends BlockEntity {
	public HashMap<GrappleCustomization.upgradeCategories, Boolean> unlockedCategories = new HashMap<GrappleCustomization.upgradeCategories, Boolean>();
	public GrappleCustomization customization;

	public TileEntityGrappleModifier(BlockPos pos, BlockState state) {
		super(CommonSetup.grappleModifierTileEntityType.get(),pos,state);
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.sendUpdates();
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		NetworkSetup.sendToServer(new GrappleModifierMessage(this.worldPosition, this.customization));
		this.sendUpdates();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.sendUpdates();
	}

	private void sendUpdates() {
		this.setChanged();
	}

	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}

	@Override
	protected void saveAdditional(CompoundTag nbtTagCompound, HolderLookup.Provider provider) {
		super.saveAdditional(nbtTagCompound, provider);

		CompoundTag unlockedNBT = nbtTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);

			unlockedNBT.putBoolean(num, unlocked);
		}

		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());
	}

	@Override
	protected void loadAdditional(CompoundTag parentNBTTagCompound, HolderLookup.Provider provider) {
		super.loadAdditional(parentNBTTagCompound, provider);

		CompoundTag unlockedNBT = parentNBTTagCompound.getCompound("unlocked");

		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);

			this.unlockedCategories.put(category, unlocked);
		}

		CompoundTag custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
	}


	// When the world loads from disk, the server needs to send the TileEntity information to the client
	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	//  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
	//  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
	//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider)
	{
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound, provider);
		return nbtTagCompound;
	}
}
