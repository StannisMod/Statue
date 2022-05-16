package net.gegy1000.statue.client.model;

import net.gegy1000.statue.client.AnimationController;
import net.ilexiconn.llibrary.client.model.tabula.container.*;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class OutlinedTabulaModel extends AdvancedModelBase implements OutlineRenderer {
    protected Map<String, AnimatedModelRenderer> cubes = new HashMap<>();
    protected Map<String, TabulaAnimationContainer> animations = new HashMap<>();
    protected Map<String, AnimatedModelRenderer> rootBoxes = new HashMap<>();
    protected Map<String, AnimatedModelRenderer> identifierMap = new HashMap<>();
    protected double[] scale;

    public OutlinedTabulaModel(TabulaModelContainer container) {
        this.textureWidth = container.getTextureWidth();
        this.textureHeight = container.getTextureHeight();
        for (TabulaCubeContainer cube : container.getCubes()) {
            this.parseCube(cube, null);
        }
        container.getCubeGroups().forEach(this::parseCubeGroup);
        container.getAnimations().forEach(c -> {
            animations.put(c.getName(), c);
            // sorting animation components by start time
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

    private void parseCube(TabulaCubeContainer cube, AdvancedModelRenderer parent) {
        AnimatedModelRenderer box = this.createBox(cube);
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

    protected AnimatedModelRenderer createBox(TabulaCubeContainer cube) {
        int[] textureOffset = cube.getTextureOffset();
        double[] position = cube.getPosition();
        double[] rotation = cube.getRotation();
        double[] offset = cube.getOffset();
        double[] scale = cube.getScale();
        int[] dimensions = cube.getDimensions();
        AnimatedModelRenderer box = new AnimatedModelRenderer(this, cube.getName(), cube.getIdentifier(), textureOffset[0], textureOffset[1], (float) cube.getOpacity());
        box.mirror = cube.isTextureMirrorEnabled();

        box.setRotationPoint((float) position[0], (float) position[1], (float) position[2]);
        box.addBox((float) offset[0], (float) offset[1], (float) offset[2], dimensions[0], dimensions[1], dimensions[2], 0.0F);
        box.rotateAngleX = (float) Math.toRadians(rotation[0]);
        box.rotateAngleY = (float) Math.toRadians(rotation[1]);
        box.rotateAngleZ = (float) Math.toRadians(rotation[2]);
        box.scaleX = (float) scale[0];
        box.scaleY = (float) scale[1];
        box.scaleZ = (float) scale[2];
        return box;
    }

    protected BlockPos pos;
    protected AnimationController controller;

    public void setRenderTarget(World world, BlockPos pos) {
        this.pos = pos;
        this.controller = AnimationController.get(world);
    }

    private float lastSeenPartialTick = 0;

    /**
     * Renders the model. You SHOULD call {@code #setRenderTarget(World, BlockPos)} before rendering
     */
    // /animate 1 -190 74 273
    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        if (pos != null) {
            if (lastSeenPartialTick >= limbSwing) {
                if (controller.tick(pos)) {
                    this.resetToDefaultPose();
                }
            }
            lastSeenPartialTick = limbSwing;
        }

        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();
        if (pos != null) {
//            int light = Minecraft.getMinecraft().world.getCombinedLight(pos, 0);
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, light >> 16);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        //ClientProxy.MINECRAFT.entityRenderer.enableLightmap();
        GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);
        for (AnimatedModelRenderer box : this.rootBoxes.values()) {
            box.render(scale, limbSwing);
        }
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    public AnimatedModelRenderer getCube(String name) {
        return this.cubes.get(name);
    }

    public AnimatedModelRenderer getCubeByIdentifier(String identifier) {
        return this.identifierMap.get(identifier);
    }

    public Map<String, AnimatedModelRenderer> getCubes() {
        return this.cubes;
    }

    public Map<String, TabulaAnimationContainer> getAnimations() {
        return animations;
    }

    @Override
    public void renderOutlines(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        // empty stub here
    }
}
