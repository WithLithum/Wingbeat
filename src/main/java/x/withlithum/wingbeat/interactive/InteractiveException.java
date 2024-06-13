package x.withlithum.wingbeat.interactive;

import net.minecraft.network.chat.Component;

public class InteractiveException extends RuntimeException {
    public InteractiveException(Component userMessage) {
        super("Interactive error occurred.");
        this.userMessage = userMessage;
    }

    public InteractiveException(Component userMessage, Throwable inner) {
        super("Interactive error occurred.", inner);
        this.userMessage = userMessage;
    }

    public final Component userMessage;
}
