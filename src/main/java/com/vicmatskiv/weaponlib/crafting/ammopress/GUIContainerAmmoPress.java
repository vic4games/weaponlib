package com.vicmatskiv.weaponlib.crafting.ammopress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.animation.gui.GuiRenderUtil;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGuiContainer;
import com.vicmatskiv.weaponlib.crafting.CraftingEntry;
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.base.GUIContainerStation;
import com.vicmatskiv.weaponlib.crafting.items.CraftingItem;
import com.vicmatskiv.weaponlib.crafting.workbench.CustomSearchTextField;
import com.vicmatskiv.weaponlib.crafting.workbench.GUIButtonCustom;
import com.vicmatskiv.weaponlib.crafting.workbench.GUIContainerWorkbench;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;
import com.vicmatskiv.weaponlib.crafting.workbench.WorkbenchBlock;
import com.vicmatskiv.weaponlib.network.packets.StationPacket;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper.StringAlignment;
import com.vicmatskiv.weaponlib.vehicle.jimphysics.InterpolationKit;

import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import scala.actors.threadpool.Arrays;

/**
 * GUIContainer for the Workbench Block
 * 
 * Crafting Modes:
 * 1.) Bullets
 * 2.) Magazines
 * 
 * Features (plus the features of it's parent class {@link GUIContainerStation})
 * 1. Player has three categories to choose from weapons, attachments, and modification attachments
 * 2. A queue that allows players to queue items up for crafting (maximum sets in queue is 7)
 * 3. Textbox that allows the player to specify the quantity
 * 
 * @author Homer Riva-Cambrin, 2022
 * @version September 23rd, 2022
 */
public class GUIContainerAmmoPress extends GUIContainerStation<TileEntityAmmoPress> {
	
	// Ammo press texture location
	private static final ResourceLocation AMMO_PRESS_TEX = new ResourceLocation("mw:textures/gui/ammosheet.png");

	// Selectors & Quantity Box
	private GUIButtonCustom bulletSelector, magazineSelector;
	private CustomSearchTextField quantityBox;
	
	
	public GUIContainerAmmoPress(EntityPlayer player, InventoryPlayer inventory,
			TileEntityAmmoPress tileEntityAmmoPress) {
		super(tileEntityAmmoPress, new ContainerAmmoPress(player, inventory, tileEntityAmmoPress));
	}
	
	
	@Override
	public void initGui() {
		super.initGui();


		
		this.quantityBox = new CustomSearchTextField(AMMO_PRESS_TEX, "Amt.", 1, 1, this.fontRenderer, this.guiLeft + 267, this.guiTop + 183, 84, 13);
		this.quantityBox.setMaxStringLength(50);
		this.quantityBox.setEnableBackgroundDrawing(true);
		this.quantityBox.setVisible(true);
		this.quantityBox.setTextColor(16777215);





		bulletSelector = new GUIButtonCustom(AMMO_PRESS_TEX, 3, this.guiLeft + 107, this.guiTop + 29, 19, 20, 256, 256, "")
				.withStandardState(0xFFFFFF, 0, 0).withHoveredState(0xFFFFFF, 19, 0)
				.withToggledState(0xFFFFFF, 38, 0).withPageRestriction(2).makeToggleButton();

		magazineSelector = new GUIButtonCustom(AMMO_PRESS_TEX, 4, this.guiLeft + 130, this.guiTop + 29, 19, 20, 256, 256, "")
				.withStandardState(0xFFFFFF, 0, 20).withHoveredState(0xFFFFFF, 19, 20)
				.withToggledState(0xFFFFFF, 38, 20).withPageRestriction(2).makeToggleButton();



		bulletSelector.toggleOn();

		addButton(bulletSelector);
		addButton(magazineSelector);
		
		setPage(1);


	}
	
	/**
	 * False because we are only interested in if the player
	 * has the materials if we are asking: Can they craft the
	 * latest item in the queue? If not, do not proceed, if yes,
	 * proceed.
	 */
	@Override
	public boolean requiresMaterialsToSubmitCraftRequest() {
		return false;
	}
	
