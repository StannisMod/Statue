package net.gegy1000.statue.client;

import net.gegy1000.statue.client.model.OutlinedTabulaModel;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationComponentContainer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {

    private final Map<BlockPos, Map<String, Integer>> playing = new HashMap<>();
    private final World world;

    public AnimationController(World world) {
        this.world = world;
    }

    public void start(BlockPos pos, String name) {
        playing.compute(pos, (k, play) -> {
            if (play == null) {
                play = new HashMap<>();
            }
            play.put(name, getFullDuration(pos, name));
            return play;
        });
    }

    public int getFullDuration(BlockPos pos, String name) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof StatueBlockEntity)) {
            return -1;
        }

        StatueBlockEntity ent = (StatueBlockEntity) te;
        ModelBase modelBase = ent.getModel();
        if (!(modelBase instanceof OutlinedTabulaModel)) {
            return -2;
        }
        TabulaAnimationComponentContainer anim = ((OutlinedTabulaModel) modelBase).getAnimations().get(name);
        if (anim == null) {
            return -1;
        }
        return anim.getLength();
    }

    public boolean isPlaying(BlockPos pos, String name) {
        return getDuration(pos, name) != -1;
    }

    public int getDuration(BlockPos pos, String name) {
        return playing.computeIfAbsent(pos, k -> new HashMap<>()).getOrDefault(name, -1);
    }
}
