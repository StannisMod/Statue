package net.gegy1000.statue.client;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.message.CommandPacket;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.HashMap;
import java.util.Map;

public class Animation {

    protected final Map<String, AnimationComponent> components = new HashMap<>();
    private final boolean doesLoop;
    private final int endFrame;
    private CommandPacket.Type commandType;
    private String command;

    private int timeGlobal = -1;

    public Animation(Map<String, AnimationComponent> components, boolean doesLoop) {
        this.components.putAll(components);
        this.components.values().forEach(c -> c.setParent(this));
        this.doesLoop = doesLoop;
        this.endFrame = components.values().stream().flatMapToInt(c ->
                c.components.stream().mapToInt(TabulaAnimationComponentContainer::getEndKey)).max().orElse(0);
    }

    public void tick() {
        if (timeGlobal >= endFrame) {
            if (!command.isEmpty()) {
                Statue.WRAPPER.sendToServer(new CommandPacket(commandType, command));
            }
            if (doesLoop) {
                timeGlobal = -1;
                this.components.values().forEach(AnimationComponent::loop);
            } else {
                this.components.clear();
                return;
            }
        }

        timeGlobal++;
        this.components.values().forEach(c -> c.tick(timeGlobal));
    }

    public int getTimeGlobal() {
        return timeGlobal;
    }

    public boolean shouldLoadIdentity() {
        return timeGlobal == 0;
    }

    public void setLoopingCommand(CommandPacket.Type type, String command) {
        this.commandType = type;
        this.command = command;
    }
}
