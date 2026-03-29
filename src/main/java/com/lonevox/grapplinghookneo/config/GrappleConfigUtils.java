package com.lonevox.grapplinghookneo.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;

public class GrappleConfigUtils {
	private static boolean anyBlocks = true;
	private static HashSet<Block> grapplingBlocks;
	private static boolean removeBlocks = false;
	private static HashSet<Block> grapplingBreaksBlocks;
	private static boolean anyBreakBlocks = false;

	public static HashSet<Block> stringToBlocks(String s) {
		HashSet<Block> blocks = new HashSet<>();
		
		if (s.isEmpty() || s.equals("none") || s.equals("any")) {
			return blocks;
		}
		
		String[] blockstr = s.split(",");
		
	    for(String str:blockstr){
	    	str = str.trim();
	    	String modid;
	    	String name;
	    	if (str.contains(":")) {
	    		String[] splitstr = str.split(":");
	    		modid = splitstr[0];
	    		name = splitstr[1];
	    	} else {
	    		modid = "minecraft";
	    		name = str;
	    	}
	    	
	    	Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, name));
	    	
	    	blocks.add(b);
	    }
	    
	    return blocks;
	}
	
	public static void updateGrapplingBlocks() {
		String s = GrappleConfig.getConf().grapplinghook.blocks.grapplingBlocks;
		if (s.equals("any") || s.isEmpty()) {
			s = GrappleConfig.getConf().grapplinghook.blocks.grapplingNonBlocks;
			if (s.equals("none") || s.isEmpty()) {
				anyBlocks = true;
			} else {
				anyBlocks = false;
				removeBlocks = true;
			}
		} else {
			anyBlocks = false;
			removeBlocks = false;
		}
	
		if (!anyBlocks) {
			grapplingBlocks = stringToBlocks(s);
		}
		
		grapplingBreaksBlocks = stringToBlocks(GrappleConfig.getConf().grapplinghook.blocks.grappleBreakBlocks);
		anyBreakBlocks = !grapplingBreaksBlocks.isEmpty();
		
	}

	private static String prevGrapplingBlocks = null;
	private static String prevGrapplingNonBlocks = null;
	public static boolean attachesBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grapplingBlocks.equals(prevGrapplingBlocks) || !GrappleConfig.getConf().grapplinghook.blocks.grapplingNonBlocks.equals(prevGrapplingNonBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (anyBlocks) {
			return true;
		}
		
		boolean inlist = grapplingBlocks.contains(block);
		
		if (removeBlocks) {
			return !inlist;
		} else {
			return inlist;
		}
	}

	private static String prevGrapplingBreakBlocks = null;
	public static boolean breaksBlock(Block block) {
		if (!GrappleConfig.getConf().grapplinghook.blocks.grappleBreakBlocks.equals(prevGrapplingBreakBlocks)) {
			updateGrapplingBlocks();
		}
		
		if (!anyBreakBlocks) {
			return false;
		}
		
		return grapplingBreaksBlocks.contains(block);
	}
}
