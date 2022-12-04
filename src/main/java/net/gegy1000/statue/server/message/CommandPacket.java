package net.gegy1000.statue.server.message;

import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommandPacket extends AbstractMessage<CommandPacket> {

    public enum Type {
        PLAYER,
        SERVER
    }

    private Type type;
    private String command;

    public CommandPacket() {
    }

    @Override
    public void onClientReceived(final Minecraft client, final CommandPacket message, final EntityPlayer player, final MessageContext messageContext) {
        // no stuff here
    }

    @Override
    public void onServerReceived(final MinecraftServer server, final CommandPacket message, final EntityPlayer player, final MessageContext messageContext) {
        switch (message.type) {
            case PLAYER:
                server.addScheduledTask(() -> server.commandManager.executeCommand(player, message.command));
                break;
            case SERVER:
                server.addScheduledTask(() -> {
                    if (player.canUseCommand(2, "")) {
                        System.out.printf(
                                "[Statue] Player %s trying to execute command %s without OP permissions%n",
                                player.getName(), message.command
                        );
                        server.commandManager.executeCommand(server, message.command);
                    }
                });
                break;
            default:
                System.err.println("[CommandPacket] Received unknown type of command sender: " + message.type);
                break;
        }
    }

    public CommandPacket(final Type type, final String command) {
        this.type = type;
        this.command = command;
    }

    @Override
    public void fromBytes(final ByteBuf buf) {
        type = Type.values()[buf.readInt()];
        command = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(final ByteBuf buf) {
        buf.writeInt(type.ordinal());
        ByteBufUtils.writeUTF8String(buf, command);
    }
}
