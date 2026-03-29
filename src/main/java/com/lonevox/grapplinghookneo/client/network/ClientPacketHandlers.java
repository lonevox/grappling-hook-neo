package com.lonevox.grapplinghookneo.client.network;

import com.lonevox.grapplinghookneo.client.ClientControllerManager;
import com.lonevox.grapplinghookneo.client.ClientProxyInterface;
import com.lonevox.grapplinghookneo.entities.grapplehook.GrapplehookEntity;
import com.lonevox.grapplinghookneo.entities.grapplehook.SegmentHandler;
import com.lonevox.grapplinghookneo.network.DetachSingleHookMessage;
import com.lonevox.grapplinghookneo.network.ClientPacketBridge;
import com.lonevox.grapplinghookneo.network.GrappleAttachMessage;
import com.lonevox.grapplinghookneo.network.GrappleAttachPosMessage;
import com.lonevox.grapplinghookneo.network.GrappleDetachMessage;
import com.lonevox.grapplinghookneo.network.SegmentMessage;
import com.lonevox.grapplinghookneo.utils.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandlers implements ClientPacketBridge {
    public ClientPacketHandlers() {
    }

    @Override
    public void handleGrappleAttach(GrappleAttachMessage message) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        Entity grapple = world.getEntity(message.id);
        if (grapple instanceof GrapplehookEntity grapplehookEntity) {
            grapplehookEntity.clientAttach(message.x, message.y, message.z);
            SegmentHandler segmenthandler = grapplehookEntity.segmentHandler;
            segmenthandler.segments = message.segments;
            segmenthandler.segmentBottomSides = message.segmentBottomSides;
            segmenthandler.segmentTopSides = message.segmentTopSides;

            Entity player = world.getEntity(message.entityId);
            segmenthandler.forceSetPos(new Vec(message.x, message.y, message.z), Vec.positionVec(player));
        }

        ClientProxyInterface.proxy.createControl(message.controlId, message.id, message.entityId, world, new Vec(message.x, message.y, message.z), message.blockPos, message.custom);
    }

    @Override
    public void handleGrappleAttachPos(GrappleAttachPosMessage message) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        Entity grapple = world.getEntity(message.id);
        if (grapple instanceof GrapplehookEntity grapplehookEntity) {
            grapplehookEntity.setAttachPos(message.x, message.y, message.z);
        }
    }

    @Override
    public void handleSegment(SegmentMessage message) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        Entity grapple = world.getEntity(message.id);
        if (!(grapple instanceof GrapplehookEntity grapplehookEntity)) {
            return;
        }

        SegmentHandler segmenthandler = grapplehookEntity.segmentHandler;
        if (message.add) {
            segmenthandler.actuallyAddSegment(message.index, message.pos, message.bottomFacing, message.topFacing);
        } else {
            segmenthandler.removeSegment(message.index);
        }
    }

    @Override
    public void handleDetachSingleHook(DetachSingleHookMessage message) {
        ClientControllerManager.receiveGrappleDetachHook(message.id, message.hookid);
    }

    @Override
    public void handleGrappleDetach(GrappleDetachMessage message) {
        ClientControllerManager.receiveGrappleDetach(message.id);
    }
}
