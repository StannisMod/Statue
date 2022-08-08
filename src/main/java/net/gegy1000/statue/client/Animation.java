package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.HashMap;
import java.util.Map;

public class Animation {

    protected final Map<String, AnimationComponent> components = new HashMap<>();
    private final boolean doesLoop;
    private final int endFrame;

    private int timeGlobal = -1;

    public Animation(Map<String, AnimationComponent> components, boolean doesLoop) {
        this.components.putAll(components);
        this.components.values().forEach(c -> c.setParent(this));
        this.doesLoop = doesLoop;
        this.endFrame = components.values().stream().flatMapToInt(c ->
                c.components.stream().mapToInt(TabulaAnimationComponentContainer::getEndKey)).max().orElse(0);
    }

    public void tick() {
        if (doesLoop && timeGlobal == endFrame) {
            timeGlobal = -1;
            this.components.values().forEach(AnimationComponent::loop);
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
}
