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

public class GUIContainerAmmoPress extends GUIContainerStation<TileEntityAmmoPress> {

	
	// GUI Textures
	private static final ResourceLocation GUI_TEX = new ResourceLocation("mw:textures/gui/workshop_sheet.png");
	private static final ResourceLocation GUI_INV_TEX = new ResourceLocation("mw:textures/gui/workbench_inv_sheet.png");
	private static final ResourceLocation AMMO_PRESS_TEX = new ResourceLocation("mw:textures/gui/ammosheet.png");

	


	// Buttons & Search box
	private GUIButtonCustom craftButton, leftArrow, rightArrow, bulletSelector, magazineSelector,
			dismantleButton;
	
	
	private CustomSearchTextField searchBox, quantityBox;

	
	
	// Currently selected crafting piece
	private IModernCrafting selectedCraftingPiece = null;

	
	// Scroll bar data
	private boolean scrollBarGrabbed = false;
	private double scrollBarProgress;
	private int scrollOffsetAtGrab, grabY;
	private int scrollBarOffset = 0;



	// Tells us what kind of stuff we're lookin to craft
	// Bullets = 1; Magazines = 2;
	private int craftingMode = 1;
	
	private boolean hasRequiredItems = false;

	private HashMap<Item, Boolean> hasAvailiableMaterials = new HashMap<>();

	

	public GUIContainerAmmoPress(EntityPlayer player, InventoryPlayer inventory,
			TileEntityAmmoPress tileEntityWorkbench) {
		super(new ContainerAmmoPress(player, inventory, tileEntityWorkbench));
		this.xSize = 402;
		this.ySize = 240;
		
		fillFilteredList();

		
		this.tileEntity = tileEntityWorkbench;
		setPageRange(1, 2);
	}
	