	@Override
	public void fillFilteredList() {
		filteredCraftingList.clear();
		if(getCraftingMode() == 1) {
			filteredCraftingList.addAll(CraftingRegistry.getCraftingListForGroup(CraftingGroup.BULLET));
		} else if(getCraftingMode() == 2) {
			filteredCraftingList.addAll(CraftingRegistry.getCraftingListForGroup(CraftingGroup.MAGAZINE));
		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		if(this.tileEntity.getCraftingQueue().size() > 9) {
			craftButton.setDisabled(true);
		} else {
			craftButton.setDisabled(false);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button == craftButton && !craftButton.isDisabled()) {

			if (hasSelectedCraftingPiece() && quantityBox.getText().length() != 0) {

				int quantity = Integer.parseInt(quantityBox.getText());
				
				
			
				modContext.getChannel().getChannel()
						.sendToServer(new StationPacket(StationPacket.CRAFT, tileEntity.getPos(), getSelectedCraftingPiece().getItem().getUnlocalizedName(), getSelectedCraftingPiece().getCraftingGroup(), quantity));

				
			}

		}  else if (button == bulletSelector) {
			((GUIButtonCustom) button).toggleOn();
			magazineSelector.toggleOff();
			setCraftingMode(1);
		
			setSelectedCraftingPiece(null);

			fillFilteredList();
		} else if (button == magazineSelector) {
			((GUIButtonCustom) button).toggleOn();
			bulletSelector.toggleOff();
			setCraftingMode(2);

			setSelectedCraftingPiece(null);

			fillFilteredList();
		}
	}

	

	

	

	


	@Override
	public void addCraftingInformationToTooltip(ArrayList<String> tooltip) {
		tooltip.add(TextFormatting.GOLD + "Crafting: " + TextFormatting.WHITE
				+ I18n.format(tileEntity.getLatestStackInQueue().getUnlocalizedName() + ".name"));
		tooltip.add(TextFormatting.GOLD + "Quantity: " + TextFormatting.WHITE
				+ tileEntity.getLatestStackInQueue().getCount());
	}

	
	@Override
	public void addCustomTooltipInformation(int mouseX, int mouseY, ArrayList<String> tooltip) {
		super.addCustomTooltipInformation(mouseX, mouseY, tooltip);
		
		
		int highlighted = -1;
		if(tileEntity.hasStack()) {
			if (mouseY >= this.guiTop && mouseY <= this.guiTop + 20) {
				int id = (mouseX - (this.guiLeft + 200))/20;
				if(id >= 0 && tileEntity.getCraftingQueue().size() - 1 >= id) {
					highlighted = id;
					tooltip.add(format(tileEntity.getCraftingQueue().get(id).getUnlocalizedName()));
					tooltip.add(TextFormatting.GRAY + "Quantity: " + TextFormatting.GOLD + tileEntity.getCraftingQueue().get(id).getCount());
				}
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(getPage() == 2) this.quantityBox.drawTextBox();
		drawTooltips(mouseX, mouseY, partialTicks);
		
		
		if (getPage() == 2) {
			
			
			
			LinkedList<ItemStack> queue = tileEntity.getCraftingQueue();
			GlStateManager.enableBlend();
			for(int i = 0; i < queue.size(); ++i) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(AMMO_PRESS_TEX);
				if(GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 200 + i*20, this.guiTop, 20, 20)) {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 200 + i*20, this.guiTop, 20, 40, 20, 20, 256, 256);
				} else {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 200 + i*20, this.guiTop, 0, 40, 20, 20, 256, 256);
				}
			}
			
			for(int i = 0; i < queue.size(); ++i) {
				ItemStack stack = queue.get(i);
				Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, this.guiLeft + 202 + i*20, this.guiTop + 2);
			}
			
			for(int i = 0; i < queue.size(); ++i) {
				ItemStack stack = queue.get(i);
				GUIRenderHelper.drawScaledString("x" + stack.getCount(), this.guiLeft + 212 + i*20, this.guiTop + 16, 0.7, GOLD);
			}
			
		}
		
		
		


	}

	@Override
	protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.compatibleMouseClicked(mouseX, mouseY, mouseButton);
		this.quantityBox.mouseClicked(mouseX, mouseY, mouseButton);
		
		

		if(tileEntity.hasStack()) {
			if (mouseY >= this.guiTop && mouseY <= this.guiTop + 20) {
				int id = (mouseX - (this.guiLeft + 200))/20;
				if(id >= 0 && tileEntity.getCraftingQueue().size() - 1 >= id) {
					
					modContext.getChannel().getChannel().sendToServer(new StationPacket(StationPacket.POP_FROM_QUEUE, tileEntity.getPos(), Minecraft.getMinecraft().player.getEntityId(), id));
				}
			}
		}
		
		


	}

	@SuppressWarnings("unchecked")
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		 
	
		boolean cancelationForQuantity = this.quantityBox.getText().length() == 0 && keyCode == Keyboard.KEY_BACK;
		
		super.keyTyped(typedChar, keyCode);
		if(Character.isDigit(typedChar) || keyCode == Keyboard.KEY_BACK) {
			this.quantityBox.textboxKeyTyped(typedChar, keyCode);
		}
		
		if((cancelationForQuantity && this.quantityBox.isFocused())) return;
		
		
		if(keyCode == Keyboard.KEY_BACK) {
			fillFilteredList();
		}
		
	
		
	}



	


}