package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.ArrayList;
import java.util.List;

public class AnimationComponent {

    private int timeLeft;
    private int duration;
    private int index;

    private Animation parent;

    protected final List<TabulaAnimationComponentContainer> components;

    public AnimationComponent(List<TabulaAnimationComponentContainer> components) {
        this.timeLeft = -1;
        this.index = -1;
        this.components = new ArrayList<>();
        this.components.addAll(components);
        tick(0);
    }

    /**
     * Performs the tick of animation
     * @return true if animation is ended
     */
    public boolean tick(int timeGlobal) {
        if (timeLeft == -1 || timeLeft == duration) {
            index++;
            timeLeft = 0;
            if (index >= components.size()) {
                return true;
            }
            duration = components.get(index).getLength();
        } else {
            if (canPlayCurrentComponent(timeGlobal)) {
                timeLeft++;
            }
        }
        return false;
    }

    private boolean canPlayCurrentComponent(int timeGlobal) {
        if (0 > index || index >= components.size()) {
            return false;
        }
        TabulaAnimationComponentContainer c = components.get(index);
        return c.getStartKey() <= timeGlobal && timeGlobal <= c.getEndKey();
    }

    protected void loop() {
        index = 0;
    }

    public void stop() {
        index = -1;
        timeLeft = -1;
        components.clear();
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setParent(final Animation parent) {
        this.parent = parent;
    }

    public TabulaAnimationComponentContainer getCurrentComponent() {
        if (!canPlayCurrentComponent(parent.getTimeGlobal())) {
            return null;
        }
        return components.get(index);
    }
}
