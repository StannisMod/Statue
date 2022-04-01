package net.gegy1000.statue.client.model;

import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class OutlinedTabulaModel extends AdvancedModelBase implements OutlineRenderer {
    protected Map<String, OutlinedModelRenderer> cubes = new HashMap<>();
    protected Map<String, TabulaAnimationComponentContainer> animations = new HashMap<>();
    protected List<OutlinedModelRenderer> rootBoxes = new ArrayList<>();
    protected Map<String, OutlinedModelRenderer> identifierMap = new HashMap<>();
    protected double[] scale;

    protected Map<String, Integer> playing = new HashMap<>();

    public OutlinedTabulaModel(TabulaModelContainer container) {
        this.textureWidth = container.getTextureWidth();
        this.textureHeight = container.getTextureHeight();
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
        // TODO Check the key of the animations map
        container.getAnimations().forEach(c -> c.getComponents().forEach((cc, act) -> act.forEach(ccc -> animations.put(cc, ccc))));
        //container.getAnimations().forEach(c -> c.getComponents().values().forEach(c1 -> animations.put(c1., c));
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
            this.rootBoxes.add(box);
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

    private static AdvancedModelRenderer R;

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);
        for (OutlinedModelRenderer box : this.rootBoxes) {
            if (R == null) {
                R = new AdvancedModelRenderer(box.getModel());
            }
            // TODO Get IDENTIFIER of the root box(or box especially)
            String identifier = null;
            Integer remaining = playing.get(identifier);
            if (remaining != null) {
                TabulaAnimationComponentContainer c = animations.get(identifier);
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

                box.transitionTo(R, remaining, c.getEndKey() - c.getStartKey());
            }
            box.render(scale);
        }
        for (String key : playing.keySet()) {
            playing.compute(key, (k, v) -> {
                v--;
                if (v == 0) {
                    v = null;
                }
                return v;
            });
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

    @Override
    public void renderOutlines(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, null);
        GlStateManager.pushMatrix();
        for (OutlinedModelRenderer cube : this.rootBoxes) {
            cube.renderOutline(scale);
        }
        GlStateManager.popMatrix();
    }
}
