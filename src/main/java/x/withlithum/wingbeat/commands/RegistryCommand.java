package x.withlithum.wingbeat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import x.withlithum.wingbeat.engine.injection.RegistryIndex;
import x.withlithum.wingbeat.util.Counter;

import static net.minecraft.commands.Commands.*;

public final class RegistryCommand {
    private RegistryCommand() {
        throw new AssertionError("No RegistryCommand instances for you!");
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("registry")
                .then(literal("list")
                        .executes(context -> runList(0, context))
                        .then(argument("page", IntegerArgumentType.integer())
                                .executes(context -> runList(IntegerArgumentType.getInteger(context, "page") + 1,
                                        context)))));
    }

    private static int runList(int page, CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        source.sendSystemMessage(Component.translatable("wingbeat.commands.registry.list.begin")
                .withStyle(ChatFormatting.GREEN));
        source.sendSystemMessage(Component.literal("--------------------------------"));

        var skip = page * 20;
        var pageSize = page / 20;
        var counter = new Counter();
        var pageCounter = new Counter();

        RegistryIndex.forEach(x -> {
            counter.postIncrement();
            if (counter.count() < skip) {
                return;
            }

            if (pageCounter.postIncrement() > 20) {
                return;
            }

            source.sendSystemMessage(Component.literal("- ")
                    .append(Component.literal(x.toString())
                            .withStyle(ChatFormatting.GOLD)));
        });

        source.sendSystemMessage(Component.literal("--------------------------------"));
        source.sendSystemMessage(Component.translatable("wingbeat.commands.registry.list.end",
                page + 1,
                RegistryIndex.size() / 20));
        return 1;
    }
}
