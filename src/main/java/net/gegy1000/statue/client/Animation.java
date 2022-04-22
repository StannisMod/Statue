package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.ArrayList;
import java.util.List;

public class Animation {

    private int timeGlobal;
    private int timeLeft;
    private int duration;
    private int index;

    private final List<TabulaAnimationComponentContainer> components;
    private final boolean doesLoop;

    public Animation(List<TabulaAnimationComponentContainer> components, boolean doesLoop) {
        this.timeLeft = -1;
        this.index = -1;
        this.components = new ArrayList<>();
        this.components.addAll(components);
        this.doesLoop = doesLoop;
        tick();
    }

    /**
     * Performs the tick of animation
     * @return true if animation is ended
     */
    public boolean tick() {
        if (timeLeft == -1 || timeLeft == duration) {
            index++;
            timeLeft = 0;
            if (index >= components.size()) {
                if (doesLoop && !components.isEmpty()) {
                    index = 0;
                } else {
                    timeGlobal = 0;
                    return true;
                }
            }
            duration = components.get(index).getLength();
        } else {
            if (canPlayCurrentComponent()) {
                timeLeft++;
            }
            timeGlobal++;
        }
        return false;
    }

    private boolean canPlayCurrentComponent() {
        if (index < 0) {
            return false;
        }
        TabulaAnimationComponentContainer c = components.get(index);
        return c.getStartKey() <= timeGlobal && timeGlobal <= c.getEndKey();
    }

    public void stop() {
        index = -1;
        timeLeft = -1;
        components.clear();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public TabulaAnimationComponentContainer getCurrentComponent() {
        if (!canPlayCurrentComponent()) {
            return null;
        }
        return components.get(index);
    }
}
