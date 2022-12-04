package net.gegy1000.statue.client;

import net.gegy1000.statue.client.model.OutlinedTabulaModel;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationContainer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationController {

    private static final Map<Integer, AnimationController> CONTROLLERS = new HashMap<>();

    public static AnimationController get(World world) {
        AnimationController c = CONTROLLERS.get(world.provider.getDimension());
        if (c == null) {
            c = new AnimationController(world);
            CONTROLLERS.put(world.provider.getDimension(), c);
        }
        return c;
    }

    public static void reset() {
        CONTROLLERS.clear();
    }

    private final Map<BlockPos, Map<String, Animation>> playing = new HashMap<>();
    private final World world;

    public AnimationController(World world) {
        this.world = world;
    }

    /**
     * Starts the given animation. If animation is already running, it restarts.
     * @param pos the position of Statue block
     * @param name the animation name that should be started
     */
    public Animation start(BlockPos pos, String name) {
        Animation[] dump = new Animation[1];
        playing.compute(pos, (k, play) -> {
            Map<String, AnimationComponent> map = new HashMap<>();
            TabulaAnimationContainer c = getAnimComponents(pos, name);
            if (c == null) {
                return play;
            }
            for (Map.Entry<String, List<TabulaAnimationComponentContainer>> entry : c.getComponents().entrySet()) {
                map.put(entry.getKey(), new AnimationComponent(entry.getValue()));
            }
            if (play == null) {
                play = new HashMap<>();
            }
            dump[0] = new Animation(map, c.doesLoop());
            play.put(name, dump[0]);
            return play;
        });
        return dump[0];
    }

    public void stopAll(BlockPos pos) {
        Map<String, Animation> anims = playing.remove(pos);
        if (anims == null) {
            return;
        }
        anims.values().forEach(parts -> parts.components.values().forEach(AnimationComponent::stop));
    }

    /**
     * Returns the parts of given animation at the given position. If
     * given animation is not exists, {@code null} should be returned. If block
     * at the given position has model that doesn't support animation, {@code null}
     * should be returned.
     * @param pos the position of Statue block
     * @param name the animation name that should be used for request
     */
    public TabulaAnimationContainer getAnimComponents(BlockPos pos, String name) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof StatueBlockEntity)) {
            return null;
        }

        StatueBlockEntity ent = (StatueBlockEntity) te;
        ModelBase modelBase = ent.getModel();
        if (!(modelBase instanceof OutlinedTabulaModel)) {
            return null;
        }
        return ((OutlinedTabulaModel) modelBase).getAnimations().get(name);
    }

    /**
     * Returns a state of animation, represented with 3 integers: time played, duration
     * and the index of currently playing component
     * @return the state of animation with given name at given pos
     */
    public AnimationComponent getAnimation(BlockPos pos, String animName, String partName) {
        Map<String, Animation> animations = playing.get(pos);
        if (animations == null) {
            return null;
        }
        Animation parts = animations.get(animName);
        if (parts == null) {
            return null;
        }
        return parts.components.get(partName);
    }

    public boolean isRunningAnimation(BlockPos pos) {
        return playing.get(pos) != null;
    }

    /**
     * Performing the tick at all animations at the given pos
     * @param pos the position of Statue block
     */
    public boolean tick(BlockPos pos) {
        Map<String, Animation> playingAtPos = playing.get(pos);
        if (playingAtPos == null) {
            return false;
        }
        playingAtPos.values().removeIf(remaining -> {
            if (remaining.shouldLoadIdentity()) {
                ((OutlinedTabulaModel) ((StatueBlockEntity) world.getTileEntity(pos)).getModel()).resetToDefaultPose();
            }
            remaining.tick();
            return remaining.components.isEmpty();
        });
        if (playingAtPos.keySet().isEmpty()) {
            playing.remove(pos);
            return true;
        }
        return false;
    }
}
