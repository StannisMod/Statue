package net.gegy1000.statue.client;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;

import java.util.ArrayList;
import java.util.List;

public class MutableTabulaAnimationComponentContainer extends TabulaAnimationComponentContainer {
    protected String name;
    protected String identifier;

    protected int startKey;
    protected int length;

    protected double[] posChange = new double[3];
    protected double[] rotChange = new double[3];
    protected double[] scaleChange = new double[3];
    protected double opacityChange;

    protected double[] posOffset = new double[3];
    protected double[] rotOffset = new double[3];
    protected double[] scaleOffset = new double[3];
    protected double opacityOffset;

    protected List<double[]> progressionCoords = new ArrayList<>();

    protected boolean hidden;

    public MutableTabulaAnimationComponentContainer() {}

    public MutableTabulaAnimationComponentContainer(final String name, final String identifier, final int startKey, final int length, final double[] posChange, final double[] rotChange, final double[] scaleChange, final double opacityChange, final double[] posOffset, final double[] rotOffset, final double[] scaleOffset, final double opacityOffset, final List<double[]> progressionCoords, final boolean hidden) {
        this.name = name;
        this.identifier = identifier;
        this.startKey = startKey;
        this.length = length;
        this.posChange = posChange;
        this.rotChange = rotChange;
        this.scaleChange = scaleChange;
        this.opacityChange = opacityChange;
        this.posOffset = posOffset;
        this.rotOffset = rotOffset;
        this.scaleOffset = scaleOffset;
        this.opacityOffset = opacityOffset;
        this.progressionCoords = progressionCoords;
        this.hidden = hidden;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getStartKey() {
        return this.startKey;
    }

    @Override
    public int getEndKey() {
        return this.startKey + this.length;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public double[] getPositionChange() {
        return this.posChange;
    }

    @Override
    public double[] getRotationChange() {
        return this.rotChange;
    }

    @Override
    public double[] getScaleChange() {
        return this.scaleChange;
    }

    @Override
    public double getOpacityChange() {
        return this.opacityChange;
    }

    @Override
    public double[] getPositionOffset() {
        return this.posOffset;
    }

    @Override
    public double[] getRotationOffset() {
        return this.rotOffset;
    }

    @Override
    public double[] getScaleOffset() {
        return this.scaleOffset;
    }

    @Override
    public double getOpacityOffset() {
        return this.opacityOffset;
    }

    @Override
    public List<double[]> getProgressionCoords() {
        return this.progressionCoords;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }
}