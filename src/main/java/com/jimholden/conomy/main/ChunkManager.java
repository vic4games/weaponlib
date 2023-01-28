package com.jimholden.conomy.main;

import java.util.List;

import com.google.common.collect.ListMultimap;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.PlayerOrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ChunkManager implements LoadingCallback {

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world)
    {
    	System.out.println("Tickets: " + tickets);
        for (ForgeChunkManager.Ticket ticket : tickets)
        {
            int xPos = ticket.getModData().getInteger("xPos");
            int yPos = ticket.getModData().getInteger("yPos");
            int zPos = ticket.getModData().getInteger("zPos");
            System.out.println(tickets);
            System.out.println(ticket);

            BlockPos pos = new BlockPos(xPos, yPos, zPos);

            TileEntity tilen = world.getTileEntity(pos);
            if (tilen instanceof TileEntityNode) {
            	TileEntityNode nodeTile = (TileEntityNode) world.getTileEntity(pos);
                if(nodeTile != null)
                {
                	//nodeTile.refreshChunkSet();
                	System.out.println("ya I load :?");
                	nodeTile.forceChunks(ticket);
                	
                }
            }
        }
    }
}