	@Override
	public void fillFilteredList() {
		if(craftingMode == 1) {
			filteredCraftingList = new ArrayList<IModernCrafting>();
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());
			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.BULLET);
		} else if(craftingMode == 2) {
			filteredCraftingList = new ArrayList<IModernCrafting>();
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());
			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.MAGAZINE);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button == craftButton) {

			if (selectedCraftingPiece != null && quantityBox.getText().length() != 0) {

				int quantity = Integer.parseInt(quantityBox.getText());
				
				
			
				modContext.getChannel().getChannel()
						.sendToServer(new StationPacket(StationPacket.CRAFT, tileEntity.getPos(), selectedCraftingPiece.getItem().getUnlocalizedName(), selectedCraftingPiece.getCraftingGroup(), quantity));

				
			}

		} else if (button == dismantleButton) {

			modContext.getChannel().getChannel().sendToServer(
					new StationPacket(StationPacket.DISMANTLE, tileEntity.getPos(), 0, WorkbenchBlock.WORKBENCH_DISMANTLING_TIME, null, ""));

		} else if (button == leftArrow) {

			setPage(getPage() - 1);
		} else if (button == rightArrow) {
			setPage(getPage() + 1);
			if (getPage() == 2) tileEntity.pushInventoryRefresh = true;

		} else if (button == bulletSelector) {
			((GUIButtonCustom) button).toggleOn();
			magazineSelector.toggleOff();
			craftingMode = 1;
		
			selectedCraftingPiece = null;

			fillFilteredList();
		} else if (button == magazineSelector) {
			((GUIButtonCustom) button).toggleOn();
			bulletSelector.toggleOff();
			craftingMode = 2;

			selectedCraftingPiece = null;

			fillFilteredList();
		}
	}

	

	@Override
	public void initGui() {
		super.initGui();

		this.searchBox = new CustomSearchTextField(GUI_TEX, "Search Items...", 0, 0, this.fontRenderer, this.guiLeft + 15, this.guiTop + 32, 84, 13);
		this.searchBox.setMaxStringLength(50);
		this.searchBox.setEnableBackgroundDrawing(true);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(16777215);
		
		this.quantityBox = new CustomSearchTextField(AMMO_PRESS_TEX, "Amt.", 1, 1, this.fontRenderer, this.guiLeft + 267, this.guiTop + 183, 84, 13);
		this.quantityBox.setMaxStringLength(50);
		this.quantityBox.setEnableBackgroundDrawing(true);
		this.quantityBox.setVisible(true);
		this.quantityBox.setTextColor(16777215);

		craftButton = new GUIButtonCustom(GUI_TEX, 0, this.guiLeft + 211, this.guiTop + 179, 53, 17, 480, 370, "CRAFT")
				.withStandardState(GRAY, 0, 240).withHoveredState(GOLD, 0, 257).withDisabledState(RED, 0, 274)
				.withPageRestriction(2);

		leftArrow = new GUIButtonCustom(GUI_TEX, 1, 0, this.guiTop + 96, 28, 33, 480, 370, "")
				.withStandardState(0xFFFFFF, 57, 284).withHoveredState(0xFFFFFF, 85, 284)
				.withDisabledCheck(() -> this.getPage() == 1);

		rightArrow = new GUIButtonCustom(GUI_TEX, 2,
				new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - 28, this.guiTop + 96, 28, 33, 480,
				370, "").withStandardState(0xFFFFFF, 57, 317).withHoveredState(0xFFFFFF, 85, 317)
						.withDisabledCheck(() -> this.getPage() == 2);

		bulletSelector = new GUIButtonCustom(AMMO_PRESS_TEX, 3, this.guiLeft + 107, this.guiTop + 29, 19, 20, 256, 256, "")
				.withStandardState(0xFFFFFF, 0, 0).withHoveredState(0xFFFFFF, 19, 0)
				.withToggledState(0xFFFFFF, 38, 0).withPageRestriction(2).makeToggleButton();

		magazineSelector = new GUIButtonCustom(AMMO_PRESS_TEX, 4, this.guiLeft + 130, this.guiTop + 29, 19, 20, 256, 256, "")
				.withStandardState(0xFFFFFF, 0, 20).withHoveredState(0xFFFFFF, 19, 20)
				.withToggledState(0xFFFFFF, 38, 20).withPageRestriction(2).makeToggleButton();


		dismantleButton = new GUIButtonCustom(GUI_INV_TEX, 6, this.guiLeft + 286, this.guiTop + 70, 73, 17, 480, 370,
				"DISMANTLE").withStandardState(GRAY, 0, 283).withHoveredState(GOLD, 0, 300)
						.withDisabledState(RED, 0, 317).withPageRestriction(1);

		bulletSelector.toggleOn();

		addButton(craftButton);
		addButton(bulletSelector);
		addButton(magazineSelector);
		addButton(leftArrow);
		addButton(rightArrow);
		addButton(dismantleButton);

		setPage(1);
	}

	

	public void onSelectNewCrafting(IModernCrafting crafting) {
		CraftingEntry[] modernRecipe = crafting.getModernRecipe();

		HashMap<Item, Integer> counter = new HashMap<>();
		for (int i = 22; i < tileEntity.mainInventory.getSlots(); ++i) {
			ItemStack stack = tileEntity.mainInventory.getStackInSlot(i);
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				if (!counter.containsKey(item)) {
					counter.put(item, stack.getCount());
				} else {
					counter.put(item, counter.get(item) + stack.getCount());
				}
			}
		}

		hasRequiredItems = true;
		
		
		
		for (CraftingEntry is : modernRecipe) {
			if(!is.isOreDictionary()) {
				if (!counter.containsKey(is.getItem())) {
					hasRequiredItems = false;
					hasAvailiableMaterials.put(is.getItem(), false);
				} else if (is.getCount() > counter.get(is.getItem())) {
					hasRequiredItems = false;
					hasAvailiableMaterials.put(is.getItem(), false);
				} else {
					hasAvailiableMaterials.put(is.getItem(), true);
				}
			} else {
				NonNullList<ItemStack> list = OreDictionary.getOres(is.getOreDictionaryEntry());
				boolean foundSomething = false;
				for(ItemStack toTest : list) {
					if(counter.containsKey(toTest.getItem()) && toTest.getCount() <= counter.get(toTest.getItem())) {
						foundSomething = true;
						hasAvailiableMaterials.put(is.getItem(), true);
						break;
					}
				}
				
				if(!foundSomething) {
					hasRequiredItems = false;
				}
			}
 			
		}

		//this.craftButton.setDisabled(!hasRequiredItems);

	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		
		if (tileEntity.pushInventoryRefresh) {
			tileEntity.pushInventoryRefresh = false;
			if (this.selectedCraftingPiece != null)
				onSelectNewCrafting(this.selectedCraftingPiece);
		}

		if (!this.craftButton.isDisabled() && tileEntity.getProgress() != 0) {
		//	this.craftButton.setDisabled(true);
		}
			
		
		
		
		if(this.selectedCraftingPiece != null && hasRequiredItems && tileEntity.getProgress() == 0) {
		//	this.craftButton.setDisabled(false);
		}
		
		

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		// this.guiLeft + 304, this.guiTop + 185, 53f, 240f, 81, 11,
		ArrayList<String> strings = new ArrayList<>();

		
		
		
	
		if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 304, this.guiTop + 185, 81, 11)
				&& tileEntity.craftingTimer != -1 && tileEntity.hasStack()) {
			int millis = (int) Math.round(((tileEntity.craftingDuration - tileEntity.craftingTimer) / 20.0)*1000);
			
			
			if(tileEntity.hasStack()) {
				strings.add(TextFormatting.GOLD + "Crafting: " + TextFormatting.WHITE
						+ I18n.format(tileEntity.getLatestStackInQueue().getUnlocalizedName() + ".name"));
				strings.add(TextFormatting.GOLD + "Quantity: " + TextFormatting.WHITE
						+ tileEntity.getLatestStackInQueue().getCount());
			}
			
		}

		if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 261, this.guiTop + 57, 122, 7)) {
			for (int i = 0; i < 4; ++i) {
				if (tileEntity.dismantleStatus[i] == -1 || tileEntity.dismantleDuration[i] == -1)
					continue;
				if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 261 + i * 31, this.guiTop + 57, 29, 7)) {
					ItemStack stack = tileEntity.mainInventory.getStackInSlot(i + 9);
					Item item = stack.getItem();
					int seconds = (tileEntity.dismantleDuration[i] - tileEntity.dismantleStatus[i]) / 20;
					strings.add(TextFormatting.BLUE + "Dismantling: " + TextFormatting.WHITE + I18n.format(
							tileEntity.mainInventory.getStackInSlot(i + 9).getItem().getUnlocalizedName() + ".name"));
					strings.add(TextFormatting.BLUE + "Time remaining: " + TextFormatting.WHITE
							+ GUIRenderHelper.formatTimeString(seconds, TimeUnit.SECONDS));
					strings.add(TextFormatting.BLUE + "Products:");
					for (CraftingEntry s : ((IModernCrafting) item).getModernRecipe()) {
						 
					
						
						int count = (int) Math.round(s.getCount() * (s.getItem() instanceof CraftingItem
								? ((CraftingItem) s.getItem()).getRecoveryPercentage()
								: 1.0));
						strings.add(TextFormatting.GOLD + "" + count + "x " + TextFormatting.WHITE
								+ I18n.format(s.getItem().getUnlocalizedName() + ".name"));
					}

				}

			}
		}

	
	
	
		int highlighted = -1;
		if(tileEntity.hasStack()) {
			if (mouseY >= this.guiTop && mouseY <= this.guiTop + 20) {
				int id = (mouseX - (this.guiLeft + 200))/20;
				if(id >= 0 && tileEntity.getCraftingQueue().size() - 1 >= id) {
					highlighted = id;
					strings.add(format(tileEntity.getCraftingQueue().get(id).getUnlocalizedName()));
					strings.add(TextFormatting.GRAY + "Quantity: " + TextFormatting.GOLD + tileEntity.getCraftingQueue().get(id).getCount());
				}
			}
		}
		
		if (getPage() == 2) {
			this.searchBox.drawTextBox();
			this.quantityBox.drawTextBox();
			
			
			LinkedList<ItemStack> queue = tileEntity.getCraftingQueue();
			for(int i = 0; i < queue.size(); ++i) {
				ItemStack stack = queue.get(i);
				GlStateManager.color(1, 1, 1);
				Minecraft.getMinecraft().getTextureManager().bindTexture(AMMO_PRESS_TEX);
				if(highlighted != -1 && highlighted == i) {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 200 + i*20, this.guiTop, 20, 40, 20, 20, 256, 256);
				} else {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 200 + i*20, this.guiTop, 0, 40, 20, 20, 256, 256);
				}
				
				Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, this.guiLeft + 202 + i*20, this.guiTop + 2);

				GUIRenderHelper.drawScaledString("x" + stack.getCount(), this.guiLeft + 212 + i*20, this.guiTop + 16, 0.7, BLUE);
			}
			
		}
		
		
		if (!strings.isEmpty())
			drawHoveringText(strings, mouseX, mouseY);
		
		

	}

	@Override
	protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.compatibleMouseClicked(mouseX, mouseY, mouseButton);
		this.searchBox.mouseClicked(mouseX, mouseY, mouseButton);
		this.quantityBox.mouseClicked(mouseX, mouseY, mouseButton);
		
		

		if(tileEntity.hasStack()) {
			if (mouseY >= this.guiTop && mouseY <= this.guiTop + 20) {
				int id = (mouseX - (this.guiLeft + 200))/20;
				if(id >= 0 && tileEntity.getCraftingQueue().size() - 1 >= id) {
					
					modContext.getChannel().getChannel().sendToServer(new StationPacket(StationPacket.POP_FROM_QUEUE, tileEntity.getPos(), Minecraft.getMinecraft().player.getEntityId(), id));
				}
			}
		}
		
		int c = (int) Math.floor(filteredCraftingList.size() * scrollBarProgress / 7) * 7;
		for (int y = 0; y < 6; ++y) {
			for (int x = 0; x < 7; ++x) {
				if (c == filteredCraftingList.size())
					break;

				boolean selected = GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 12 + (x * 23),
						this.guiTop + 52 + (y * 23), 22, 22);
				if (selected) {
					onSelectNewCrafting(filteredCraftingList.get(c));
					selectedCraftingPiece = filteredCraftingList.get(c);
				}
				c += 1;

			}
		}

		if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 40, this.guiTop + 219, 176, 20)) {
			for (int i = 0; i < 9; ++i) {
				if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 40 + (i * 22), this.guiTop + 219, 20,
						20)) {
					modContext.getChannel().getChannel().sendToServer(new StationPacket(StationPacket.MOVE_OUTPUT,
							tileEntity.getPos(), Minecraft.getMinecraft().player.getEntityId(), i));
					break;
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		 
	
		boolean cancelationForSearch = this.searchBox.getText().length() == 0 && keyCode == Keyboard.KEY_BACK;
		boolean cancelationForQuantity = this.quantityBox.getText().length() == 0 && keyCode == Keyboard.KEY_BACK;
		
		super.keyTyped(typedChar, keyCode);
		this.searchBox.textboxKeyTyped(typedChar, keyCode);
		if(Character.isDigit(typedChar) || keyCode == Keyboard.KEY_BACK) {
			this.quantityBox.textboxKeyTyped(typedChar, keyCode);
		}
		
		if((cancelationForSearch && this.searchBox.isFocused()) || (cancelationForQuantity && this.quantityBox.isFocused())) return;
		
		
		if(keyCode == Keyboard.KEY_BACK) {
			filteredCraftingList = new ArrayList<>();
			fillFilteredList();
		}
		
	
		if (searchBox.getText().length() != 0) {
			// Filter out bad results.
			filteredCraftingList.removeIf((a) -> !I18n.format(a.getItem().getUnlocalizedName() + ".name").toLowerCase()
					.contains(searchBox.getText().toLowerCase()));
		}
	}



	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		
		drawDefaultBackground();
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		GlStateManager.enableBlend();

		if (getPage() == 1) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_INV_TEX);
			drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0f, 0f, 402, 232, 480, 370);

			for (int i = 0; i < 4; ++i) {
				if (tileEntity.dismantleStatus[i] == -1 || tileEntity.dismantleDuration[i] == -1)
					continue;

				double progress = tileEntity.dismantleStatus[i] / (double) tileEntity.dismantleDuration[i];
				drawModalRectWithCustomSizedTexture(this.guiLeft + 261 + i * 31, this.guiTop + 57, 81, 232, 29, 7, 480,
						370);
				drawModalRectWithCustomSizedTexture(this.guiLeft + 261 + i * 31, this.guiTop + 57, 81, 239,
						(int) (29 * progress), 7, 480, 370);

			}

			GUIRenderHelper.drawScaledString("INVENTORY", this.guiLeft + 10, this.guiTop + 5, 1.2, BLUE);
			GUIRenderHelper.drawScaledString("DISMANTLING", this.guiLeft + 255, this.guiTop + 5, 1.2, BLUE);
			GUIRenderHelper.drawScaledString("Player Inventory", this.guiLeft + 21, this.guiTop + 115, 1.0, LIGHT_GREY);

		} else if (getPage() == 2) {

			GlStateManager.color(1f, 1f, 1f, 1f);

			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);

			GlStateManager.enableBlend();

			// Draw background elements
			drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0f, 0f, 192, 210, 480, 370);
			drawModalRectWithCustomSizedTexture(200 + this.guiLeft, 20 + this.guiTop, 200f, 20f, 202, 190, 480, 370);
			drawModalRectWithCustomSizedTexture(this.guiLeft, 213 + this.guiTop, 0f, 213f, 241, 27, 480, 370);

			// Draw progress bar
			// forty notches, therefore 1/40.0 = 0.025
			
			
			double prevProgress = (Math.max(tileEntity.prevCraftingTimer, 0)) / (double) tileEntity.craftingDuration;
			double currProgress = (Math.max(tileEntity.craftingTimer, 0)) / (double) tileEntity.craftingDuration;
			double intpProgress = InterpolationKit.interpolateValue(prevProgress, currProgress, Minecraft.getMinecraft().getRenderPartialTicks());
			
			double progress = (0.025) * (Math.round(intpProgress / (0.025)));
			drawModalRectWithCustomSizedTexture(this.guiLeft + 304, this.guiTop + 185, 53f, 240f, 81, 11, 480, 370);
			drawModalRectWithCustomSizedTexture(this.guiLeft + 304, this.guiTop + 185, 53f, 240f + 11, (int) (81 * progress), 11, 480,
					370);



		
			if (!Mouse.isButtonDown(0)) {
				scrollBarGrabbed = false;
			}

			
			// Handle scroll bar
			int scrollBarVertical = this.guiTop + 54 + scrollBarOffset;
			int scrollBarHeight = (int) (138 * Math.min(1.0, 42.0 / filteredCraftingList.size()));
			drawModalRectWithCustomSizedTexture(this.guiLeft + 176, scrollBarVertical, 412, 54, 6, scrollBarHeight, 480, 370);

			if (Mouse.isButtonDown(0)
					&& GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 176, scrollBarVertical, 6, scrollBarHeight)
					&& !scrollBarGrabbed) {
				scrollBarGrabbed = true;
				scrollOffsetAtGrab = scrollBarOffset;
				grabY = mouseY;
			}

			if (scrollBarGrabbed) {
				scrollBarOffset = scrollOffsetAtGrab + (mouseY - grabY);
				if (scrollBarOffset + scrollBarHeight > 138) {
					scrollBarOffset = 138 - scrollBarHeight;
				} else if (scrollBarOffset < 0)
					scrollBarOffset = 0;
			}

			// Calculate the new scroll bar progress.
			scrollBarProgress = scrollBarOffset / (138.0 - scrollBarHeight);
			if (Double.isNaN(scrollBarProgress))
				scrollBarProgress = 0.0;

			if (!filteredCraftingList.isEmpty()) {

				// Draw slots
				int c = (int) Math.floor(filteredCraftingList.size() * scrollBarProgress / 7) * 7;
				for (int y = 0; y < 6; ++y) {
					for (int x = 0; x < 7; ++x) {
						if (c == filteredCraftingList.size())
							break;

						if (filteredCraftingList.get(c) == selectedCraftingPiece) {
							drawModalRectWithCustomSizedTexture(this.guiLeft + 12 + (x * 23), this.guiTop + 52 + (y * 23), 97f - 44, 262f,
									22, 22, 480, 370);

						} else {
							boolean selected = GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 12 + (x * 23),
									this.guiTop + 52 + (y * 23), 22, 22);
							if (!selected) {
								drawModalRectWithCustomSizedTexture(this.guiLeft + 12 + (x * 23), this.guiTop + 52 + (y * 23), 97f - 22,
										262f, 22, 22, 480, 370);

							} else {
								setItemRenderTooltip(new ItemStack(filteredCraftingList.get(c).getItem()));
								drawModalRectWithCustomSizedTexture(this.guiLeft + 12 + (x * 23), this.guiTop + 52 + (y * 23), 97f, 262f,
										22, 22, 480, 370);
							}
						}
			
						RenderHelper.enableGUIStandardItemLighting();
						Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(
								new ItemStack(filteredCraftingList.get(c).getItem()), this.guiLeft + 15 + (x * 23),
								this.guiTop + 55 + (y * 23));
						RenderHelper.disableStandardItemLighting();
						
						Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
						
						c += 1;
					}
				}

				
			}


			if (craftingMode == 1 && selectedCraftingPiece != null) {
				render3DItemInGUI(selectedCraftingPiece.getItem(), this.guiLeft + 300, this.guiTop + 65, mouseX, mouseY);
			}
			
			if (selectedCraftingPiece != null) {

				GuiRenderUtil.drawScaledString(fontRenderer,
						format(selectedCraftingPiece.getItem().getUnlocalizedName()), this.guiLeft + 214, this.guiTop + 31,
						0.9, 0xFDF17C);
				GlStateManager.pushMatrix();
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.translate(this.guiLeft + 275, this.guiTop + 45, 0);
				GlStateManager.scale(3, 3, 3);

				Minecraft.getMinecraft().getRenderItem()
						.renderItemIntoGUI(new ItemStack(selectedCraftingPiece.getItem()), 0, 0);
				GlStateManager.popMatrix();
			}
			
			GlStateManager.enableBlend();

			// Draw all the text within the GUI
			GuiRenderUtil.drawScaledString(fontRenderer, "CRAFTING", this.guiLeft + 11, this.guiTop + 5, 1.2, 0x8FC5E3);
			GuiRenderUtil.drawScaledString(fontRenderer, "Progress", this.guiLeft + 326, this.guiTop + 175, 0.8, 0xFFFFFF);
			GuiRenderUtil.drawScaledString(fontRenderer, "Output", this.guiLeft + 7, this.guiTop + 223, 0.9, 0xFFFFFF);
			GuiRenderUtil.drawScaledString(fontRenderer, "CRAFT", this.guiLeft + 222, this.guiTop + 184, 1.0, 0xB06061);
			GuiRenderUtil.drawScaledString(fontRenderer, "Results: " + TextFormatting.YELLOW + "" + filteredCraftingList.size(), this.guiLeft + 12, this.guiTop + 191,
					0.8, 0xFFFFFF);

			if (selectedCraftingPiece != null) {
				IModernCrafting modernCraftingPiece = selectedCraftingPiece;
				if (modernCraftingPiece.getModernRecipe() != null && modernCraftingPiece.getModernRecipe().length != 0) {
					int c = 0;
					for (CraftingEntry stack : modernCraftingPiece.getModernRecipe()) {
						ItemStack itemStack = new ItemStack(stack.getItem());
						Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);

						boolean hasItem = this.hasAvailiableMaterials.get(stack.getItem());

						int x = this.guiLeft + 210 + (c * 20);
						int y = this.guiTop + 122;

						if (GUIRenderHelper.checkInBox(mouseX, mouseY, x, y, 15, 15)) {

							Item item = stack.getItem();
							if (item instanceof CraftingItem) {

								TextFormatting formatColor;
								int percentage = ((int) Math
										.round(((CraftingItem) item).getRecoveryPercentage() * 100));
								if (percentage <= 25) {
									formatColor = TextFormatting.RED;
								} else if (percentage <= 50) {
									formatColor = TextFormatting.GOLD;
								} else if (percentage <= 75) {
									formatColor = TextFormatting.YELLOW;
								} else {
									formatColor = TextFormatting.GREEN;
								}

								setItemRenderTooltip(itemStack, formatColor + "" + percentage + "% Yield");
							} else {
								setItemRenderTooltip(itemStack, TextFormatting.GREEN + "100% Yield");

							}

							GlStateManager.enableTexture2D();
							Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
							GlStateManager.enableBlend();

						}

						GlStateManager.pushMatrix();
						GlStateManager.color(1f, 1f, 1f);
						GlStateManager.translate(x + 0.5, y + 0.5, 0);
						GlStateManager.scale(0.125, 0.125, 0.125);
						if (hasItem) {
							drawModalRectWithCustomSizedTexture(0, 0, 242f, 215f, 113, 114, 480, 370);
						} else {
							drawModalRectWithCustomSizedTexture(0, 0, 358f, 215f, 113, 114, 480, 370);
						}
						GlStateManager.popMatrix();

						Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(itemStack, x, y);

						GUIRenderHelper.drawScaledString("x" + stack.getCount(), x + 8, y + 12, 0.6,
								hasItem ? GREEN : RED);

						c += 1;
					}
				}

			}
		

				


			GlStateManager.popMatrix();
			
			

			boolean playerInventoryFull = Minecraft.getMinecraft().player.inventory.getFirstEmptyStack() == -1;
			if (playerInventoryFull) {
				GUIRenderHelper.drawAlignedString("Inventory Full!", StringAlignment.LEFT, false, this.guiLeft + 245, this.guiTop + 214,
						1.0, RED);
			}
			for (int i = 0; i < 9; ++i) {
				ItemStack stack = tileEntity.mainInventory.getStackInSlot(i);
				Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);

				if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 40 + (i * 22), this.guiTop + 219, 20, 20)) {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 39 + (i * 22), this.guiTop + 218, playerInventoryFull ? 18 : 0, 351,
							18, 18, 480, 370);
					setItemRenderTooltip(stack);

				}
				Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack,
						this.guiLeft + 40 + (i * 22), this.guiTop + 219);

				if(!stack.isEmpty()) {
					GUIRenderHelper.drawScaledString(tileEntity.mainInventory.getStackInSlot(i).getCount() + "", this.guiLeft + 50 + (i * 22), this.guiTop + 230, 1, 0xFFFFFF);
					
				}
				
			}

		}

	}
	
	
	

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

}
