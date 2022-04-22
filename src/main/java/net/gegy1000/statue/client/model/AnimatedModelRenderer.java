package net.gegy1000.statue.client.model;

import net.gegy1000.statue.client.Animation;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.minecraft.client.renderer.GlStateManager;

public class AnimatedModelRenderer extends OutlinedModelRenderer {

    private AnimatedModelRenderer snapshot;
    private float lastSeenTime = -1;

    private float opacity;
    private boolean hidden;

    private final String identifier;

    public AnimatedModelRenderer(final OutlinedTabulaModel model, final String name, final String identifier, final int textureX, final int textureY, final float opacity) {
        super(model, name, textureX, textureY);
        this.opacity = opacity;
        this.identifier = identifier;
    }

    @Override
    public OutlinedTabulaModel getModel() {
        return (OutlinedTabulaModel) super.getModel();
    }

    private void updateSnapshot() {
        if (snapshot == null) {
            snapshot = new AnimatedModelRenderer(getModel(), "", "", 0, 0, 0.0F);
        }
        snapshot.rotateAngleX = this.rotateAngleX;
        snapshot.rotateAngleY = this.rotateAngleY;
        snapshot.rotateAngleZ = this.rotateAngleZ;

        snapshot.rotationPointX = this.rotationPointX;
        snapshot.rotationPointY = this.rotationPointY;
        snapshot.rotationPointZ = this.rotationPointZ;

        snapshot.defaultOffsetX = this.defaultOffsetX;
        snapshot.defaultOffsetY = this.defaultOffsetY;
        snapshot.defaultOffsetZ = this.defaultOffsetZ;

        snapshot.defaultPositionX = this.defaultPositionX;
        snapshot.defaultPositionY = this.defaultPositionY;
        snapshot.defaultPositionZ = this.defaultPositionZ;

        snapshot.offsetX = this.offsetX;
        snapshot.offsetY = this.offsetY;
        snapshot.offsetZ = this.offsetZ;

        snapshot.scaleX = this.scaleX;
        snapshot.scaleY = this.scaleY;
        snapshot.scaleZ = this.scaleZ;

        snapshot.hidden = this.hidden;
        snapshot.opacity = this.opacity;
    }

    public void transitionUsing(final TabulaAnimationComponentContainer to, final float timer, final float maxTime) {
        if (lastSeenTime == -1 || lastSeenTime > timer) {
            // now we are starting new frame
            updateSnapshot();
        }
        lastSeenTime = timer;

        this.rotateAngleX = (float) Math.toRadians(snapshot.rotateAngleX + (float) to.getRotationChange()[0] * timer / maxTime);
        this.rotateAngleY = (float) Math.toRadians(snapshot.rotateAngleX + (float) to.getRotationChange()[1] * timer / maxTime);
        this.rotateAngleZ = (float) Math.toRadians(snapshot.rotateAngleX + (float) to.getRotationChange()[2] * timer / maxTime);

        this.rotationPointX = (float) to.getRotationOffset()[0] / 16;
        this.rotationPointY = (float) to.getRotationOffset()[1] / 16;
        this.rotationPointZ = (float) to.getRotationOffset()[2] / 16;

        this.offsetX = (snapshot.offsetX + snapshot.defaultPositionX + (float) to.getPositionChange()[0] * timer / maxTime) / 16;
        this.offsetY = (snapshot.offsetY + snapshot.defaultPositionY + (float) to.getPositionChange()[1] * timer / maxTime) / 16;
        this.offsetZ = (snapshot.offsetZ + snapshot.defaultPositionZ + (float) to.getPositionChange()[2] * timer / maxTime) / 16;

        this.defaultOffsetX = (snapshot.defaultOffsetX + (float) to.getPositionOffset()[0] * timer / maxTime) / 16;
        this.defaultOffsetY = (snapshot.defaultOffsetY + (float) to.getPositionOffset()[1] * timer / maxTime) / 16;
        this.defaultOffsetZ = (snapshot.defaultOffsetZ + (float) to.getPositionOffset()[2] * timer / maxTime) / 16;

        this.scaleX = snapshot.scaleX + (float) to.getScaleChange()[0] * timer / maxTime;
        this.scaleY = snapshot.scaleY + (float) to.getScaleChange()[1] * timer / maxTime;
        this.scaleZ = snapshot.scaleZ + (float) to.getScaleChange()[2] * timer / maxTime;

        this.opacity = snapshot.opacity + (float) to.getOpacityChange() * timer / maxTime;
        this.isHidden = to.isHidden();
    }

    @Override
    public void render(final float scale) {
        GlStateManager.color(opacity / 100.0F, opacity / 100.0F, opacity / 100.0F, 1.0F);
        //GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (getModel().pos != null) {
            for (String animId : getModel().animations.keySet()) {
                Animation animation = getModel().controller.getAnimation(getModel().pos, animId, identifier);
                if (animation == null) {
                    continue;
                }
                TabulaAnimationComponentContainer c = animation.getCurrentComponent();
                if (c == null) {
                    continue;
                }

                // apply changes
                this.transitionUsing(c, animation.getTimeLeft(), c.getLength());
            }
        }
        super.render(scale);
    }

    @Override
    public void resetToDefaultPose() {
        super.resetToDefaultPose();
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
        this.scaleZ = 1.0F;
        this.hidden = false;
        this.opacity = 100.0F;
        lastSeenTime = -1;
    }
}
