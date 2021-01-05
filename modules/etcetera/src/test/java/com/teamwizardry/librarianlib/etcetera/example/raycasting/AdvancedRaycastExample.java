package com.teamwizardry.librarianlib.etcetera.example.raycasting;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.etcetera.Raycaster;

public class AdvancedRaycastExample {
    // note: Raycaster is *not* thread-safe, though world should only be
    // accessed from the main thread anyway.
    private static Raycaster raycaster = new Raycaster();
    private static Predicate<Entity> isPlayerPredicate =
            (entity) -> entity instanceof PlayerEntity;

    public void advancedRaycast(Entity entity) {
        double rayLength = 100;
        Vec3d start = entity.getCameraPosVec(0);
        Vec3d look = entity.getRotationVector();
        look = new Vec3d(
                look.getX() * rayLength,
                look.getY() * rayLength,
                look.getZ() * rayLength
        );

        // cast the ray
        raycaster.cast(entity.getEntityWorld(),
                Raycaster.BlockMode.VISUAL,
                Raycaster.FluidMode.SOURCE,
                isPlayerPredicate,
                start.getX(), start.getY(), start.getZ(),
                start.getX() + look.getX(),
                start.getY() + look.getY(),
                start.getZ() + look.getZ()
        );

        // get the result out of it

        if(raycaster.getHitType() == Raycaster.HitType.NONE) {
            return;
        }

        // the fraction along the raycast that the hit occurred
        double distance = raycaster.getFraction() * rayLength;

        // normal and hit position apply to all the hit types
        Vec3d normal = new Vec3d(
                raycaster.getNormalX(),
                raycaster.getNormalY(),
                raycaster.getNormalZ()
        );
        Vec3d hitPos = new Vec3d(
                raycaster.getHitX(),
                raycaster.getHitY(),
                raycaster.getHitZ()
        );

        switch (raycaster.getHitType()) {
            // block and fluid hits both have block positions
            case BLOCK:
            case FLUID:
                BlockPos hitBlock = new BlockPos(
                        raycaster.getBlockX(),
                        raycaster.getBlockY(),
                        raycaster.getBlockZ()
                );
                break;
            // entity hits have the entity that was hit
            case ENTITY:
                Entity hitEntity = raycaster.getEntity();
                break;
        }

        // it is VITALLY important that you do this
        raycaster.reset();
    }
}
