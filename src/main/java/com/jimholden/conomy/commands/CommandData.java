package com.jimholden.conomy.commands;

import java.util.List;

import org.apache.commons.logging.Log;

import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.MemoryItem;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;





public class CommandData extends CommandBase {
	String usage = "dataset <analysis | data > <value>";
	String tabOpt[] = {"analysis", "data"};
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "dataset";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return usage;
	} 
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		
		//System.out.println(EntityList.getEntityNameList());
		if (args.length == 1) return getListOfStringsMatchingLastWord(args, tabOpt);
		return super.getTabCompletions(server, sender, args, targetPos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			
			EntityPlayer p = (EntityPlayer) sender;
			if(!(p.getHeldItemMainhand().getItem() instanceof MemoryItem)) return;
			MemoryItem item = (MemoryItem) p.getHeldItemMainhand().getItem();
			ItemStack is = p.getHeldItemMainhand();
			
			if(args.length == 0)
			{
				p.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
			
			if(args.length == 2)
			{
				
				switch(args[0]) {
					case "data":
						double valueD = Double.parseDouble(args[1]);
						item.setDataTotal(valueD, is);
						break;
					case "analysis":
						int value = Integer.parseInt(args[1]);
						if(0.0F < (float) value/100 && 1.0F >= (float) value/100)
						{
							item.setAnalysis((float) value/100, is);
							//System.out.println((float) value/100);
						}
						else return;
						
						break;
					case "quality":
						p.sendMessage(new TextComponentString(TextFormatting.RED + "Quality: " + item.getQuality(is)));
						break;
						
				}
			}
			else {
				p.sendMessage(new TextComponentString(TextFormatting.RED + usage));
				return;
			}
		}
		
	}

}
