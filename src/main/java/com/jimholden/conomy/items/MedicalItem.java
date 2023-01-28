package com.jimholden.conomy.items;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.client.gui.TimedConsumableTracker;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.medical.IConscious;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.medical.BandageServerPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MedicalItem extends ItemBase implements ITimedConsumable {
	
	public static class MedicalProperties{
		
		private int time;
		private int regenLevel;
		private int regenerationTime;
		private boolean stopsBleeding;
		
		public MedicalProperties(int time, int regenLevel, int regenerationTime, boolean stopsBleeding) {
			this.time = time;
			this.regenLevel = regenLevel;
			this.regenerationTime = regenerationTime;
			this.stopsBleeding = stopsBleeding;
		}
		
		public boolean willStopBleeding() {
			return this.stopsBleeding;
		}
		
		public int getRegenerationLevel() {
			return this.regenLevel;
		}
		
		public int getApplicationTime() {
			return this.getApplicationTime();
		}
		
		public int getRegenerationTime() {
			return this.regenerationTime;
		}
		
	}
	
	public MedicalProperties medicinalProperties;

	public MedicalItem(String name, MedicalProperties properties) {
		super(name);
		this.medicinalProperties = properties;
		// TODO Auto-generated constructor stub
	}
	
	public MedicalProperties getMedicinalProperties() {
		return this.medicinalProperties;
	}
	
	@SideOnly(Side.CLIENT)
	public void playBandageNoise() {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundsHandler.APPLY_BANDAGE, 1.0F));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		IConscious con = playerIn.getCapability(ConsciousProvider.CONSCIOUS, null);
		
		if(con.isBleeding() || getMedicinalProperties().getRegenerationLevel() != 0) {
			
			if(worldIn.isRemote) {
				
				TimedConsumableTracker.setup(this, playerIn, worldIn, handIn);
			}
			
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public int getDuration() {
		
		return 60;
	}

	@Override
	public void onComplete(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
	
		
		if(worldIn.isRemote) {
			playBandageNoise();
			MedicalProperties medProperties = getMedicinalProperties();
			Main.NETWORK.sendToServer(new BandageServerPacket(playerIn.getEntityId(), handIn, medProperties.getRegenerationLevel(), medProperties.getRegenerationTime(), medProperties.willStopBleeding()));
		}
		
		
		
		
	}

}
