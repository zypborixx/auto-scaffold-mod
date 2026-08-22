package com.example.mixin;

import com.example.EdgeDetector;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class AutoScaffoldMixin {

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayer(CallbackInfo ci) {
        EntityPlayerSP self = (EntityPlayerSP) (Object) this;

        Vec3 moveDir = EdgeDetector.getInputDirection(self);
        if (moveDir.lengthVector() < 0.01) return;

        boolean nearEdge = EdgeDetector.isNearEdge(self, moveDir, 0.3);

        if (nearEdge) {
            // Placement/edge logic
        }
    }
}
