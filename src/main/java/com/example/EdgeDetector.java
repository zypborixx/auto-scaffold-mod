package com.example.autoscaffold;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EdgeDetector {
    // Player bounding box is typically 0.6 wide, 1.8 tall
    private static final double PLAYER_WIDTH = 0.6;

    public static boolean isNearEdge(LocalPlayer player, Vec3 moveDir, double lookahead) {
        AABB currentBox = player.getBoundingBox();

        // Project the box forward along the movement vector
        AABB projected = currentBox.move(
            moveDir.x * lookahead,
            0,
            moveDir.z * lookahead
        );

        // Check the block(s) directly beneath the projected box, at foot level
        BlockPos footPos = new BlockPos(
            Mth.floor(projected.minX + PLAYER_WIDTH / 2),
            Mth.floor(player.getY() - 0.1), // just below feet
            Mth.floor(projected.minZ + PLAYER_WIDTH / 2)
        );

        BlockState state = player.level().getBlockState(footPos);
        VoxelShape shape = state.getCollisionShape(player.level(), footPos);

        // No collision shape under projected position = edge/air
        return shape.isEmpty();
    }

    public static Vec3 getInputDirection(LocalPlayer player) {
        float forward = player.input.forwardImpulse; // -1 (S) to 1 (W)
        float strafe = player.input.leftImpulse;     // -1 (D) to 1 (A)

        if (forward == 0 && strafe == 0) {
            return Vec3.ZERO;
        }

        // Normalize diagonal input so S+D isn't faster than S alone
        float length = Mth.sqrt(forward * forward + strafe * strafe);
        if (length < 1.0F) length = 1.0F;

        forward /= length;
        strafe /= length;

        float yaw = player.getYRot();
        float yawRad = yaw * ((float) Math.PI / 180F);

        double sin = Mth.sin(yawRad);
        double cos = Mth.cos(yawRad);

        // Rotate local input into world space
        double worldX = strafe * cos - forward * sin;
        double worldZ = forward * cos + strafe * sin;

        return new Vec3(worldX, 0, worldZ).normalize();
    }

    public static boolean canScaffoldUp(LocalPlayer player, BlockPos targetPos) {
        int footY = Mth.floor(player.getY());
        int targetY = targetPos.getY();

        // Target is exactly one block higher than current foot level
        if (targetY != footY + 1) return false;

        // Check player has room to stand (no block in their head space at new height)
        BlockPos headCheck = targetPos.above();
        BlockState headState = player.level().getBlockState(headCheck);

        return headState.getCollisionShape(player.level(), headCheck).isEmpty();
    }
}
