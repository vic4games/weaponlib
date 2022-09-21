package com.vicmatskiv.weaponlib.crafting.base;

import java.util.ArrayList;

import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGuiContainer;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.ammopress.ContainerAmmoPress;
import com.vicmatskiv.weaponlib.crafting.ammopress.TileEntityAmmoPress;
import com.vicmatskiv.weaponlib.crafting.workbench.GUIButtonCustom;
import com.vicmatskiv.weaponlib.crafting.workbench.GUIContainerWorkbench;
import com.vicmatskiv.weaponlib.crafting.workbench.TileEntityWorkbench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;

public class GUIContainerStation<T extends TileEntityStation> extends CompatibleGuiContainer {
	
	protected T tileEntity;
	
	// Current item to have a tooltip render.
	private ArrayList<String> tooltipRenderItem = new ArrayList<>();

	protected static ModContext modContext;

	
	// Currently used crafting list.
	protected ArrayList<IModernCrafting> filteredCraftingList = new ArrayList<>();


	// The page the workbench is on
	private int page = 1;
	
	private int minPage, maxPage;
	
	
	// Color pallette
	protected static final int GRAY = 0x7B7B7B;
	protected static final int RED = 0xA95E5F;
	protected static final int GOLD = 0xFDF17C;
	protected static final int BLUE = 0x8FC5E3;
	protected static final int GREEN = 0x97E394;
	protected static final int LIGHT_GREY = 0xDADADA;
	
	
	public GUIContainerStation(Container c) {
		super(c);
	}
	
	public static void setModContext(ModContext context) {
		modContext = context;
	}

	public void setPageRange(int min, int max) {
		this.minPage = min;
		this.maxPage = max;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public String format(String unloc) {
		return I18n.format(unloc + ".name");
	}
	
	public void fillFilteredList() {};
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		this.tooltipRenderItem.clear();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		if (tooltipRenderItem != null && !tooltipRenderItem.isEmpty())
			drawHoveringText(tooltipRenderItem, mouseX, mouseY);
	}
	
	@SuppressWarnings("unchecked")
	public void setItemRenderTooltip(ItemStack stack, String... strings) {

		if(stack.isEmpty()) return;
		
		this.tooltipRenderItem.clear();
		this.tooltipRenderItem.add(format(stack.getItem().getUnlocalizedName()));

		ITooltipFlag flag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips
				? ITooltipFlag.TooltipFlags.ADVANCED
				: ITooltipFlag.TooltipFlags.NORMAL;
		stack.getItem().addInformation(stack, this.tileEntity.getWorld(), this.tooltipRenderItem, flag);
		if (strings.length > 0)
			this.tooltipRenderItem.addAll(Arrays.asList(strings));
	}
	
	protected void setPage(int id) {

		// Lock to minimum page
		if (id < minPage)
			id = minPage;

		// Lock to maximum page
		if (id > maxPage)
			id = maxPage;

		this.page = id;
		((ContainerStation) this.inventorySlots).page = id;
		for (GuiButton b : this.buttonList) {
			if (b instanceof GUIButtonCustom && ((GUIButtonCustom) b).getPageID() != -1) {
				b.visible = ((GUIButtonCustom) b).getPageID() == id;
			}
		}
	}
	
	public void render3DItemInGUI(Item item, int x, int y, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, 100.0F);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(20.0F, 20.0F, 20.0F);

		GlStateManager.rotate(15 + mouseY*0.01f, 1, 0, 0);
		GlStateManager.rotate(120 + mouseX*0.01f, 0, 1, 0);
		GlStateManager.rotate(0, 0, 0, 1);

		GlStateManager.scale(4, 4, 4);
		GlStateManager.enableLighting();
		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(item),
				TransformType.THIRD_PERSON_LEFT_HAND);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}


}
