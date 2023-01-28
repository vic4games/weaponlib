package com.jimholden.conomy.commands;

import java.util.List;

import com.jimholden.conomy.items.LedgerBase;
import com.jimholden.conomy.items.OpenDimeBase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;





public class CommandCurrencyLedger extends CommandBase {
	
	String useCase = "Usage: /leger <add | remove | set | inrange> <value>";

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ledger";
	}
	


	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return useCase;
	} 

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer) {
			
			EntityPlayer p = (EntityPlayer) sender;
			if(!(p.getHeldItemMainhand().getItem() instanceof LedgerBase)) return;
			LedgerBase item = (LedgerBase) p.getHeldItemMainhand().getItem();
			ItemStack is = p.getHeldItemMainhand();
			
			if(args.length == 0)
			{
				p.sendMessage(new TextComponentString(TextFormatting.RED + useCase));
				return;
			}
			
			if(args.length == 2)
			{
				int value = Integer.parseInt(args[1]);
				switch(args[0]) {
					case "add":
						item.setBalance(item.getBalance(is) + value, is);
						break;
					case "remove":
						item.setBalance(item.getBalance(is) - value, is);
						break;
					case "set":
						item.setBalance(value, is);
						break;
					case "pinrange":
						p.sendMessage(new TextComponentString(TextFormatting.RED + "Checking nearby..."));
						List<EntityPlayer> players = p.world.getEntitiesWithinAABB(EntityPlayer.class, p.getEntityBoundingBox().expand(15, 15, 15));
						EntityPlayer target = players.get(0);
						p.sendMessage(new TextComponentString(TextFormatting.RED + "Players Near: "));
						for(int i = 0; i < players.size(); i++) {
							if(!(players.get(i).getName() != p.getName()))
							{
								p.sendMessage(new TextComponentString(TextFormatting.RED + "" + players.get(i).getName()));
							}

						}
						break;
					case "linrange":
						List<EntityPlayer> playersL = p.world.getEntitiesWithinAABB(EntityPlayer.class, p.getEntityBoundingBox().expand(15, 15, 15));
						if(!playersL.isEmpty()) {
							EntityPlayer targetL = playersL.get(0);
							ItemStack stack = p.getHeldItemMainhand();
							ItemStack targetStack = null;
							for(int i = 0; i < targetL.inventory.getSizeInventory(); i++) {
								if(targetL.inventory.getStackInSlot(i).getItem() instanceof LedgerBase) {
									targetStack = targetL.inventory.getStackInSlot(i);
									if(((LedgerBase) targetStack.getItem()).getState(targetStack)) {
										p.sendMessage(new TextComponentString(TextFormatting.RED + "" + ((LedgerBase) targetStack.getItem()).getKey(targetStack)));
										break;
									}
								}
							}
						}
				}
			}
			else {
				p.sendMessage(new TextComponentString(TextFormatting.RED + useCase));
				return;
			}
		}
		
	}

}
