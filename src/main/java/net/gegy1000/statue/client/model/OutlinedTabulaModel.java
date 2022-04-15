package net.gegy1000.statue.client.model;

import net.gegy1000.statue.client.Animation;
import net.gegy1000.statue.client.AnimationController;
import net.ilexiconn.llibrary.client.model.tabula.container.*;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class OutlinedTabulaModel extends AdvancedModelBase implements OutlineRenderer {
    protected Map<String, OutlinedModelRenderer> cubes = new HashMap<>();
    protected Map<String, TabulaAnimationContainer> animations = new HashMap<>();
    protected Map<String, OutlinedModelRenderer> rootBoxes = new HashMap<>();
    protected Map<String, OutlinedModelRenderer> identifierMap = new HashMap<>();
    protected double[] scale;

    public OutlinedTabulaModel(TabulaModelContainer container) {
        this.textureWidth = container.getTextureWidth();
        this.textureHeight = container.getTextureHeight();
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
        // TODO Check the key of the animations map
        container.getAnimations().forEach(c -> {
            animations.put(c.getIdentifier(), c);
            c.getComponents().values().forEach(lst -> lst.sort(Comparator.comparingInt(TabulaAnimationComponentContainer::getStartKey)));
        });
        this.updateDefaultPose();
        this.scale = container.getScale();
        if (this.scale == null) {
            this.scale = new double[] { 1.0, 1.0, 1.0 };
        }
    }

    private void parseCubeGroup(TabulaCubeGroupContainer container) {
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
    }

    private void parseCube(TabulaCubeContainer cube, OutlinedModelRenderer parent) {
        OutlinedModelRenderer box = this.createBox(cube);
        this.cubes.put(cube.getName(), box);
        this.identifierMap.put(cube.getIdentifier(), box);
        if (parent != null) {
            parent.addChild(box);
        } else {
            this.rootBoxes.put(cube.getIdentifier(), box);
        }
        for (TabulaCubeContainer child : cube.getChildren()) {
            this.parseCube(child, box);
        }
    }

    protected OutlinedModelRenderer createBox(TabulaCubeContainer cube) {
        int[] textureOffset = cube.getTextureOffset();
        double[] position = cube.getPosition();
        double[] rotation = cube.getRotation();
        double[] offset = cube.getOffset();
        int[] dimensions = cube.getDimensions();
        OutlinedModelRenderer box = new OutlinedModelRenderer(this, cube.getName(), textureOffset[0], textureOffset[1]);
        box.mirror = cube.isTextureMirrorEnabled();
        box.setRotationPoint((float) position[0], (float) position[1], (float) position[2]);
        box.addBox((float) offset[0], (float) offset[1], (float) offset[2], dimensions[0], dimensions[1], dimensions[2], 0.0F);
        box.rotateAngleX = (float) Math.toRadians(rotation[0]);
        box.rotateAngleY = (float) Math.toRadians(rotation[1]);
        box.rotateAngleZ = (float) Math.toRadians(rotation[2]);
        return box;
    }

    // TODO Rewrite handling transition variable
    private static AdvancedModelRenderer R;

    private World world;
    private BlockPos pos;
    private AnimationController controller;

    public void setRenderTarget(World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;
        this.controller = AnimationController.get(world);
    }

    /**
     * Renders the model. You SHOULD call {@code #setRenderTarget(World, BlockPos)} before rendering
     */
    // /animate )(M9#$n&2-l,cY*f0e<5 -190 75 274
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);
        for (Map.Entry<String, OutlinedModelRenderer> entry : this.rootBoxes.entrySet()) {
            OutlinedModelRenderer box = entry.getValue();
            if (R == null) {
                R = new AdvancedModelRenderer(box.getModel());
            }
            if (pos != null) {
                String identifier = entry.getKey();
                // TODO Make more robust algorithm. Store the current playing component
                for (String animId : animations.keySet()) {
                    Animation animation = controller.getAnimation(pos, animId, identifier);
                    if (animation == null) {
                        continue;
                    }
                    TabulaAnimationComponentContainer c = animation.getCurrentComponent();

                    // copy info
                    R.offsetX = (float) c.getPositionOffset()[0];
                    R.offsetY = (float) c.getPositionOffset()[1];
                    R.offsetZ = (float) c.getPositionOffset()[2];

                    R.rotationPointX = (float) c.getRotationOffset()[0];
                    R.rotationPointY = (float) c.getRotationOffset()[1];
                    R.rotationPointZ = (float) c.getRotationOffset()[2];

                    R.rotateAngleX = (float) c.getRotationChange()[0];
                    R.rotateAngleY = (float) c.getRotationChange()[1];
                    R.rotateAngleZ = (float) c.getRotationChange()[2];

                    R.scaleX = (float) c.getScaleChange()[0];
                    R.scaleY = (float) c.getScaleChange()[1];
                    R.scaleZ = (float) c.getScaleChange()[2];

                    box.transitionTo(R, animation.getTimeLeft(), c.getEndKey() - c.getStartKey());
                }
            }
            box.render(scale);
        }
        if (pos != null) {
            controller.tick(pos);
        }
        GlStateManager.popMatrix();
    }

    public OutlinedModelRenderer getCube(String name) {
        return this.cubes.get(name);
    }

    public OutlinedModelRenderer getCubeByIdentifier(String identifier) {
        return this.identifierMap.get(identifier);
    }

    public Map<String, OutlinedModelRenderer> getCubes() {
        return this.cubes;
    }

    public Map<String, TabulaAnimationContainer> getAnimations() {
        return animations;
    }

    @Override
    public void renderOutlines(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, null);
        GlStateManager.pushMatrix();
        for (OutlinedModelRenderer cube : this.rootBoxes.values()) {
            cube.renderOutline(scale);
        }
        GlStateManager.popMatrix();
    }
}
