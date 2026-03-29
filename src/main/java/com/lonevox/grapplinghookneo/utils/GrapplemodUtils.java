package com.lonevox.grapplinghookneo.utils;

import com.lonevox.grapplinghookneo.common.NetworkSetup;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

public class GrapplemodUtils {
	public static void sendToCorrectClient(CustomPacketPayload message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer player) {
			NetworkSetup.sendToPlayer(player, message);
		} else {
			System.out.println("ERROR! couldn't find player");
		}
	}

	public static BlockHitResult rayTraceBlocks(Level world, Vec from, Vec to) {
		HitResult result = world.clip(new ClipContext(
				from.toVec3d(),
				to.toVec3d(),
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				CollisionContext.empty()));
		if (result instanceof BlockHitResult blockHit) {
			if (blockHit.getType() != HitResult.Type.BLOCK) {
				return null;
			}
			return blockHit;
		}
		return null;
	}

	public static long getTime(Level w) {
		return w.getGameTime();
	}

	private static int controllerid = 0;
	public static int GRAPPLEID = controllerid++;
	public static int REPELID = controllerid++;
	public static int AIRID = controllerid++;

}
