package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.client.AnimationController;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AnimationMessage extends AbstractMessage<AnimationMessage> {

    private String animation;
    private BlockPos pos;
    private FunctionPacket.Type commandType;
    private String command;
    private int loops;

    public AnimationMessage() {}

    public AnimationMessage(final String animation, final BlockPos pos, final FunctionPacket.Type commandType, final String command, final int loops) {
        this.animation = animation;
        this.pos = pos;
        this.commandType = commandType;
        this.command = command;
        this.loops = loops;
    }

    @Override
    public void onClientReceived(final Minecraft client, final AnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {
        StatueBlockEntity te = (StatueBlockEntity) player.getEntityWorld().getTileEntity(message.pos);
        if (te == null) {
            player.sendMessage(new TextComponentString("Statue not found"));
            return;
        }
        ((AdvancedModelBase) te.getModel()).resetToDefaultPose();
        AnimationController.get(player.world).start(message.pos, message.animation, message.loops)
                .setLoopingCommand(message.commandType, message.command);
    }

    @Override
    public void onServerReceived(final MinecraftServer server, final AnimationMessage message, final EntityPlayer player, final MessageContext messageContext) {
        // no stuff here
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        animation = ByteBufUtils.readUTF8String(buf);
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        commandType = FunctionPacket.Type.values()[buf.readInt()];
        command = ByteBufUtils.readUTF8String(buf);
        loops = buf.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, animation);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(commandType.ordinal());
        ByteBufUtils.writeUTF8String(buf, command);
        buf.writeInt(loops);
    }
}
