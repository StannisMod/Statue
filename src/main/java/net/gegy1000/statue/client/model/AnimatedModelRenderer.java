package net.gegy1000.statue.client.model;

import net.gegy1000.statue.client.Animation;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class AnimatedModelRenderer extends OutlinedModelRenderer {

    private AnimatedModelRenderer snapshot;
    private float lastSeenTime = -1;

    private float opacity;
    private final float defaultOpacity;
    private boolean hidden;

    private final String identifier;

    public AnimatedModelRenderer(final OutlinedTabulaModel model, final String name, final String identifier, final int textureX, final int textureY, final float opacity) {
        super(model, name, textureX, textureY);
        this.opacity = opacity;
        this.defaultOpacity = opacity;
        this.identifier = identifier;
    }

    @Override
    public OutlinedTabulaModel getModel() {
        return (OutlinedTabulaModel) super.getModel();
    }

    private void updateSnapshot(final TabulaAnimationComponentContainer anim) {
        if (snapshot == null) {
            snapshot = new AnimatedModelRenderer(getModel(), "", "", 0, 0, 0.0F);
        }
        snapshot.rotateAngleX = this.rotateAngleX;
        snapshot.rotateAngleY = this.rotateAngleY;
        snapshot.rotateAngleZ = this.rotateAngleZ;

        snapshot.defaultRotationX = this.defaultRotationX + (float) anim.getRotationOffset()[0];
        snapshot.defaultRotationY = this.defaultRotationY + (float) anim.getRotationOffset()[1];
        snapshot.defaultRotationZ = this.defaultRotationZ + (float) anim.getRotationOffset()[2];

        snapshot.rotationPointX = this.rotationPointX;
        snapshot.rotationPointY = this.rotationPointY;
        snapshot.rotationPointZ = this.rotationPointZ;

        snapshot.defaultOffsetX = this.defaultOffsetX;
        snapshot.defaultOffsetY = this.defaultOffsetY;
        snapshot.defaultOffsetZ = this.defaultOffsetZ;

        snapshot.defaultPositionX = this.defaultPositionX;
        snapshot.defaultPositionY = this.defaultPositionY;
        snapshot.defaultPositionZ = this.defaultPositionZ;

        snapshot.offsetX = this.offsetX + (float) anim.getPositionOffset()[0];
        snapshot.offsetY = this.offsetY + (float) anim.getPositionOffset()[1];
        snapshot.offsetZ = this.offsetZ + (float) anim.getPositionOffset()[2];

        snapshot.scaleX = this.scaleX;
        snapshot.scaleY = this.scaleY;
        snapshot.scaleZ = this.scaleZ;

        snapshot.hidden = this.hidden;
        snapshot.opacity = this.opacity + (float) anim.getOpacityOffset();
    }

    public void transitionUsing(final TabulaAnimationComponentContainer to, final float timer, final float maxTime, float partialTicks) {
        if (lastSeenTime == -1 || lastSeenTime > timer) {
            // now we are starting new frame
            updateSnapshot(to);
        }
        lastSeenTime = timer;

        this.rotateAngleX = animate(snapshot.rotateAngleX, (float) Math.toRadians(to.getRotationOffset()[0] + to.getRotationChange()[0]), timer, maxTime, partialTicks);
        this.rotateAngleY = animate(snapshot.rotateAngleY, (float) Math.toRadians(to.getRotationOffset()[1] + to.getRotationChange()[1]), timer, maxTime, partialTicks);
        this.rotateAngleZ = animate(snapshot.rotateAngleZ, (float) Math.toRadians(to.getRotationOffset()[2] + to.getRotationChange()[2]), timer, maxTime, partialTicks);

        this.rotationPointX = snapshot.rotationPointX;
        this.rotationPointY = snapshot.rotationPointY;
        this.rotationPointZ = snapshot.rotationPointZ;

        this.offsetX = animate(snapshot.offsetX, (float) to.getPositionChange()[0], timer, maxTime, partialTicks);
        this.offsetY = animate(snapshot.offsetY, (float) to.getPositionChange()[1], timer, maxTime, partialTicks);
        this.offsetZ = animate(snapshot.offsetZ, (float) to.getPositionChange()[2], timer, maxTime, partialTicks);

        this.scaleX = animate(snapshot.scaleX, (float) to.getScaleChange()[0], timer, maxTime, partialTicks);
        this.scaleY = animate(snapshot.scaleY, (float) to.getScaleChange()[1], timer, maxTime, partialTicks);
        this.scaleZ = animate(snapshot.scaleZ, (float) to.getScaleChange()[2], timer, maxTime, partialTicks);

        this.opacity = animate(snapshot.opacity, (float) to.getOpacityChange(), timer, maxTime, partialTicks);
        this.isHidden = to.isHidden();
    }

    private float animate(float origin, float change, float timer, float maxTime, float partialTicks) {
        float prevTimer = Math.max(0, timer - 1);
        float prevChange = change * prevTimer / maxTime;
        float curChange = change * timer / maxTime;
        return origin + prevChange + (curChange - prevChange) * partialTicks;
    }

    public void render(final float scale, final float partialTicks) {
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
                this.transitionUsing(c, animation.getTimeLeft(), c.getLength(), partialTicks);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, opacity / 100.0F);

        if (!this.isHidden) {
            if (this.showModel) {
                GlStateManager.pushMatrix();
                if (!this.compiled) {
                    this.compileDisplayList(scale);
                }
                GlStateManager.translate(this.offsetX / 16, this.offsetY / 16, this.offsetZ / 16);
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleZ), 0.0F, 0.0F, 1.0F);
                }
                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleY), 0.0F, 1.0F, 0.0F);
                }
                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate((float) Math.toDegrees(this.rotateAngleX), 1.0F, 0.0F, 0.0F);
                }
                if (this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F) {
                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                }
                GlStateManager.callList(this.displayList);
                if (this.childModels != null) {
                    for (ModelRenderer childModel : this.childModels) {
                        ((AnimatedModelRenderer) childModel).render(scale, partialTicks);
                    }
                }
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void resetToDefaultPose() {
        super.resetToDefaultPose();
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
        this.scaleZ = 1.0F;
        this.hidden = false;
        this.opacity = defaultOpacity;
        lastSeenTime = -1;
    }
}
