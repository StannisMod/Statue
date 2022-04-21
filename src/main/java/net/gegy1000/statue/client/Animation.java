package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.ArrayList;
import java.util.List;

public class Animation {

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
                    return true;
                }
            }
            duration = components.get(index).getLength();
        } else {
            timeLeft++;
        }
        return false;
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
        if (index == -1) {
            return null;
        }
        return components.get(index);
    }
}
