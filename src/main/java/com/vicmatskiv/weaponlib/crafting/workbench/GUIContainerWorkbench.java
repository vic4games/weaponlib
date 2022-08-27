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
import com.vicmatskiv.weaponlib.crafting.CraftingGroup;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.crafting.IModernCrafting;
import com.vicmatskiv.weaponlib.crafting.items.CraftingItem;
import com.vicmatskiv.weaponlib.network.packets.WorkbenchPacket;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper.StringAlignment;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import scala.actors.threadpool.Arrays;

public class GUIContainerWorkbench extends CompatibleGuiContainer {

	private static ModContext modContext;

	// GUI Textures
	private static final ResourceLocation GUI_TEX = new ResourceLocation("mw:textures/gui/workshop_sheet.png");
	private static final ResourceLocation GUI_INV_TEX = new ResourceLocation("mw:textures/gui/workbench_inv_sheet.png");

	// Color pallette
	private static final int GRAY = 0x7B7B7B;
	private static final int RED = 0xA95E5F;
	private static final int GOLD = 0xFDF17C;
	private static final int BLUE = 0x8FC5E3;
	private static final int GREEN = 0x97E394;
	private static final int LIGHT_GREY = 0xDADADA;

	private TileEntityWorkbench teWorkbench;

	// Buttons & Search box
	private GUIButtonCustom craftButton, leftArrow, rightArrow, assaultSelector, attachSelector, modSelector,
			dismantleButton;
	private CustomSearchTextField searchBox;

	// Currently selected crafting piece
	private IModernCrafting selectedCraftingPiece = null;

	// Currently used crafting list.
	private ArrayList<IModernCrafting> filteredCraftingList = new ArrayList<>();

	// Scroll bar data
	private boolean scrollBarGrabbed = false;
	private double scrollBarProgress;
	private int scrollOffsetAtGrab, grabY;
	private int scrollBarOffset = 0;

	// Current item to have a tooltip render.
	private ArrayList<String> tooltipRenderItem = new ArrayList<>();

	// The page the workbench is on
	private int page = 1;

	// Tells us what kind of stuff we're lookin to craft
	// Guns = 1, Attachments = 2, Modification Attachments = 3
	private int craftingMode = 1;
	
	private boolean hasRequiredItems = false;

	private HashMap<Item, Boolean> hasAvailiableMaterials = new HashMap<>();

	public static void setModContext(ModContext context) {
		modContext = context;
	}

