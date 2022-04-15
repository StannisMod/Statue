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
        // make the reversed animation component
        MutableTabulaAnimationComponentContainer pathToOrigin = new MutableTabulaAnimationComponentContainer();
        pathToOrigin.startKey = this.components.get(this.components.size() - 1).getEndKey() + 1;
        pathToOrigin.length = 10;
        for (TabulaAnimationComponentContainer c : components) {
            pathToOrigin.opacityChange -= c.getOpacityChange();
            pathToOrigin.opacityOffset -= c.getOpacityOffset();
            pathToOrigin.posChange[0] -= c.getPositionChange()[0];
            pathToOrigin.posChange[1] -= c.getPositionChange()[1];
            pathToOrigin.posChange[2] -= c.getPositionChange()[2];
            pathToOrigin.posOffset[0] -= c.getPositionOffset()[0];
            pathToOrigin.posOffset[1] -= c.getPositionOffset()[1];
            pathToOrigin.posOffset[2] -= c.getPositionOffset()[2];
            pathToOrigin.rotChange[0] -= c.getRotationChange()[0];
            pathToOrigin.rotChange[1] -= c.getRotationChange()[1];
            pathToOrigin.rotChange[2] -= c.getRotationChange()[2];
            pathToOrigin.rotOffset[0] -= c.getRotationOffset()[0];
            pathToOrigin.rotOffset[1] -= c.getRotationOffset()[1];
            pathToOrigin.rotOffset[2] -= c.getRotationOffset()[2];
            pathToOrigin.scaleChange[0] -= c.getScaleChange()[0];
            pathToOrigin.scaleChange[1] -= c.getScaleChange()[1];
            pathToOrigin.scaleChange[2] -= c.getScaleChange()[2];
            pathToOrigin.scaleOffset[0] -= c.getScaleOffset()[0];
            pathToOrigin.scaleOffset[1] -= c.getScaleOffset()[1];
            pathToOrigin.scaleOffset[2] -= c.getScaleOffset()[2];
        }
        this.components.add(pathToOrigin);
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
            timeLeft = 0;
            if (index >= components.size()) {
                if (doesLoop && !components.isEmpty()) {
                    index = 0;
                } else {
                    return true;
                }
            }
            duration = components.get(index).getLength();
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
