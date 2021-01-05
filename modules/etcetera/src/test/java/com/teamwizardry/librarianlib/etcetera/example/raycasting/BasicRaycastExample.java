package com.teamwizardry.librarianlib.etcetera.example.raycasting;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.etcetera.Raycaster;
import org.jetbrains.annotations.Nullable;

public class BasicRaycastExample {
    // note: Raycaster is *not* thread-safe, though world should only be
    // accessed from the main thread anyway.
    private static Raycaster raycaster = new Raycaster();

    @Nullable
    public BlockPos basicBlockRaycast(Entity entity) {
        Vec3d start = entity.getCameraPosVec(0);
        Vec3d look = entity.getRotationVector();
        look = new Vec3d(
                look.getX() * 100,
                look.getY() * 100,
                look.getZ() * 100
        );

        // cast the ray
        raycaster.cast(entity.getEntityWorld(), Raycaster.BlockMode.VISUAL,
                start.getX(), start.getY(), start.getZ(),
                start.getX() + look.getX(),
                start.getY() + look.getY(),
                start.getZ() + look.getZ()
        );

        // get the result out of it
        BlockPos result = null;
        if (raycaster.getHitType() == Raycaster.HitType.BLOCK) {
            result = new BlockPos(
                    raycaster.getBlockX(),
                    raycaster.getBlockY(),
                    raycaster.getBlockZ()
            );
        }

        // it is VITALLY important that you do this
        raycaster.reset();
        return result;
    }
}
