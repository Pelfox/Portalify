package dev.pelfox.portalify.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for particle-related operations. This class provides methods
 * to draw particles in various shapes and patterns.
 */
public final class ParticleUtils {
    /**
     * Draws a circle of particles at the specified location.
     *
     * @param stepParticleCount the number of particles to use for each step of the circle
     * @param particleCount the number of particles to spawn at each step
     * @param particle the type of particle to spawn
     * @param radius the radius of the circle
     * @param initialLocation the center location of the circle
     */
    public static void drawCircle(int stepParticleCount, int particleCount, @NotNull Particle particle, double radius, @NotNull Location initialLocation) {
        double angleStep = 2 * Math.PI / stepParticleCount;
        for (double i = 0; i < 2 * Math.PI; i += angleStep) {
            double x = radius * Math.cos(i);
            double z = radius * Math.sin(i);
            Location location = initialLocation.clone().add(x + 0.5, 1, z + 0.5);
            initialLocation.getWorld().spawnParticle(particle, location, particleCount);
        }
    }
}
