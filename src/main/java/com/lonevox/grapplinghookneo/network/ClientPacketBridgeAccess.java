package com.lonevox.grapplinghookneo.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

final class ClientPacketBridgeAccess {
    private static final ClientPacketBridge BRIDGE = createBridge();

    private ClientPacketBridgeAccess() {
    }

    static ClientPacketBridge get() {
        return BRIDGE;
    }

    private static ClientPacketBridge createBridge() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return new com.lonevox.grapplinghookneo.client.network.ClientPacketHandlers();
        }

        return new NoopClientPacketBridge();
    }

    private static final class NoopClientPacketBridge implements ClientPacketBridge {
        @Override
        public void handleGrappleAttach(GrappleAttachMessage message) {
        }

        @Override
        public void handleGrappleAttachPos(GrappleAttachPosMessage message) {
        }

        @Override
        public void handleSegment(SegmentMessage message) {
        }

        @Override
        public void handleDetachSingleHook(DetachSingleHookMessage message) {
        }

        @Override
        public void handleGrappleDetach(GrappleDetachMessage message) {
        }
    }
}
