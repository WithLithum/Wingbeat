package x.withlithum.wingbeat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import x.withlithum.wingbeat.Wingbeat;
import x.withlithum.wingbeat.interactive.InteractiveException;

import static net.minecraft.commands.Commands.*;

public final class IdeCommand {
    private IdeCommand() {
        throw new AssertionError("No IdeCommand instances for you!");
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(literal("ide")
                .requires(source -> source.hasPermission(2))
                .then(literal("pack")
                        .then(cmdNewPack(context))
                        .then(cmdOpenPack())
                        .then(cmdSavePack())
                        .then(cmdClosePack())));
    }

    // <editor-fold desc="/ide pack close">

    private static CommandNode<CommandSourceStack> cmdClosePack() {
        return literal("close")
                .executes(context -> runClosePack(false, true, context))
                .then(literal("force")
                        .executes(context -> runClosePack(true, true, context)))
                .then(literal("discard")
                        .executes(context -> runClosePack(true, false, context)))
                .build();
    }

    private static int runClosePack(boolean force, boolean attemptSave, CommandContext<CommandSourceStack> context) {
        if (Wingbeat.projectManager().getCurrentProject() == null) {
            context.getSource().sendFailure(Component.translatable("wingbeat.commands.ide.no_open_project"));
            return 0;
        }

        if (attemptSave) {
            try {
                Wingbeat.projectManager().saveProjectInteractive();
            } catch (InteractiveException e) {
                if (!force) {
                    context.getSource().sendFailure(e.userMessage);
                    return 0;
                }

                context.getSource().sendSystemMessage(Component.translatable("wingbeat.commands.ide.close.force_save_failed",
                        e.userMessage)
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

        Wingbeat.projectManager().closeProject();
        context.getSource().sendSuccess(() -> Component.translatable("wingbeat.commands.ide.close.success"),
                true);
        return 1;
    }

    // </editor-fold>

    private static CommandNode<CommandSourceStack> cmdSavePack() {
        return literal("save")
                .executes(context -> {
                   try {
                       Wingbeat.projectManager().saveProjectInteractive();
                   } catch (InteractiveException e) {
                       context.getSource().sendFailure(e.userMessage);
                       return 0;
                   }

                   return 1;
                })
                .build();
    }

    // <editor-fold desc="/ide pack open">

    private static CommandNode<CommandSourceStack> cmdOpenPack() {
        return literal("open")
                .then(argument("name", StringArgumentType.word())
                        .executes(IdeCommand::runOpenPack))
                .build();
    }

    private static int runOpenPack(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, "name");

        try {
            Wingbeat.projectManager().loadProjectInteractive(name);
        } catch (InteractiveException e) {
            context.getSource().sendFailure(e.userMessage);
            return 0;
        }

        return 1;
    }

    // </editor-fold>

    private static CommandNode<CommandSourceStack> cmdNewPack(CommandBuildContext context) {
        return literal("create")
                .then(argument("name", StringArgumentType.word())
                        .executes(IdeCommand::runNewPack)
                        .then(argument("displayName", ComponentArgument.textComponent(context))
                            .executes(IdeCommand::runNewPackWithDisplayName)))
                .build();
    }

    private static int runNewPackWithDisplayName(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, "name");
        final Component component = ComponentArgument.getComponent(context, "displayName");

        try {
            Wingbeat.projectManager().newProjectInteractive(name, component);
        } catch (InteractiveException e) {
            context.getSource().sendFailure(e.userMessage);
            return 0;
        }

        return 1;
    }

    private static int runNewPack(CommandContext<CommandSourceStack> context) {
        final String name = StringArgumentType.getString(context, "name");

        try {
            Wingbeat.projectManager().newProjectInteractive(name, null);
        } catch (InteractiveException e) {
            context.getSource().sendFailure(e.userMessage);
            return 0;
        }

        return 1;
    }
}
