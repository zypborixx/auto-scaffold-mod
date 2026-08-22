package com.example;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EdgeDetector {

    public static Vec3 getInputDirection(EntityPlayerSP player) {
        float forward = player.movementInput.moveForward;
        float strafe = player.movementInput.moveStrafe;

        if (forward == 0 && strafe == 0) {
            return new Vec3(0, 0, 0);
        }

        float yaw = player.rotationYaw;
        float yawRad = yaw * ((float) Math.PI / 180F);

        double sin = MathHelper.sin(yawRad);
        double cos = MathHelper.cos(yawRad);

        double worldX = strafe * cos - forward * sin;
        double worldZ = forward * cos + strafe * sin;

        return new Vec3(worldX, 0, worldZ).normalize();
    }

    public static boolean isNearEdge(EntityPlayerSP player, Vec3 moveDir, double lookahead) {
        double checkX = player.posX + (moveDir.xCoord * lookahead);
        double checkY = player.posY - 0.5;
        double checkZ = player.posZ + (moveDir.zCoord * lookahead);

        BlockPos checkPos = new BlockPos(checkX, checkY, checkZ);
        return player.worldObj.isAirBlock(checkPos);
    }
}
