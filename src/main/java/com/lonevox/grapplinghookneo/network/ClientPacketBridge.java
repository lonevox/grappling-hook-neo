package com.lonevox.grapplinghookneo.network;

public interface ClientPacketBridge {
    void handleGrappleAttach(GrappleAttachMessage message);

    void handleGrappleAttachPos(GrappleAttachPosMessage message);

    void handleSegment(SegmentMessage message);

    void handleDetachSingleHook(DetachSingleHookMessage message);

    void handleGrappleDetach(GrappleDetachMessage message);
}
