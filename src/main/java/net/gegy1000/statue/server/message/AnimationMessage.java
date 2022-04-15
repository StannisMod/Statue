package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.client.AnimationController;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AnimationMessage extends AbstractMessage<AnimationMessage> {

    private String animation;
    private BlockPos pos;

    public AnimationMessage() {}

    public AnimationMessage(final String animation, final BlockPos pos) {
        this.animation = animation;
        this.pos = pos;
    }

    @Override
    public void onClientReceived(final Minecraft client, final AnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {
        AnimationController.get(player.world).start(message.pos, message.animation);
        System.out.println("Received at controller");
    }

    @Override
    public void onServerReceived(final MinecraftServer server, final AnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {

    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        animation = ByteBufUtils.readUTF8String(buf);
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, animation);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }
}
