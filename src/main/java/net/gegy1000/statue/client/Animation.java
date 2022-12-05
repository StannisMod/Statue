package net.gegy1000.statue.client;

import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.message.FunctionPacket;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.HashMap;
import java.util.Map;

public class Animation {

    protected final Map<String, AnimationComponent> components = new HashMap<>();
    private final int endFrame;
    private final boolean doesLoop;
    private final boolean finite;
    private FunctionPacket.Type commandType;
    private String command;

    private int timeGlobal = -1;
    private int loops;

    public Animation(Map<String, AnimationComponent> components, boolean doesLoop, int loops) {
        this.components.putAll(components);
        this.components.values().forEach(c -> c.setParent(this));
        this.endFrame = components.values().stream().flatMapToInt(c ->
                c.components.stream().mapToInt(TabulaAnimationComponentContainer::getEndKey)).max().orElse(0);
        this.doesLoop = doesLoop;
        this.loops = loops;
        this.finite = loops != 0;
    }

    public void tick() {
        if (timeGlobal >= endFrame) {
            if (!command.isEmpty()) {
                Statue.WRAPPER.sendToServer(new FunctionPacket(commandType, command));
            }
            if ((!finite && doesLoop) || (finite && --loops > 0)) {
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

    public void setLoopingCommand(FunctionPacket.Type type, String command) {
        this.commandType = type;
        this.command = command;
    }
}
