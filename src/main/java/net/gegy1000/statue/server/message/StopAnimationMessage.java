package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.client.AnimationController;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StopAnimationMessage extends AbstractMessage<StopAnimationMessage> {

    private BlockPos pos;

    public StopAnimationMessage() {}

    public StopAnimationMessage(final BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void onClientReceived(final Minecraft client, final StopAnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {
        AnimationController.get(player.world).stopAll(message.pos);
    }

    @Override
    public void onServerReceived(final MinecraftServer server, final StopAnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {

    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }
}
