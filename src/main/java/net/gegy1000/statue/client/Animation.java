package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.List;

public class Animation {

    private int timeLeft;
    private int duration;
    private int index;
    private final List<TabulaAnimationComponentContainer> components;

    public Animation(List<TabulaAnimationComponentContainer> components) {
        this.timeLeft = -1;
        this.index = -1;
        this.components = components;
        tick();
    }

    /**
     * Performs the tick of animation
     * @return true if animation is ended
     */
    public boolean tick() {
        timeLeft++;
        if (timeLeft == duration) {
            index++;
            if (index < components.size()) {
                timeLeft = 0;
                duration = components.get(index).getLength();
            } else {
                return true;
            }
        }
        return false;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public TabulaAnimationComponentContainer getCurrentComponent() {
        return components.get(index);
    }
}