	public GUIContainerWorkbench(EntityPlayer player, InventoryPlayer inventory,
			TileEntityWorkbench tileEntityWorkbench) {
		super(new ContainerWorkbench(player, inventory, tileEntityWorkbench));
		this.xSize = 402;
		this.ySize = 240;

		filteredCraftingList = new ArrayList<IModernCrafting>();
		filteredCraftingList.addAll(CraftingRegistry.getWeaponCraftingRegistry());

		this.teWorkbench = tileEntityWorkbench;

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if (button == craftButton) {

			if (selectedCraftingPiece != null && teWorkbench.craftingTimer == -1) {

				modContext.getChannel().getChannel()
						.sendToServer(new WorkbenchPacket(WorkbenchPacket.CRAFT, teWorkbench.getPos(), 0, craftingMode == 1 ? WorkbenchBlock.WORKBENCH_WEAPON_CRAFTING_TIME : WorkbenchBlock.WORKBENCH_ATTACHMENT_CRAFTING_TIME,
								CraftingGroup.getValue(craftingMode),
								selectedCraftingPiece.getItem().getUnlocalizedName()));

			}

		} else if (button == dismantleButton) {

			modContext.getChannel().getChannel().sendToServer(
					new WorkbenchPacket(WorkbenchPacket.DISMANTLE, teWorkbench.getPos(), 0, WorkbenchBlock.WORKBENCH_DISMANTLING_TIME, null, ""));

		} else if (button == leftArrow) {

			setPage(page - 1);
		} else if (button == rightArrow) {
			setPage(page + 1);

			if (page == 2) {
				teWorkbench.pushInventoryRefresh = true;
			}

		} else if (button == assaultSelector) {
			((GUIButtonCustom) button).toggleOn();
			modSelector.toggleOff();
			attachSelector.toggleOff();
			craftingMode = 1;

			selectedCraftingPiece = null;

			filteredCraftingList.clear();
			filteredCraftingList.addAll(CraftingRegistry.getWeaponCraftingRegistry());
		} else if (button == attachSelector) {
			((GUIButtonCustom) button).toggleOn();
			modSelector.toggleOff();
			assaultSelector.toggleOff();
			craftingMode = 2;

			selectedCraftingPiece = null;

			filteredCraftingList.clear();
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());
			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() == CraftingGroup.ATTACHMENT_MODIFICATION);
		} else if (button == modSelector) {
			((GUIButtonCustom) button).toggleOn();
			attachSelector.toggleOff();
			assaultSelector.toggleOff();
			filteredCraftingList.addAll(CraftingRegistry.getAttachmentCraftingRegistry());

			filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.ATTACHMENT_MODIFICATION);
			craftingMode = 3;
		}
	}

	@SuppressWarnings("unchecked")
	public void setItemRenderTooltip(ItemStack stack, String... strings) {

		if(stack.isEmpty()) return;
		
		this.tooltipRenderItem.clear();
		this.tooltipRenderItem.add(format(stack.getItem().getUnlocalizedName()));

		ITooltipFlag flag = Minecraft.getMinecraft().gameSettings.advancedItemTooltips
				? ITooltipFlag.TooltipFlags.ADVANCED
				: ITooltipFlag.TooltipFlags.NORMAL;
		stack.getItem().addInformation(stack, this.teWorkbench.getWorld(), this.tooltipRenderItem, flag);
		if (strings.length > 0)
			this.tooltipRenderItem.addAll(Arrays.asList(strings));
	}

	public String format(String unloc) {
		return I18n.format(unloc + ".name");
	}

	@Override
	public void initGui() {
		super.initGui();

		this.searchBox = new CustomSearchTextField(0, this.fontRenderer, this.guiLeft + 15, this.guiTop + 32, 84, 13);
		this.searchBox.setMaxStringLength(50);
		this.searchBox.setEnableBackgroundDrawing(true);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(16777215);

		craftButton = new GUIButtonCustom(GUI_TEX, 0, this.guiLeft + 211, this.guiTop + 179, 53, 17, 480, 370, "CRAFT")
				.withStandardState(GRAY, 0, 240).withHoveredState(GOLD, 0, 257).withDisabledState(RED, 0, 274)
				.withPageRestriction(2);

		leftArrow = new GUIButtonCustom(GUI_TEX, 1, 0, this.guiTop + 96, 28, 33, 480, 370, "")
				.withStandardState(0xFFFFFF, 57, 284).withHoveredState(0xFFFFFF, 85, 284)
				.withDisabledCheck(() -> this.page == 1);

		rightArrow = new GUIButtonCustom(GUI_TEX, 2,
				new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - 28, this.guiTop + 96, 28, 33, 480,
				370, "").withStandardState(0xFFFFFF, 57, 317).withHoveredState(0xFFFFFF, 85, 317)
						.withDisabledCheck(() -> this.page == 2);

		assaultSelector = new GUIButtonCustom(GUI_TEX, 3, this.guiLeft + 107, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 291).withHoveredState(0xFFFFFF, 19, 291)
				.withToggledState(0xFFFFFF, 38, 291).withPageRestriction(2).makeToggleButton();

		attachSelector = new GUIButtonCustom(GUI_TEX, 4, this.guiLeft + 130, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 311).withHoveredState(0xFFFFFF, 19, 311)
				.withToggledState(0xFFFFFF, 38, 311).withPageRestriction(2).makeToggleButton();

		modSelector = new GUIButtonCustom(GUI_TEX, 5, this.guiLeft + 154, this.guiTop + 29, 19, 20, 480, 370, "")
				.withStandardState(0xFFFFFF, 0, 331).withHoveredState(0xFFFFFF, 19, 331)
				.withToggledState(0xFFFFFF, 38, 331).withPageRestriction(2).makeToggleButton();

		dismantleButton = new GUIButtonCustom(GUI_INV_TEX, 6, this.guiLeft + 286, this.guiTop + 70, 73, 17, 480, 370,
				"DISMANTLE").withStandardState(GRAY, 0, 283).withHoveredState(GOLD, 0, 300)
						.withDisabledState(RED, 0, 317).withPageRestriction(1);

		assaultSelector.toggleOn();

		addButton(craftButton);
		addButton(assaultSelector);
		addButton(attachSelector);
		addButton(modSelector);
		addButton(leftArrow);
		addButton(rightArrow);
		addButton(dismantleButton);

		setPage(1);
	}

	private void setPage(int id) {

		// Lock to minimum page
		if (id < 1)
			id = 1;

		// Lock to maximum page
		if (id > 2)
			id = 2;

		this.page = id;
		((ContainerWorkbench) this.inventorySlots).page = id;
		for (GuiButton b : this.buttonList) {
			if (b instanceof GUIButtonCustom && ((GUIButtonCustom) b).getPageID() != -1) {
				b.visible = ((GUIButtonCustom) b).getPageID() == id;
			}
		}
	}

	public void onSelectNewCrafting(IModernCrafting crafting) {
		ItemStack[] modernRecipe = crafting.getModernRecipe();

		HashMap<Item, Integer> counter = new HashMap<>();
		for (int i = 22; i < teWorkbench.mainInventory.getSlots(); ++i) {
			ItemStack stack = teWorkbench.mainInventory.getStackInSlot(i);
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
		for (ItemStack is : modernRecipe) {
			if (!counter.containsKey(is.getItem())) {
				hasRequiredItems = false;
				hasAvailiableMaterials.put(is.getItem(), false);
			} else if (is.getCount() > counter.get(is.getItem())) {
				hasRequiredItems = false;
				hasAvailiableMaterials.put(is.getItem(), false);
			} else {
				hasAvailiableMaterials.put(is.getItem(), true);
			}
		}

		this.craftButton.setDisabled(!hasRequiredItems);

	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		this.tooltipRenderItem.clear();

		if (teWorkbench.pushInventoryRefresh) {
			teWorkbench.pushInventoryRefresh = false;
			if (this.selectedCraftingPiece != null)
				onSelectNewCrafting(this.selectedCraftingPiece);
		}

		if (!this.craftButton.isDisabled() && teWorkbench.getProgress() != 0) {
			this.craftButton.setDisabled(true);
		}
			
		
		
		
		if(this.selectedCraftingPiece != null && hasRequiredItems && teWorkbench.getProgress() == 0) {
			this.craftButton.setDisabled(false);
		}
		
		

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		// this.guiLeft + 304, this.guiTop + 185, 53f, 240f, 81, 11,
		ArrayList<String> strings = new ArrayList<>();

		if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 304, this.guiTop + 185, 81, 11)
				&& teWorkbench.craftingTimer != -1) {
			int seconds = (teWorkbench.craftingDuration - teWorkbench.craftingTimer) / 20;
			strings.add(TextFormatting.GOLD + "Crafting: " + TextFormatting.WHITE
					+ I18n.format(teWorkbench.craftingTargetName + ".name"));
			strings.add(TextFormatting.GOLD + "Time remaining: " + TextFormatting.WHITE
					+ GUIRenderHelper.formatTimeString(seconds, TimeUnit.SECONDS));
		}

		if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 261, this.guiTop + 57, 122, 7)) {
			for (int i = 0; i < 4; ++i) {
				if (teWorkbench.dismantleStatus[i] == -1 || teWorkbench.dismantleDuration[i] == -1)
					continue;
				if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 261 + i * 31, this.guiTop + 57, 29, 7)) {
					ItemStack stack = teWorkbench.mainInventory.getStackInSlot(i + 9);
					Item item = stack.getItem();
					int seconds = (teWorkbench.dismantleDuration[i] - teWorkbench.dismantleStatus[i]) / 20;
					strings.add(TextFormatting.BLUE + "Dismantling: " + TextFormatting.WHITE + I18n.format(
							teWorkbench.mainInventory.getStackInSlot(i + 9).getItem().getUnlocalizedName() + ".name"));
					strings.add(TextFormatting.BLUE + "Time remaining: " + TextFormatting.WHITE
							+ GUIRenderHelper.formatTimeString(seconds, TimeUnit.SECONDS));
					strings.add(TextFormatting.BLUE + "Products:");
					for (ItemStack s : ((IModernCrafting) item).getModernRecipe()) {
						int count = (int) Math.round(s.getCount() * (s.getItem() instanceof CraftingItem
								? ((CraftingItem) s.getItem()).getRecoveryPercentage()
								: 1.0));
						strings.add(TextFormatting.GOLD + "" + count + "x " + TextFormatting.WHITE
								+ I18n.format(s.getItem().getUnlocalizedName() + ".name"));
					}

				}

			}
		}

		if (!strings.isEmpty())
			drawHoveringText(strings, mouseX, mouseY);

		if (page == 2) {
			this.searchBox.drawTextBox();
			if (tooltipRenderItem != null && !tooltipRenderItem.isEmpty())
				drawHoveringText(tooltipRenderItem, mouseX, mouseY);

		}

	}

	@Override
	protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.compatibleMouseClicked(mouseX, mouseY, mouseButton);
		this.searchBox.mouseClicked(mouseX, mouseY, mouseButton);

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
					modContext.getChannel().getChannel().sendToServer(new WorkbenchPacket(WorkbenchPacket.MOVE_OUTPUT,
							teWorkbench.getPos(), Minecraft.getMinecraft().player.getEntityId(), i));
					break;
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		boolean cancelationFlag = this.searchBox.getText().length() == 0 && keyCode == Keyboard.KEY_BACK;
		
		super.keyTyped(typedChar, keyCode);
		this.searchBox.textboxKeyTyped(typedChar, keyCode);
		
		
		if(cancelationFlag) return;
		
		
		if(keyCode == Keyboard.KEY_BACK) {
			filteredCraftingList = new ArrayList<>();
			if (craftingMode == 1) {
				filteredCraftingList.addAll((ArrayList<Weapon>) CraftingRegistry.getWeaponCraftingRegistry().clone());
			} else if (craftingMode == 2) {
				filteredCraftingList.addAll((ArrayList<Weapon>) CraftingRegistry.getAttachmentCraftingRegistry().clone());
				filteredCraftingList.removeIf((s) -> s.getCraftingGroup() == CraftingGroup.ATTACHMENT_MODIFICATION);
			} else {
				filteredCraftingList.addAll((ArrayList<Weapon>) CraftingRegistry.getAttachmentCraftingRegistry().clone());
				filteredCraftingList.removeIf((s) -> s.getCraftingGroup() != CraftingGroup.ATTACHMENT_MODIFICATION);
			}
		}
		

		if (searchBox.getText().length() != 0) {
			// Filter out bad results.
			filteredCraftingList.removeIf((a) -> !I18n.format(a.getItem().getUnlocalizedName() + ".name").toLowerCase()
					.contains(searchBox.getText().toLowerCase()));
		}
	}

	public Weapon getSelectedWeaponIDForGUI() {
		return (Weapon) selectedCraftingPiece;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		
		drawDefaultBackground();
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		GlStateManager.enableBlend();

		if (page == 1) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_INV_TEX);
			drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0f, 0f, 402, 232, 480, 370);

			for (int i = 0; i < 4; ++i) {
				if (teWorkbench.dismantleStatus[i] == -1 || teWorkbench.dismantleDuration[i] == -1)
					continue;

				double progress = teWorkbench.dismantleStatus[i] / (double) teWorkbench.dismantleDuration[i];
				drawModalRectWithCustomSizedTexture(this.guiLeft + 261 + i * 31, this.guiTop + 57, 81, 232, 29, 7, 480,
						370);
				drawModalRectWithCustomSizedTexture(this.guiLeft + 261 + i * 31, this.guiTop + 57, 81, 239,
						(int) (29 * progress), 7, 480, 370);

			}

			GUIRenderHelper.drawScaledString("INVENTORY", this.guiLeft + 10, this.guiTop + 5, 1.2, BLUE);
			GUIRenderHelper.drawScaledString("DISMANTLING", this.guiLeft + 255, this.guiTop + 5, 1.2, BLUE);
			GUIRenderHelper.drawScaledString("Player Inventory", this.guiLeft + 21, this.guiTop + 115, 1.0, LIGHT_GREY);

		} else if (page == 2) {

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
			double progress = (0.025) * (Math.round(teWorkbench.getProgress() / (0.025)));
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
			
						
						Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(
								new ItemStack(filteredCraftingList.get(c).getItem()), this.guiLeft + 15 + (x * 23),
								this.guiTop + 55 + (y * 23));
						
						
						Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
						
						c += 1;
					}
				}

				
			}


			if (craftingMode == 1 && getSelectedWeaponIDForGUI() != null) {
				render3DItemInGUI(getSelectedWeaponIDForGUI(), this.guiLeft + 300, this.guiTop + 65, mouseX, mouseY);
			}
			
			if (craftingMode == 1 && selectedCraftingPiece != null) {
				Weapon weapon = getSelectedWeaponIDForGUI();
				GuiRenderUtil.drawScaledString(fontRenderer, format(weapon.getUnlocalizedName()),
						this.guiLeft + 214, this.guiTop + 31, 1.2, 0xFDF17C);
				GuiRenderUtil.drawScaledString(fontRenderer, weapon.builder.getWeaponType(), this.guiLeft + 214, this.guiTop + 43, 0.75,
						0xC8C49C);

			} else if (craftingMode > 1 && selectedCraftingPiece != null) {

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
				IModernCrafting weapon = selectedCraftingPiece;
				if (weapon.getModernRecipe() != null && weapon.getModernRecipe().length != 0) {
					int c = 0;
					for (ItemStack stack : weapon.getModernRecipe()) {
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

								setItemRenderTooltip(stack, formatColor + "" + percentage + "% Yield");
							} else {
								setItemRenderTooltip(stack, TextFormatting.GREEN + "100% Yield");

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

						Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);

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
				Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);

				if (GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 40 + (i * 22), this.guiTop + 219, 20, 20)) {
					GUIRenderHelper.drawTexturedRect(this.guiLeft + 39 + (i * 22), this.guiTop + 218, playerInventoryFull ? 18 : 0, 351,
							18, 18, 480, 370);
					setItemRenderTooltip(teWorkbench.mainInventory.getStackInSlot(i));

				}
				Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(teWorkbench.mainInventory.getStackInSlot(i),
						this.guiLeft + 40 + (i * 22), this.guiTop + 219);

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
	public void onGuiClosed() {
		super.onGuiClosed();
	}

}
