package x.withlithum.wingbeat.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class WingbeatCommands {
    private WingbeatCommands() {
        throw new AssertionError("No WingbeatCommands instances for you!");
    }

    public static void registerHandler(CommandDispatcher<CommandSourceStack> dispatcher,
                                       CommandBuildContext context,
                                       Commands.CommandSelection selection) {
        IdeCommand.register(dispatcher, context);
    }
}
