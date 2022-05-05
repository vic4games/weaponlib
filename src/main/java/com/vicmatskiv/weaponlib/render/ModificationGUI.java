package com.vicmatskiv.weaponlib.render;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Use;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.AttachmentCategory;
import com.vicmatskiv.weaponlib.ClientModContext;
import com.vicmatskiv.weaponlib.ItemAttachment;
import com.vicmatskiv.weaponlib.ModContext;
import com.vicmatskiv.weaponlib.PlayerWeaponInstance;
import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect;
import com.vicmatskiv.weaponlib.WeaponAttachmentAspect.FlaggedAttachment;
import com.vicmatskiv.weaponlib.shader.jim.Shader;
import com.vicmatskiv.weaponlib.shader.jim.ShaderManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import scala.util.Random;

public class ModificationGUI {

	public static ModificationGUI instance = new ModificationGUI();
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static ArrayList<ModificationTab> tabList = new ArrayList<>();

	private static final ModificationTab SCOPE_TAB = new ModificationTab("Sight", AttachmentCategory.SCOPE, 131, 200,
			ModificationGroup.ATTACHMENT);
	private static final ModificationTab BARREL_TAB = new ModificationTab("Muzzle", AttachmentCategory.SILENCER, 131,
			200, ModificationGroup.ATTACHMENT);
	private static final ModificationTab LASER_TAB = new ModificationTab("Laser", AttachmentCategory.LASER, 131, 200,
			ModificationGroup.ATTACHMENT);
	private static final ModificationTab GRIP_TAB = new ModificationTab("Grip", AttachmentCategory.GRIP, 131, 200,
			ModificationGroup.ATTACHMENT);

	private static final ModificationTab HANDGUARD_TAB = new ModificationTab("Handguard", AttachmentCategory.GUARD, 131,
			200, ModificationGroup.MODIFICATION);
	private static final ModificationTab FRONT_SIGHT_TAB = new ModificationTab("Front Sight",
			AttachmentCategory.FRONTSIGHT, 131, 200, ModificationGroup.MODIFICATION);
	private static final ModificationTab RECEIVER_TAB = new ModificationTab("Receiver", AttachmentCategory.RECEIVER,
			131, 200, ModificationGroup.MODIFICATION);
	private static final ModificationTab REAR_GRIP_TAB = new ModificationTab("Rear Grip", AttachmentCategory.BACKGRIP,
			131, 200, ModificationGroup.MODIFICATION);
	private static final ModificationTab STOCK_TAB = new ModificationTab("Stock", AttachmentCategory.STOCK, 131, 200,
			ModificationGroup.MODIFICATION);

	private ModificationTab activeTab;

	static {

		// attachment
		tabList.add(SCOPE_TAB);
		tabList.add(BARREL_TAB);
		tabList.add(LASER_TAB);
		tabList.add(GRIP_TAB);

		// modification
		tabList.add(HANDGUARD_TAB);
		tabList.add(FRONT_SIGHT_TAB);
		tabList.add(RECEIVER_TAB);
		tabList.add(REAR_GRIP_TAB);
		tabList.add(STOCK_TAB);

		// customization
	}

	private boolean waitingForMouseRelease = true;
	private boolean isInClick = false;
	
	private boolean dropdownCancel = false;
	
	// Integer of side tab hovered (i.e. mod/attach mode)
	private int tabHovered = -1;

	private ModificationGroup currentGroup = ModificationGroup.MODIFICATION;

	public double getGUIScale() {

		return 0.3;
	}

	public static enum ModificationGroup {
		ATTACHMENT, MODIFICATION, CUSTOMIZATION;

		public static ModificationGroup fromID(int id) {
			switch (id) {
			case 0:
				return ATTACHMENT;
			case 1:
				return MODIFICATION;
			case 2:
				return CUSTOMIZATION;
			}
			return ATTACHMENT;
		}
	}

	public static String translate(String unlocalized) {
		return new TextComponentTranslation(unlocalized + ".name").getFormattedText();
	}

	private static class TooltipBuilder {
		private StringBuilder sb = new StringBuilder();

		public void addLine(String line) {
			sb.append(line + "\n");
		}

		public String[] getLines() {
			return getText().split("\n");
		}

		public String getText() {
			return sb.toString();
		}

	}

