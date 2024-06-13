package x.withlithum.wingbeat.mixins;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import x.withlithum.wingbeat.engine.injection.RegistryIndex;

@Mixin(ResourceKey.class)
public class ResourceKeyMixin {
    @Inject(at = @At("HEAD"), method = "createRegistryKey")
    private static <T> void onCreateRegistryKey(ResourceLocation location, CallbackInfoReturnable<ResourceKey<Registry<T>>> cir) {
        RegistryIndex.appendRegistry(location);
    }
}
