package com.vicmatskiv.weaponlib.crafting.workbench;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.vicmatskiv.weaponlib.network.packets.StationPacket;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper.StringAlignment;

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
 * 1.) Guns
 * 2.) Attachments (normal ones)
 * 3.) Modification mode attachments
 * 
 * 
 * @author Homer Riva-Cambrin, 2022
 */
public class GUIContainerWorkbench extends GUIContainerStation<TileEntityWorkbench> {

	// Buttons & Search box
	private GUIButtonCustom assaultSelector, attachSelector, modSelector;

	public GUIContainerWorkbench(EntityPlayer player, InventoryPlayer inventory,
			TileEntityWorkbench tileEntityWorkbench) {
		super(tileEntityWorkbench, new ContainerWorkbench(player, inventory, tileEntityWorkbench));
	}
	

	@Override
	public void initGui() {
		super.initGui();
		
		assaultSelector = new GUIButtonCustom(GUI_TEX, 3, this.guiLeft + 107, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 291).withHoveredState(0xFFFFFF, 19, 291)
				.withToggledState(0xFFFFFF, 38, 291).withPageRestriction(2).makeToggleButton();

		attachSelector = new GUIButtonCustom(GUI_TEX, 4, this.guiLeft + 130, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 311).withHoveredState(0xFFFFFF, 19, 311)
				.withToggledState(0xFFFFFF, 38, 311).withPageRestriction(2).makeToggleButton();

		modSelector = new GUIButtonCustom(GUI_TEX, 5, this.guiLeft + 154, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 331).withHoveredState(0xFFFFFF, 19, 331)
				.withToggledState(0xFFFFFF, 38, 331).withPageRestriction(2).makeToggleButton();
		
		assaultSelector.toggleOn();

		addButton(assaultSelector);
		addButton(attachSelector);
		addButton(modSelector);
		
		setPage(1);
	}
	
	@Override
	public void fillFilteredList() {
		filteredCraftingList.clear();
		if(getCraftingMode() == 1) {
			filteredCraftingList.addAll(CraftingRegistry.getWeaponCraftingRegistry());
		} else if(getCraftingMode() == 2) {
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());
			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.ATTACHMENT_NORMAL);
		} else {
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());
			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.ATTACHMENT_MODIFICATION);
		}
  	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button == craftButton) {

			if (hasSelectedCraftingPiece() && tileEntity.craftingTimer == -1) {

				modContext.getChannel().getChannel()
						.sendToServer(new StationPacket(StationPacket.CRAFT, tileEntity.getPos(), 0, getCraftingMode() == 1 ? WorkbenchBlock.WORKBENCH_WEAPON_CRAFTING_TIME : WorkbenchBlock.WORKBENCH_ATTACHMENT_CRAFTING_TIME,
								CraftingGroup.getValue(getCraftingMode()),
								getSelectedCraftingPiece().getItem().getUnlocalizedName()));

			}

		} else if (button == assaultSelector) {
			((GUIButtonCustom) button).toggleOn();
			modSelector.toggleOff();
			attachSelector.toggleOff();
			setCraftingMode(1);

			setSelectedCraftingPiece(null);

			fillFilteredList();
		} else if (button == attachSelector) {
			((GUIButtonCustom) button).toggleOn();
			modSelector.toggleOff();
			assaultSelector.toggleOff();
			setCraftingMode(2);

			setSelectedCraftingPiece(null);

			fillFilteredList();
		} else if (button == modSelector) {
			((GUIButtonCustom) button).toggleOn();
			attachSelector.toggleOff();
			assaultSelector.toggleOff();
			setCraftingMode(3);
			fillFilteredList();
			
			
		}
	}



	@Override
	public void updateScreen() {
		super.updateScreen();
		if (!this.craftButton.isDisabled() && tileEntity.getProgress() != 0) {
			this.craftButton.setDisabled(true);
		}
		if(hasSelectedCraftingPiece() && hasRequiredItems() && tileEntity.getProgress() == 0) {
			this.craftButton.setDisabled(false);
		}
	}
	
	/**
	 * Since weapon crafting is not queued, we need to block
	 * the player from initiating a craft should they not have
	 * the materials.
	 */
	@Override
	public boolean requiresMaterialsToSubmitCraftRequest() {
		return true;
	}
	
	@Override
	public void addCraftingInformationToTooltip(ArrayList<String> tooltip) {
		int seconds = (tileEntity.craftingDuration - tileEntity.craftingTimer) / 20;
		tooltip.add(TextFormatting.GOLD + "Crafting: " + TextFormatting.WHITE
				+ I18n.format(tileEntity.craftingTargetName + ".name"));
		tooltip.add(TextFormatting.GOLD + "Time remaining: " + TextFormatting.WHITE
				+ GUIRenderHelper.formatTimeString(seconds, TimeUnit.SECONDS));
	}

	/**
	 * Rendering the weapons into the GUI requires a special
	 * consideration as we want them to be displayed as 3D models.
	 */
	@Override
	public boolean shouldOverrideCraftingModeOneRender() {
		return true;
	}
	
	@Override
	public void doCraftingModeOneRender(float partialTicks, int mouseX, int mouseY) {
		
		// This is just a backup check. This should only ever run if we are dealing
		// with crafting mode one, so it will always be a weapon.
		if(!(getSelectedCraftingPiece().getItem() instanceof Weapon)) return;
		
		Weapon weapon = (Weapon) getSelectedCraftingPiece().getItem();
        GuiRenderUtil.drawScaledString(fontRenderer, format(weapon.getUnlocalizedName()),
                        this.guiLeft + 214, this.guiTop + 31, 1.2, 0xFDF17C);
        GuiRenderUtil.drawScaledString(fontRenderer, weapon.builder.getWeaponType(), this.guiLeft + 214, this.guiTop + 43, 0.75,
                        0xC8C49C);
	}

}