	private static class TexturedRect {
		private double x, y, u, v, textureWidth, textureHeight, width, height, scale;

		
		private int selectedU, selectedV;
		
		
		public TexturedRect(double x, double y, double u, double v, double width, double height, double textureWidth,
				double textureHeight, double scale) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.scale = scale;
			this.u = u;
			this.v = v;
			this.textureHeight = textureHeight;
			this.textureWidth = textureWidth;
		}
		
		
		public TexturedRect withSelectedVariant(int u, int v) {
			this.selectedU = u;
			this.selectedV = v;
			return this;
		}

		public void render() {

			// 149, 254
			drawTexturedScaledRect(x, y, (float) u, (float) v, width, height, textureWidth, textureHeight, scale);
			// drawTexturedRect(x, y, u, v, width, height, textureWidth, textureHeight);
		}
		
		public void renderSelected() {
			drawTexturedScaledRect(x, y, (float) selectedU, (float) selectedV, width, height, textureWidth, textureHeight, scale);
	
		}

		public boolean checkBounding(double guiX, double guiY, int mouseX, int mouseY, double guiScale) {
			return checkInBox(mouseX, mouseY, guiX + this.x * guiScale, guiY + this.y * guiScale,
					this.width * scale * guiScale, this.height * scale * guiScale);
		}
	}

	public static class ModificationTab {
		private String title;
		private AttachmentCategory category;
		private double x, y;

		private boolean isDropDownOpen = false;
		private int page;

		private ModificationGroup group;

		public ModificationTab(String title, AttachmentCategory category, double x, double y, ModificationGroup group) {
			this.x = x;
			this.y = y;
			this.group = group;
			this.title = title;
			this.category = category;
		}

		public void setDropdown(boolean down) {
			this.isDropDownOpen = down;
		}

		public void nextPage(int max) {

			// System.out.println(max);
			if (page + 1 > max / 4) {
				page = 0;
			} else {
				page++;
			}

		}

		public void previousPage() {
			if (page == 0)
				return;
			page--;

		}

		public void setPos(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	public static ModificationGUI getInstance() {
		return instance;
	}

	public static double radarMappings[] = new double[] { 1, 4, 6, 5, 8, 9, 7, 5 };

	public RadarChart radarChart = new RadarChart("3", 0x1abc9c, 0.5f, 50, 5)
			.withTitles(new String[] { "Damage", "Recoil", "Inaccuracy", "Firerate", "Velocity" });

	public void setGroup(ModificationGroup group) {
		this.currentGroup = group;
	}

	public void render(ModContext modContext) {

		ScaledResolution scaledresolution = new ScaledResolution(mc);
		final int scaledWidth = scaledresolution.getScaledWidth();
		final int scaledHeight = scaledresolution.getScaledHeight();
		int mouseX = Mouse.getX() * scaledWidth / mc.displayWidth;
		int mouseY = scaledHeight - Mouse.getY() * scaledHeight / mc.displayHeight - 1;

		GlStateManager.pushMatrix();

		mc.setIngameNotInFocus();
		/*
		 * ResourceLocation modifiTex = new
		 * ResourceLocation("mw:textures/gui/modificationguisheet.png");
		 * GlStateManager.enableAlpha(); GlStateManager.enableBlend();
		 * 
		 * 
		 * GlStateManager.pushMatrix(); GlStateManager.translate(30, 30, 0);
		 */

		// Item item =
		// modContext.getMainHeldWeapon().getAttachmentItemWithCategory(AttachmentCategory.RAILING);

		// if(item == null) item = Items.DIAMOND;

		// quickItemRender(item, 6, 6, 1.5);
		// GlStateManager.enableTexture2D();

		// mc.getTextureManager().bindTexture(modifiTex);

		/*
		 * GlStateManager.scale(0.3, 0.3, 0.3);
		 * 
		 * GlStateManager.enableBlend(); GlStateManager.color(1, 1, 1, 0.8f);
		 * drawTexturedRect(0, 0, 0, 0, 311, 108, 512, 512);
		 * 
		 * 
		 * GlStateManager.color(1.0f, 0.8f, 0.5f, 1); drawTexturedRect(0, 111, 40, 240,
		 * 109, 28, 512, 512);
		 * 
		 * //mc.getRenderItem().
		 * 
		 * 
		 * GlStateManager.popMatrix();
		 */

		// drawScaledString("Railing", 30, 15, 1, 0xffffff);
		// GlStateManager.p

		if (!waitingForMouseRelease && Mouse.isButtonDown(0)) {
			// System.out.println("hi");
			waitingForMouseRelease = true;
			isInClick = true;
		} else if (!Mouse.isButtonDown(0)) {
			waitingForMouseRelease = false;
		}

		SCOPE_TAB.setPos(-50, 50);
		BARREL_TAB.setPos(120, 75);
		LASER_TAB.setPos(150, 0);
		GRIP_TAB.setPos(100, -50);

		RECEIVER_TAB.setPos(-50, 50);
		FRONT_SIGHT_TAB.setPos(120, 50);
		HANDGUARD_TAB.setPos(145, 0);
		REAR_GRIP_TAB.setPos(0, -50);
		STOCK_TAB.setPos(-100, -50);

		if (activeTab != null) {
			tabList.remove(activeTab);
			tabList.add(activeTab);
		}

		for (ModificationTab mt : tabList) {
			if (mt.group != currentGroup)
				continue;
			// if(mt == activeTab) continue;
			if (mt == activeTab) {
			//	 GlStateManager.translate(0, 0, 1000);
			}
		
			drawModificationTab(scaledresolution, mt, mouseX, mouseY, modContext.getMainHeldWeapon(), mt.category,
					modContext, mt == activeTab);
		
			if (mt == activeTab) {
			//	 GlStateManager.translate(0, 0, -1000);
			}
		}
		if (activeTab != null) {
			// drawModificationTab(scaledresolution, activeTab, mouseX, mouseY,
			// modContext.getMainHeldWeapon(), activeTab.category, modContext);
		}

		// ItemAttachment<Weapon> attach =
		// modContext.getMainHeldWeapon().getAttachmentItemWithCategory(AttachmentCategory.GUARD);

		// boolean result = WeaponAttachmentAspect.isAttachmentInUse(attach,
		// modContext.getMainHeldWeapon());
		// System.out.println(result);
		// drawTexturedRect(0, 0, 0, 0, width, height, textureWidth, textureHeight);

		// System.out.println(SCOPE_TAB.category);
		// drawTexturedModalRect(30, 30, 0, 0, 311, 109);

		

		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		double scale = 0.8;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.disableTexture2D();
		drawColoredRectangle(20, 20, 115, 175, 0.3, 0x000000);
		drawColoredRectangle(140.5, 20, 7.5, 175, 0.3, 0x000000);
		
		drawColoredRectangle(20, 200, 128, 125, 0.3, 0x000000);

		

		if (Minecraft.getMinecraft().player.ticksExisted % 50 == 0) {
			// System.out.println("UPLOADED");
			
			
			
			float firerate = modContext.getMainHeldWeapon().getFireRate();
			float inaccuracy = modContext.getMainHeldWeapon().getWeapon().getInaccuracy()/10f;
			float damage = modContext.getMainHeldWeapon().getWeapon().getSpawnEntityDamage()/20;
			float recoil = modContext.getMainHeldWeapon().getRecoil()/10f;
			
			damage = Math.min(damage, 1.0f);
			
			//float norm = MathHelper.sqrt(firerate*firerate + inaccuracy*inaccuracy + damage*damage + recoil*recoil + 0.14*0.14);
			
			
			//norm = 20f;
			//float norm = (firerate + inaccuracy + damage + recoil)/4f;
		//	firerate /= norm;
			//inaccuracy /= norm;
			//damage /= norm;
			//recoil /= norm;
			
		//	firerate = 0.9f;
			
			radarChart.uploadSet(new float[] { damage, recoil, inaccuracy, firerate, 0.14f });
		}

		GlStateManager.disableTexture2D();
		radarChart.render(84, 275.5, mouseX, mouseY, scale);

		GlStateManager.enableTexture2D();
		
		drawScaledString(
				TextFormatting.GOLD + "Weapon Stats",
				30, 205, 1.0, 0xffffff);
		
		drawScaledString(
				TextFormatting.GOLD + new TextComponentTranslation(modContext.getMainHeldWeapon().getWeapon().getUnlocalizedName() + ".name")
						.getFormattedText(),
				30, 30, 1.0, 0xffffff);
		drawScaledString(
				"Damage :: " + TextFormatting.GOLD + modContext.getMainHeldWeapon().getWeapon().getSpawnEntityDamage(),
				30, 60, 1, 0xffffff);
		drawScaledString("Recoil :: " + TextFormatting.GOLD + modContext.getMainHeldWeapon().getWeapon().getRecoil(),
				30, 75, 1, 0xffffff);
		drawScaledString("Firerate :: " + TextFormatting.GOLD + modContext.getMainHeldWeapon().getFireRate(), 30, 90, 1,
				0xffffff);
		drawScaledString(
				"Inaccuracy :: " + TextFormatting.GOLD + modContext.getMainHeldWeapon().getWeapon().getInaccuracy(), 30,
				105, 1, 0xffffff);

		GlStateManager.popMatrix();

		GlStateManager.disableTexture2D();
		ResourceLocation modifiTex = new ResourceLocation("mw:textures/gui/modificationguisheet.png");

		
		tabHovered = -1;
		GlStateManager.color(1, 1, 1);
		for (int groupID = 0; groupID < 2; ++groupID) {
			GlStateManager.disableTexture2D();
			drawColoredRectangle(scaledresolution.getScaledWidth_double() - 15,
					scaledresolution.getScaledHeight_double() - 75 - (18 * groupID), 15, 15, 0.5, 0x00000);

			GlStateManager.enableTexture2D();
			mc.getTextureManager().bindTexture(modifiTex);
			TexturedRect groupSelector = new TexturedRect(scaledresolution.getScaledWidth_double() - 13,
					scaledresolution.getScaledHeight_double() - 73.5 - (18 * groupID), 413, 356, 46, 46, 768, 768,
					0.25);
			// drawTexturedScaledRect(scaledresolution.getScaledWidth_double()-13,
			// scaledresolution.getScaledHeight_double()- 73.5 - yOffset, 413, 356, 46, 46,
			// 768, 768, 0.25);
			groupSelector.render();

			if (groupSelector.checkBounding(0, 0, mouseX, mouseY, 1.0) || (tabHovered == -1 && ModificationGroup.fromID(groupID) == currentGroup)) {
				tabHovered = groupID;
				TexturedRect groupSelector2 = new TexturedRect(scaledresolution.getScaledWidth_double() - 13,
						scaledresolution.getScaledHeight_double() - 73.5 - (18 * groupID), 413, 402, 46, 46, 768, 768,
						0.25);
				// drawTexturedScaledRect(scaledresolution.getScaledWidth_double()-13,
				// scaledresolution.getScaledHeight_double()- 73.5 - yOffset, 413, 356, 46, 46,
				// 768, 768, 0.25);
				groupSelector2.render();
				String text = ModificationGroup.fromID(groupID).toString();
				text = text.toLowerCase();
				text = text.substring(0, 1).toUpperCase() + text.substring(1);
				text += " Mode";
				
				
				//GlStateManager.pushMatrix();
				GlStateManager.color(1, 1, 1, 0.5f);
				mc.getTextureManager().bindTexture(modifiTex);
				drawTexturedScaledRect(scaledresolution.getScaledWidth_double() - 134, scaledresolution.getScaledHeight_double() - 74.5 - (18 * groupID), 90, 624, 390, 45, 768, 768, 0.3);
				GlStateManager.color(1, 1, 1, 1);

				
				drawScaledString(text,
						scaledresolution.getScaledWidth_double() - 3 - mc.fontRenderer.getStringWidth(text),
						scaledresolution.getScaledHeight_double() - 75 - (18 * groupID) + mc.fontRenderer.FONT_HEIGHT/2.0, scale, 0xffffff);

				
				if(isInClick) {
					
					activeTab = null;
					currentGroup = ModificationGroup.fromID(groupID);
				}
			}
		}

		/*
		 * for(double yOffset = 0; yOffset < 40; yOffset += 16) {
		 * GlStateManager.disableTexture2D();
		 * //drawColoredRectangle(scaledresolution.getScaledWidth_double()-15,
		 * scaledresolution.getScaledHeight_double()-75 - yOffset, 15, 15, 0.5,
		 * 0x00000); GlStateManager.enableTexture2D(); GlStateManager.color(1f, 1f, 1f);
		 * mc.getTextureManager().bindTexture(modifiTex);
		 * 
		 * drawTexturedScaledRect(scaledresolution.getScaledWidth_double()-13,
		 * scaledresolution.getScaledHeight_double()- 73.5 - yOffset, 413, 356, 46, 46,
		 * 768, 768, 0.25); }
		 */

		// Reset click detection
				isInClick = false;
		
		GlStateManager.enableTexture2D();
	}

	public static boolean checkInBox(double tX, double tY, double x, double y, double width, double height) {
		return tX >= x && tX <= x + width && tY >= y && tY <= y + height;
	}

	public static boolean checkInRadius(double tX, double tY, double x, double y, double r) {
		double x1 = x - tX;
		double y1 = y - tY;
		return Math.sqrt(x1 * x1 + y1 * y1) <= r;
	}

	public void drawModificationTab(ScaledResolution sr, ModificationTab tab, int mouseX, int mouseY,
			PlayerWeaponInstance pwi, AttachmentCategory category, ModContext modcontext, boolean primary) {

		float guiTransparency = 0.7f;

		if (activeTab != null && tab != activeTab) {
			guiTransparency = 0.3f;
		}

		double scale = 0.3;
		double x = sr.getScaledWidth_double() / 2 - tab.x;
		double y = sr.getScaledHeight_double() / 2 - tab.y;
		String title = tab.title;
		category = tab.category;

		double sheetSize = 768;

		// Set up layout
		TexturedRect primaryElement = new TexturedRect(0, 0, 0, 0, 311, 108, sheetSize, sheetSize, 1);
		TexturedRect redElement = new TexturedRect(0, 0, 311, 0, 311, 108, sheetSize, sheetSize, 1);

		TexturedRect dropdownSelect = new TexturedRect(0, 111, 40, 254, 109, 28, sheetSize, sheetSize, 1)
				.withSelectedVariant(188, 254);
		
		TexturedRect dropdownSelectRed = new TexturedRect(0, 111, 350, 300, 109, 28, sheetSize, sheetSize, 1)
				.withSelectedVariant(350, 328);
		
		//TexturedRect dropdownSelectHovered = new TexturedRect(0, 111, 188, 254, 109, 28, sheetSize, sheetSize, 1);

		TexturedRect dropdownMenu = new TexturedRect(0, 140, 0, 108, 434, 109, sheetSize, sheetSize, 1)
				.withSelectedVariant(0, 478);
		TexturedRect dropdownBar = new TexturedRect(0, 250, 0, 222, 435, 32, sheetSize, sheetSize, 1)
				.withSelectedVariant(0, 592);
		TexturedRect leftArrow = new TexturedRect(165, 256.5, 0, 254, 40, 46, sheetSize, sheetSize, 0.4);
		TexturedRect rightArrow = new TexturedRect(255, 256.5, 149, 254, 39, 46, sheetSize, sheetSize, 0.4);

		ArrayList<FlaggedAttachment> inventory = modcontext.getAttachmentAspect().getInventoryAttachments(category,
				pwi);

		// Checks to see if they all
		// require parts
		boolean allInventoryRequired = true;
		for (FlaggedAttachment flag : inventory) {
			if (!flag.requiresAnyParts()) {
				allInventoryRequired = false;
				break;
			}
		}
		if (inventory.isEmpty())
			allInventoryRequired = false;

		boolean dropdownHovered = dropdownSelect.checkBounding(x, y, mouseX, mouseY, scale);

		// Bind texture
		ResourceLocation modifiTex = new ResourceLocation("mw:textures/gui/modificationguisheet.png");
		mc.getTextureManager().bindTexture(modifiTex);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0.0);
		GlStateManager.scale(scale, scale, scale);

		// Set color & slight transparency
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, guiTransparency);

		// System.out.println(isInClick);
		if (dropdownHovered && isInClick) {
			tab.isDropDownOpen = !tab.isDropDownOpen;
			if (tab.isDropDownOpen) {
				activeTab = tab;

				for (ModificationTab mt : tabList) {
					if (mt != tab)
						mt.isDropDownOpen = false;
				}

			} else {
				activeTab = null;
			}
		}

		// Setup tooltip
		TooltipBuilder tooltip = new TooltipBuilder();
		boolean requiresTooltip = false;

		// TexturedRect dropdownMenu = new TexturedRect
		// GlStateManager.color(1, 0.4f, 0.2f, 0.8f);
		if (allInventoryRequired) {
			redElement.render();
		} else {
			primaryElement.render();
		}
		ItemAttachment<Weapon> primaryAttachment = pwi.getAttachmentItemWithCategory(category);

		boolean lockOutState = WeaponAttachmentAspect.isRequired(primaryAttachment, modcontext.getMainHeldWeapon());

		// primaryElement.render();
		// GlStateManager.color(1, 1f, 1f, 0.8f);

		TexturedRect primarySelector = new TexturedRect(11, 10, 0, 389, 89, 89, sheetSize, sheetSize, 1);
		TexturedRect lockOut = new TexturedRect(11, 10, 0, 624, 89, 89, sheetSize, sheetSize, 1);

	//	System.out.println(dropdownCancel);
		if (!dropdownCancel && primaryAttachment != null && primarySelector.checkBounding(x, y, mouseX, mouseY, scale)) {
			GlStateManager.color(1, 1, 1, 0.5f);
			if (!lockOutState)
				primarySelector.render();

			requiresTooltip = true;
			tooltip.addLine(
					new TextComponentTranslation(primaryAttachment.getUnlocalizedName() + ".name").getFormattedText());

			if (lockOutState) {
				ArrayList<ItemAttachment<Weapon>> requirees = WeaponAttachmentAspect.whatRequiredFor(primaryAttachment,
						pwi);
				tooltip.addLine(TextFormatting.BOLD + "Is Required By:");
				for (ItemAttachment<Weapon> req : requirees)
					tooltip.addLine(
							"• " + new TextComponentTranslation(req.getUnlocalizedName() + ".name").getFormattedText());
			}

			if (isInClick) {
				modcontext.getAttachmentAspect().forceAttachment(category, modcontext.getMainHeldWeapon(),
						ItemStack.EMPTY);

			}
			GlStateManager.color(1, 1, 1, guiTransparency);

		}

		if (lockOutState) {
			GlStateManager.color(1, 1, 1, 0.9f);

			lockOut.render();
			GlStateManager.color(1, 1, 1, guiTransparency);

		}
		// Render tab
		// drawTexturedRect(0, 0, 0, 0, 311, 108, 512, 512);

		// Render dropdown
		
		if(!allInventoryRequired) {
			if (!dropdownHovered) {
				dropdownSelect.render();
				// GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);
			} else {
				dropdownSelect.renderSelected();
				//dropdownSelectHovered.render();
				// GlStateManager.color(1.0f, 0.8f, 0.7f, 0.8f);
			}
		} else {
			if (!dropdownHovered) {
				dropdownSelectRed.render();
				// GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);
			} else {
				dropdownSelectRed.renderSelected();
				//dropdownSelectHovered.render();
				// GlStateManager.color(1.0f, 0.8f, 0.7f, 0.8f);
			}
		}
		
		

		/*
		TexturedRect permanencyAlert = new TexturedRect(118, 50, 26, 713, 26, 26, sheetSize, sheetSize, 1.4);
		if (!modcontext.getMainHeldWeapon().getWeapon().isCategoryRemovable(category))
			permanencyAlert.render();

		if (permanencyAlert.checkBounding(x, y, mouseX, mouseY, scale)) {
			requiresTooltip = true;
			tooltip.addLine("Permanent Category");
			tooltip.addLine(TextFormatting.ITALIC + "Can never be empty");
		}
		*/

		TexturedRect moreItemsAlert = new TexturedRect(75, 114, 0, 713, 26, 26, sheetSize, sheetSize, 0.8);
		if (inventory.size() > 0)
			moreItemsAlert.render();

		// drawTexturedRect(0, 111, 40, 254, 109, 28, 512, 512);
		GlStateManager.color(1.0f, 1.0f, 1.0f, guiTransparency);

		if(activeTab == tab) {
			dropdownCancel = false;
			if(tab.isDropDownOpen && dropdownMenu.checkBounding(x, y, mouseX, mouseY, scale)) {
				dropdownCancel = true;
			}
		}
		
		if (tab.isDropDownOpen) {
			
			
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 100);
			if(!allInventoryRequired) {
				dropdownMenu.render();
			} else {
				dropdownMenu.renderSelected();
			}
			GlStateManager.popMatrix();
			//GlStateManager.translate(0, 0, -1000);
			
			// drawTexturedRect(0, 140, 0, 108, 434, 109, 512, 512);
			// drawTexturedRect(0, 250, 0, 222, 435, 32, 512, 512);
			if(!allInventoryRequired) {
				dropdownBar.render();
			} else {
				dropdownBar.renderSelected();
			}
			//dropdownBar.render();

			if (leftArrow.checkBounding(x, y, mouseX, mouseY, scale)) {

				if (isInClick)
					tab.previousPage();
				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);

			}
			leftArrow.render();
			GlStateManager.color(1, 1, 1, 1);
			/*
			 * if(!checkInBox(mouseX, mouseY, x + 165*scale, y + 256.5*scale, 40*0.4*scale,
			 * 46*0.4*scale)) { drawTexturedScaledRect(165, 256.5, 0, 254, 40, 46, 512, 512,
			 * 0.4);
			 * 
			 * }
			 */
			if (rightArrow.checkBounding(x, y, mouseX, mouseY, scale)) {
				if (isInClick)
					tab.nextPage(inventory.size());

				GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);

			}
			rightArrow.render();
			GlStateManager.color(1, 1, 1, 1);
			if (!checkInBox(mouseX, mouseY, x + 255 * scale, y + 256.5, 40 * 0.4 * scale, 46 * 0.4 * scale)) {
				// drawTexturedScaledRect(255, 256.5, 149, 254, 40, 46, 512, 512, 0.4);

			}

			drawScaledString("Pg. " + (tab.page + 1), 188.5, 256.5, 2.5, 0xffffff);

		}

		GlStateManager.enableBlend();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.color(0.5f, 1, 1, 0.3f);

		if (dropdownHovered) {
			drawScaledString(title, 125, 10, 2.5, 0xfeca57);

		} else {
			// GlStateManager.color(0.5f, 1, 1, 0.3f);
			drawScaledString(title, 125, 10, 2.5, 0xffffff);

		}

		// Primary item render
		ItemAttachment<Weapon> current = pwi.getAttachmentItemWithCategory(category);
	
		if (current != null) {
			GlStateManager.color(1, 1, 1, 1);
			
			//Shader shadla = ShaderManager.loadVMWShader("alpha");
			//shadla.use();
			
			boolean obscurityCheck = false;
			for(ModificationTab mt : this.tabList) {
				if(activeTab == null) break;
				if(mt == tab || tab == activeTab) continue;
				//System.out.println(tab.y - mt.y);
				if(tab.y < mt.y) {
					obscurityCheck = true;
					break;
				}
			}
			
			obscurityCheck = false;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, -15);
			quickItemRender(current, 13, 13, 5, obscurityCheck);
			GlStateManager.popMatrix();
			//shadla.release();
			
		}

		// System.out.println("hi");

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 50);
		if (tab.isDropDownOpen) {

			double i = 0;
			int startingPosition = tab.page * 4;
			for (int g = startingPosition; g < Math.min(inventory.size(), startingPosition + 4); ++g) {

				FlaggedAttachment flag = inventory.get(g);

				
				quickItemRender(flag.getAttachment(), i + 13, 150, 5, false);

				// RenderHelper.disableStandardItemLighting();
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				mc.getTextureManager().bindTexture(modifiTex);

				// TexturedRect redBlockade = new TexturedRect(i + 11, 150, 0, 300, 89, 89, 512,
				// 512, 1);

				if (flag.requiresAnyParts()) {
					TexturedRect redBlockade = new TexturedRect(i + 11, 150, 0, 300, 89, 89, sheetSize, sheetSize, 1);
					redBlockade.render();

					if (redBlockade.checkBounding(x, y, mouseX, mouseY, scale)) {
						requiresTooltip = true;

						tooltip.addLine(
								new TextComponentTranslation(flag.getAttachment().getUnlocalizedName() + ".name")
										.getFormattedText());
						tooltip.addLine(TextFormatting.BOLD + "Required Mods: ");

						for (ItemAttachment<Weapon> required : flag.getRequiredParts()) {
							tooltip.addLine("• " + new TextComponentTranslation(required.getUnlocalizedName() + ".name")
									.getFormattedText());
						}
					}
					// drawTexturedRect(i+11, 150, 0, 300, 89, 89, 512, 512);

				} else {
					TexturedRect selector = new TexturedRect(i + 11, 150, 0, 389, 89, 89, sheetSize, sheetSize, 1);
					if (selector.checkBounding(x, y, mouseX, mouseY, scale)) {
						GlStateManager.color(1, 1, 1, 0.5f);
						selector.render();

						requiresTooltip = true;
						tooltip.addLine(
								new TextComponentTranslation(flag.getAttachment().getUnlocalizedName() + ".name")
										.getFormattedText());

						if (isInClick) {
							modcontext.getAttachmentAspect().forceAttachment(category, modcontext.getMainHeldWeapon(),
									flag.getItemStack());

						}
					}
					// if(redBlockade)
				}
				// checkInBox(mouseX, mouseY, x + i*scale + 11*scale, y + 150*scale, 89*scale,
				// 89*scale)

				// if(checkInBox(mouseX, mouseY, x + (i + 11), y + 150, 89, 89)) {
				// }

				i += 108;

			}
		}
		
		GlStateManager.popMatrix();

		if (allInventoryRequired) {
			drawScaledString("Mods required", 119, 50, 3.0, 0xff3f34);
		}

		GlStateManager.popMatrix();

		if (requiresTooltip) {
			GlStateManager.pushMatrix();

			// Translate to front
			GlStateManager.translate(0, 0, 1000);

			// tooltip.addLine("fuck off");

			// Bind tooltip texture
			mc.getTextureManager().bindTexture(modifiTex);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 0.5f);

			String[] args = tooltip.getLines();

			double maxStringWidth = 0.0;
			double maxStringHeight = args.length * 12;

			for (String s : args) {
				double len = mc.fontRenderer.getStringWidth(s) + 5.0;

				if (len > maxStringWidth) {
					maxStringWidth = len;
				}
			}

			// Draw tooltip background
			GlStateManager.disableTexture2D();
			
			drawColoredRectangle(mouseX, mouseY, maxStringWidth, maxStringHeight, 0.5, 0x2f3640);
			//drawTexturedScaledRect(mouseX, mouseY, 89, 300, maxStringWidth, maxStringHeight, sheetSize, sheetSize, 0.5);

			// System.out.println(tooltip.getText());
			
			GlStateManager.enableTexture2D();
			int space = 0;
			for (String splitted : args) {
				drawScaledString(splitted, mouseX + 2, mouseY + 2 + space, 1.0, 0xffffff);
				space += 10;
			}

			
			GlStateManager.popMatrix();
		}

	}

	public void drawScaledString(String text, double x, double y, double scale, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, scale);

		mc.fontRenderer.drawStringWithShadow(text, 0, 0, color);

		GlStateManager.popMatrix();
	}

	public static void quickItemRender(Item item, double x, double y, double scale, boolean depthUse) {
		GlStateManager.pushMatrix();

		
		
		
		if(depthUse) GlStateManager.disableDepth();
	
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(scale, scale, 1.0);
		RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(new ItemStack(item), 0, 0);
		RenderHelper.disableStandardItemLighting();
		
		
		if(depthUse) GlStateManager.enableDepth();
		
		GlStateManager.popMatrix();
	}

	public static void drawTexturedRect(double x, double y, float u, float v, double width, double height,
			double textureWidth, double textureHeight) {
		float f = (float) (1.0F / textureWidth);
		float f1 = (float) (1.0F / textureHeight);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D)
				.tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();

	}

	public static void drawColoredRectangle(double x, double y, double width, double height, double alpha, int color) {

		float r = (float) (((color & 0xFF0000) >> 16) / 255.0);
		float g = (float) (((color & 0xFF00) >> 8) / 255.0);
		float b = (float) ((color & 0xFF) / 255.0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) x, (double) (y + height), 0.0D).color(r, g, b, (float) alpha).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).color(r, g, b, (float) alpha).endVertex();
		bufferbuilder.pos((double) (x + width), (double) y, 0.0D).color(r, g, b, (float) alpha).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).color(r, g, b, (float) alpha).endVertex();
		tessellator.draw();
	}

	public static void drawTexturedScaledRect(double x, double y, float u, float v, double width, double height,
			double textureWidth, double textureHeight, double scale) {
		float f = (float) (1.0F / textureWidth);
		float f1 = (float) (1.0F / textureHeight);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) x, (double) (y + height * scale), 0.0D)
				.tex((double) (u * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width * scale), (double) (y + height * scale), 0.0D)
				.tex((double) ((u + (float) width) * f), (double) ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((double) (x + width * scale), (double) y, 0.0D)
				.tex((double) ((u + (float) width) * f), (double) (v * f1)).endVertex();
		bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
		tessellator.draw();
	}

	/*
	 * RENDER TOOLS
	 */

}
