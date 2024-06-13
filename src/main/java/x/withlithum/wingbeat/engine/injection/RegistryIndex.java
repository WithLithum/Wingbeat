package x.withlithum.wingbeat.engine.injection;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public final class RegistryIndex {
    private RegistryIndex() {
        throw new AssertionError("No RegistryIndex instances for you!");
    }

    private static final Set<ResourceLocation> REGISTRIES = new HashSet<>(202);

    public static int size() {
        return REGISTRIES.size();
    }

    public static void forEach(Consumer<ResourceLocation> consumer) {
        REGISTRIES.forEach(consumer);
    }

    /**
     * Checks if a registry with the specified identifier exists.
     * @param id The identifier.
     * @return The registry.
     */
    public static boolean contains(ResourceLocation id) {
        return REGISTRIES.contains(id);
    }

    /**
     * Appends a registry to the list of known registries.
     *
     * <p>
     * This API is an internal implementation detail and should not be called by
     * external code. Registries should be registered via the usual methods for vanilla registration, where a mixin
     * will automatically handle Wingbeat usage for you.
     * </p>
     *
     * <p>
     * The consequences could be from incorrect feedback of a non-existent registry that exists, to unexplainable
     * crashes.
     * </p>
     *
     * @param id The identifier.
     */
    @ApiStatus.Internal
    public static void appendRegistry(ResourceLocation id) {
        if (REGISTRIES.contains(id)) {
            return;
        }

        REGISTRIES.add(id);
    }
}
