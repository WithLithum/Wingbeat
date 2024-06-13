package x.withlithum.wingbeat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public final class WingbeatToasts {
    private WingbeatToasts() {
        throw new AssertionError("No WingbeatToasts instances for you!");
    }

    public static final SystemToast.SystemToastId ID_PROJECT_SAVE_FAILURE = new SystemToast.SystemToastId();

    public static void onProjectSaveFailure(Minecraft minecraft, String name) {
        SystemToast.addOrUpdate(minecraft.getToasts(),
                ID_PROJECT_SAVE_FAILURE,
                Component.translatable("wingbeat.client.toast.project_save_failed.title",
                        name),
                Component.translatable("wingbeat.client.toast.project_save_failed.message"));
    }
}
