package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.gegy1000.statue.client.AnimationController;
import net.gegy1000.statue.client.model.OutlinedTabulaModel;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
        TileEntity te = player.getEntityWorld().getTileEntity(message.pos);
        if (!(te instanceof StatueBlockEntity)) {
            player.sendMessage(new TextComponentString("Can't stop animation in place that not contain Statue block: " + message.pos));
            return;
        }
        ModelBase model = ((StatueBlockEntity) te).getModel();
        if (!(model instanceof OutlinedTabulaModel)) {
            player.sendMessage(new TextComponentString("Statue block at position " + message.pos + " haven't got Tabula model"));
            return;
        }
        ((OutlinedTabulaModel) model).resetToDefaultPose();
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
