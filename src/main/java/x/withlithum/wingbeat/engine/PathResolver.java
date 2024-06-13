package x.withlithum.wingbeat.engine;

import net.minecraft.resources.ResourceLocation;

import java.io.File;

public final class PathResolver {
    private PathResolver() {
        throw new AssertionError("No PathResolver instances for you!");
    }

    /**
     * Resolves the path part of a resource location to a file system path, relative to the
     * root of the data pack.
     *
     * @param path The path.
     * @return The resolved system path.
     * @throws IllegalArgumentException The path specified is not a valid path.
     */
    public static String resolvePath(String path) {
        if (!ResourceLocation.isValidPath(path)) {
            throw new IllegalArgumentException("The path is invalid.");
        }

        return path.replace("/", File.separator);
    }
}
