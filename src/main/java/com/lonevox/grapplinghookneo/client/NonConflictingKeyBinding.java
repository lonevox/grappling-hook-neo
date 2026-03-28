package com.lonevox.grapplinghookneo.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class NonConflictingKeyBinding extends KeyMapping {
	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.setNonConflict();
	}

	private void setNonConflict() {
		this.setKeyConflictContext(new IKeyConflictContext() {
			@Override
			public boolean isActive() {
				// NeoForge key processing only updates active contexts.
				return true;
			}
			@Override
			public boolean conflicts(IKeyConflictContext other) {
				// Must conflict with IN_GAME so NONE-modifier keybinds stay active
				// while Shift/Ctrl/Alt are held (NeoForge KeyModifier.NONE behavior).
				return other == KeyConflictContext.IN_GAME;
			}
		});
	}

	public NonConflictingKeyBinding(String description, InputConstants.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
		this.setNonConflict();
	}

   public boolean same(KeyMapping p_197983_1_) {
	   return false;
   }
   
   public boolean isDown = false;
   
   public boolean isDown() {
	   return isDown;
   }
   
   @Override
   public void setDown(boolean value) {
	   this.isDown = value;
   }
}
