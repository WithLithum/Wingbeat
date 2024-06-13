package x.withlithum.wingbeat.mixins;

import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import x.withlithum.wingbeat.Wingbeat;

import java.io.IOException;

@Mixin(MinecraftServer.class)
public class DedicatedWorldSaveMixin {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger(DedicatedWorldSaveMixin.class);

    @Inject(at = @At("TAIL"), method = "saveAllChunks")
    public void onSaveAllChunks(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        LOGGER.info("Saving project");
        try {
            var project = Wingbeat.projectManager().getCurrentProject();
            if (project != null) {
                project.store();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save current project", e);
        }
    }
}
