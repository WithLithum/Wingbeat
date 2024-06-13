package x.withlithum.wingbeat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import x.withlithum.wingbeat.commands.WingbeatCommands;
import x.withlithum.wingbeat.server.ide.PackProjectManager;

public class Wingbeat implements ModInitializer {
    private static PackProjectManager projectManager;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(WingbeatCommands::registerHandler);
        ServerWorldEvents.LOAD.register((server, world) -> {
            projectManager = new PackProjectManager(world);
        });
    }

    public static PackProjectManager projectManager() {
        return projectManager;
    }
}
