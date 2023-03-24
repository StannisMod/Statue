package net.gegy1000.statue.server.command;

import mcp.MethodsReturnNonnullByDefault;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MoveAnimCommand extends CommandBase {

    @Override
    public String getName() {
        return "moveanim";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/moveanim <from x> <from y> <from z> <to x> <to y> <to z>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length != 6) {
            throw new WrongUsageException(getUsage(sender));
        }

        try {
            int[] fromArr = new int[3];
            fromArr[0] = Integer.parseInt(args[0]);
            fromArr[1] = Integer.parseInt(args[1]);
            fromArr[2] = Integer.parseInt(args[2]);
            BlockPos from = new BlockPos(fromArr[0], fromArr[1], fromArr[2]);
            TileEntity tew = sender.getEntityWorld().getTileEntity(from);
            if (!(tew instanceof StatueBlockEntity)) {
                throw new WrongUsageException("There is no Statue at given coordinates");
            }
            StatueBlockEntity te = (StatueBlockEntity) tew;

            int[] toArr = new int[3];
            for (int i = 3; i < 6; i++) {
                if (args[i].startsWith("~")) {
                    // relational
                    args[i] = args[i].substring(1);
                    toArr[i - 3] = fromArr[i - 3] + Integer.parseInt(args[i]);
                } else {
                    toArr[i - 3] = Integer.parseInt(args[i]);
                }
            }
            BlockPos to = new BlockPos(toArr[0], toArr[1], toArr[2]);

            // set target block, it will create TE
            IBlockState state = sender.getEntityWorld().getBlockState(from);
            sender.getEntityWorld().setBlockState(to, state);

            // move TE
            te.setPos(to);
            NBTTagCompound tag = te.writeToNBT(new NBTTagCompound());
            StatueBlockEntity teNew = (StatueBlockEntity) sender.getEntityWorld().getTileEntity(to);
            if (teNew == null) {
                System.out.println("null TE :(");
                return;
            }
            teNew.readFromNBT(tag);

            // remove old block and TE
            sender.getEntityWorld().setBlockToAir(from);
            sender.getEntityWorld().removeTileEntity(from);

            teNew.sendTextureUpdates();
            teNew.markDirty();
            sender.getEntityWorld().notifyBlockUpdate(to, state, state, 3);

            // /moveanim -8 64 137 -10 64 137

        } catch (NumberFormatException e) {
            throw new WrongUsageException("Animation coordinates should be integers!");
        }
    }
}
