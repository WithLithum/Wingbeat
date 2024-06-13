package x.withlithum.wingbeat.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import x.withlithum.wingbeat.Wingbeat;
import x.withlithum.wingbeat.client.WingbeatToasts;

import java.io.IOException;

@Mixin(IntegratedServer.class)
public class IntegratedWorldSaveMixin {
    @Shadow @Final private Minecraft minecraft;

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegratedWorldSaveMixin.class);

    @Inject(at = @At("HEAD"), method = "stopServer")
    public void onStopServer(CallbackInfo ci) {
        LOGGER.info("Responding to server stopping call");

        saveInteractive();
    }

    @Unique
    private void saveInteractive() {
        var project = Wingbeat.projectManager().getCurrentProject();
        if (project != null) {
            try {
                project.store();
                LOGGER.info("Current project was saved");
            } catch (IOException e) {
                WingbeatToasts.onProjectSaveFailure(minecraft, project.metadata.name);
                LOGGER.error("Failed to save current project", e);
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "saveEverything")
    public void onSaveEverything(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        LOGGER.info("Saving project");

        saveInteractive();
    }
}
