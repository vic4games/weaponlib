package com.vicmatskiv.weaponlib.crafting.workbench;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.Weapon;
import com.vicmatskiv.weaponlib.WeaponRenderer;
import com.vicmatskiv.weaponlib.animation.gui.GuiRenderUtil;
import com.vicmatskiv.weaponlib.compatibility.CompatibleGuiContainer;
import com.vicmatskiv.weaponlib.crafting.CraftingRegistry;
import com.vicmatskiv.weaponlib.render.ModificationGUI;
import com.vicmatskiv.weaponlib.render.gui.GUIRenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GUIContainerWorkbench extends CompatibleGuiContainer {
	
	
	private static final int GRAY = 0x7B7B7B;
	private static final int RED = 0x8FC5E3;
	private static final int GOLD = 0xFDF17C;
	
	private static final ResourceLocation GUI_TEX = new ResourceLocation("mw:textures/gui/workshop_sheet.png");

	private EntityPlayer player;
	private InventoryPlayer inventory;
	
	private TileEntityWorkbench teWorkbench;
	
	private CustomSearchTextField searchBox;
	private GUIButtonCustom craftButton;
	
	private Weapon selectedWeapon = null;
	
	private Weapon superficialSelectedWeapon = null;
	
	private ArrayList<Weapon> filteredWeaponsList = new ArrayList<>();
	
	
	private double scrollBarProgress;
	
	// Scroll bar grabbed
	private boolean scrollBarGrabbed = false;
	private int grabX, grabY;
	private int scrollBarOffset = 0;
	
	private Instant start;
	
	public GUIContainerWorkbench(EntityPlayer player, InventoryPlayer inventory, TileEntityWorkbench tileEntityWorkbench) {
		super(new ContainerWorkbench(player, inventory, tileEntityWorkbench));
		
		this.player = player;
		this.inventory = inventory;
		
		this.xSize = 400;
		this.ySize = 250;
		
		filteredWeaponsList = (ArrayList<Weapon>) CraftingRegistry.getCraftingRegistry();
		
		this.teWorkbench = tileEntityWorkbench;
		
		start = Instant.now();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		// TODO Auto-generated method stub
		super.actionPerformed(button);
		
		if(button == craftButton) {
	
			this.teWorkbench.craftingStart = Instant.now();
			this.teWorkbench.craftingLength = Duration.ofMinutes(1);
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

		//tX + 211, tY + 179
		this.craftButton = new GUIButtonCustom(GUI_TEX, 0, this.guiLeft + 211, this.guiTop + 179, 53, 17, 480, 370, "CRAFT")
				.withStandardState(GRAY, 0, 240)
				.withHoveredState(GOLD, 0, 257)
				.withDisabledState(RED, 0, 274);
		
	//	System.out.println(this.craftButton.y);
		this.addButton(this.craftButton);
		
		
		this.searchBox = new CustomSearchTextField(0, this.fontRenderer, this.guiLeft + 15, this.guiTop + 32, 84, 13);
        this.searchBox.setMaxStringLength(50);
        this.searchBox.setEnableBackgroundDrawing(true);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        
        
        
        GUIButtonCustom leftArrow = new GUIButtonCustom(GUI_TEX, 1, 0, this.guiTop + 96, 28, 33, 480, 370, "")
        		.withStandardState(0xFFFFFF, 57, 284);
        
        this.addButton(leftArrow);
		
        GUIButtonCustom rightArrow = new GUIButtonCustom(GUI_TEX, 1, new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - 28, this.guiTop + 96, 28, 33, 480, 370, "")
        		.withStandardState(0xFFFFFF, 57, 317);
        
        this.addButton(rightArrow);
        
        GUIButtonCustom assaultSelector = new GUIButtonCustom(GUI_TEX, 2, this.guiLeft + 107, this.guiTop + 29, 19, 20, 480, 370, "")
        		.withStandardState(0xFFFFFF, 0, 291)
        		.withHoveredState(0xFFFFFF, 19, 291);
        
        GUIButtonCustom attachSelector = new GUIButtonCustom(GUI_TEX, 2, this.guiLeft + 130, this.guiTop + 29, 19, 20, 480, 370, "")
        		.withStandardState(0xFFFFFF, 0, 311)
        		.withHoveredState(0xFFFFFF, 19, 311);
        
        GUIButtonCustom modSelector = new GUIButtonCustom(GUI_TEX, 2, this.guiLeft + 154, this.guiTop + 29, 19, 20, 480, 370, "")
        		.withStandardState(0xFFFFFF, 0, 331)
        		.withHoveredState(0xFFFFFF, 19, 331);
        
        this.addButton(assaultSelector);
        this.addButton(attachSelector);
        this.addButton(modSelector);
        
		//this.guiLeft = 40;
	//	this.guiTop = 40;
	}
	
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.searchBox.drawTextBox();
		
		if(superficialSelectedWeapon != null) {
			renderToolTip(new ItemStack(superficialSelectedWeapon), mouseX, mouseY);
			
		}
		//System.out.println("hi");
	}
	
	@Override
	protected void compatibleMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.compatibleMouseClicked(mouseX, mouseY, mouseButton);
		this.searchBox.mouseClicked(mouseX, mouseY, mouseButton);
		
		int c = (int) Math.floor(filteredWeaponsList.size()*scrollBarProgress/7)*7;
		for(int y = 0; y < 6; ++y) {
			for(int x = 0; x < 7; ++x) {
				if(c == filteredWeaponsList.size()) break;
				
			//Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(new ItemStack(filteredWeaponsList.get(c)), tX + 15 + (x*23), tY + 55 + (y * 23));
				boolean selected = GUIRenderHelper.checkInBox(mouseX, mouseY, this.guiLeft + 12 + (x*23), this.guiTop + 52 + (y * 23), 22, 22);
				if(selected) {
					selectedWeapon = filteredWeaponsList.get(c);
				}
				c += 1;
				
				
			}
		}
		
		
		/*
		int c = (int) Math.floor(filteredWeaponsList.size()*scrollProgress/7)*7;
		for(int y = 0; y < 6; ++y) {
			for(int x = 0; x < 7; ++x) {
				
			}
		}
		*/
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		this.searchBox.textboxKeyTyped(typedChar, keyCode);
		
		filteredWeaponsList = (ArrayList<Weapon>) CraftingRegistry.getCraftingRegistry().clone();
		if(searchBox.getText().length() != 0) {
			// Filter out bad results.
			filteredWeaponsList.removeIf((a) -> 
				!I18n.format(a.getUnlocalizedName() + ".name").toLowerCase().contains(searchBox.getText().toLowerCase())
			);
		}
	}
	
	
	public Weapon getSelectedWeaponIDForGUI() {
		//if(superficialSelectedWeapon != null) return superficialSelectedWeapon;
		//else return selectedWeapon;
		return selectedWeapon;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		//drawDefaultBackground();
		
		superficialSelectedWeapon = null;
		//selectedWeapon = -1;
		
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	
		
		
		//renderToolTip(stack, x, y);
		
		
		/*
		GlStateManager.color(1f, 1f, 1f, 1f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("mw:textures/gui/image-004.png"));
		Tessellator t = Tessellator.getInstance();
		BufferBuilder bb = t.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		
		
		bb.pos(sr.getScaledWidth_double(), 0, 0).tex(1, 0).endVertex();
		bb.pos(0, 0, 0).tex(0, 0).endVertex();
		
		bb.pos(0, sr.getScaledHeight_double(), 0).tex(0, 1).endVertex();
		
		bb.pos(sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0).tex(1, 1).endVertex();
		
		
		t.draw();
		*/
		GlStateManager.color(1f, 1f, 1f, 1f);
		
		
		
		
		//System.out.println(this.guiLeft + " | " + this.guiTop);
		int tX = this.guiLeft;
		int tY = this.guiTop;
		
		
		//System.out.println(sr.getScaledWidth_double());
		
		
		
		
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
		
		// Draw page arrows
			//	drawModalRectWithCustomSizedTexture(0, tY + 96, 57f, 284f, 28, 33, 480, 370);
			//	drawModalRectWithCustomSizedTexture(sr.getScaledWidth() - 28, tY + 99, 57f, 317f, 28, 33, 480, 370);
				
		
				
	
		GlStateManager.enableBlend();
		//double scale = 0.885;
		double scale = 1.0;
		GlStateManager.scale(scale, scale, scale);
		
		
		// Draw background elements
		drawModalRectWithCustomSizedTexture(tX, tY, 0f, 0f, 192, 210, 480, 370);
		drawModalRectWithCustomSizedTexture(200 + tX, 20 + tY, 200f, 20f, 202, 190, 480, 370);
		drawModalRectWithCustomSizedTexture(tX, 213 + tY, 0f, 213f, 241, 27, 480, 370);
		
		// Draw progress bar
		// forty notches, therefore 1/40.0 = 0.025
		
	
		double progress = teWorkbench.getProgress();
		
		
		progress = (0.025)*(Math.round(progress/(0.025)));
		drawModalRectWithCustomSizedTexture(tX + 304, tY + 185, 53f, 240f, 81, 11, 480, 370);
		drawModalRectWithCustomSizedTexture(tX + 304, tY + 185, 53f, 240f + 11, (int) (81*progress), 11, 480, 370);
		
		
		/*
		// Draw selectors
		if(!GUIRenderHelper.checkInBox(mouseX, mouseY, tX + 107, tY + 29, 19, 20)) {
			drawModalRectWithCustomSizedTexture(tX + 107, tY + 29, 0, 291f, 19, 20, 480, 370);
		} else {
			drawModalRectWithCustomSizedTexture(tX + 107, tY + 29, 0 + 19, 291f, 19, 20, 480, 370);
		}
		
		
		drawModalRectWithCustomSizedTexture(tX + 107 + 23*2 + 1, tY + 29, 0, 291f + 40f, 19, 20, 480, 370);
		drawModalRectWithCustomSizedTexture(tX + 107 + 23, tY + 29, 0, 291f + 20f, 19, 20, 480, 370);
		*/
		
		// Draw craft button
		drawModalRectWithCustomSizedTexture(tX + 211, tY + 179, 0f, 274f, 53, 17, 480, 370);
		
		
		if(!Mouse.isButtonDown(0)) {
			scrollBarGrabbed = false;
		}
		
		int scrollBarVertical = tY + 54 + scrollBarOffset;
		int scrollBarHeight = (int) (138 * Math.min(1.0, 42.0/filteredWeaponsList.size()));
		drawModalRectWithCustomSizedTexture(tX + 176, scrollBarVertical, 412, 54, 6, scrollBarHeight, 480, 370);
		
		if(Mouse.isButtonDown(0) && GUIRenderHelper.checkInBox(mouseX, mouseY, tX + 176, scrollBarVertical, 6, scrollBarHeight) && !scrollBarGrabbed) {
			scrollBarGrabbed = true;
			grabX = scrollBarOffset;
			grabY = mouseY;
		}
		
		
		if(scrollBarGrabbed) {
		
			
			
			scrollBarOffset = grabX + (mouseY - grabY);
			if(scrollBarOffset + scrollBarHeight > 138) {
				scrollBarOffset = 138 - scrollBarHeight;
				
			} else if(scrollBarOffset < 0) {
				scrollBarOffset = 0;
			}
			
		}
		
		// Calculate the new scroll bar progress.
		scrollBarProgress = scrollBarOffset/(138.0 - scrollBarHeight);
		if(Double.isNaN(scrollBarProgress)) scrollBarProgress = 0.0;
		
		
		//drawModalRectWithCustomSizedTexture(tX + 304, tY + 185, 53f, 240f + 11, (int) (81*progress), 11, 480, 370);
		//drawModalRectWithCustomSizedTexture(tX + 304, tY + 185, 53f, 240f + 11, (int) (81*progress), 11, 480, 370);
		
		
		if(!filteredWeaponsList.isEmpty()) {
			
		
			
			// Draw slots
			int c = (int) Math.floor(filteredWeaponsList.size()*scrollBarProgress/7)*7;
			for(int y = 0; y < 6; ++y) {
				for(int x = 0; x < 7; ++x) {
					if(c == filteredWeaponsList.size()) break;
					
					
					if(filteredWeaponsList.get(c) == selectedWeapon) {
						drawModalRectWithCustomSizedTexture(tX + 12 + (x*23), tY + 52 + (y * 23), 97f - 44, 262f, 22, 22, 480, 370);
						
					} else {
						boolean selected = GUIRenderHelper.checkInBox(mouseX, mouseY, tX + 12 + (x*23), tY + 52 + (y * 23), 22, 22);
						if(!selected) {
							drawModalRectWithCustomSizedTexture(tX + 12 + (x*23), tY + 52 + (y * 23), 97f - 22, 262f, 22, 22, 480, 370);
							
						} else {
							superficialSelectedWeapon = filteredWeaponsList.get(c);
							drawModalRectWithCustomSizedTexture(tX + 12 + (x*23), tY + 52 + (y * 23), 97f, 262f, 22, 22, 480, 370);
							
						}
					}
					
					
					
					
					
					c += 1;
					
					/*
					GlStateManager.enableTexture2D();
					GlStateManager.disableDepth();
					GlStateManager.disableLighting();
					*/
			
					
				}
			}
			
			c = (int) Math.floor(filteredWeaponsList.size()*scrollBarProgress/7)*7;
			for(int y = 0; y < 6; ++y) {
				for(int x = 0; x < 7; ++x) {
					if(c >= filteredWeaponsList.size()) break;
					
					
					
				Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(new ItemStack(filteredWeaponsList.get(c)), tX + 15 + (x*23), tY + 55 + (y * 23));
		
					c += 1;
					
					
				}
			}
		}
		
		
		
		
		
		GuiRenderUtil.drawScaledString(fontRenderer, "CRAFTING", tX + 11, tY + 5, 1.2, 0x8FC5E3);
		//GuiRenderUtil.drawScaledString(fontRenderer, "Search Items...", tX + 18, tY + 35, 0.8, 0xFFFFFF);
		
		if(getSelectedWeaponIDForGUI() != null) {
			Weapon weapon = getSelectedWeaponIDForGUI();
			GuiRenderUtil.drawScaledString(fontRenderer, I18n.format(weapon.getUnlocalizedName() + ".name"), tX + 214, tY + 31, 1.2, 0xFDF17C);
			GuiRenderUtil.drawScaledString(fontRenderer, weapon.builder.getWeaponType(), tX + 214, tY + 43, 0.75, 0xC8C49C);
			
		}
		
		GuiRenderUtil.drawScaledString(fontRenderer, "Progress", tX + 326, tY + 175, 0.8, 0xFFFFFF);
		GuiRenderUtil.drawScaledString(fontRenderer, "Output", tX + 7, tY + 223, 0.9, 0xFFFFFF);
		
	
		//B06061
		GuiRenderUtil.drawScaledString(fontRenderer, "CRAFT", tX + 222, tY + 184, 1.0, 0xB06061);
		GuiRenderUtil.drawScaledString(fontRenderer, "Results: " + filteredWeaponsList.size(), tX + 12, tY + 191, 0.8, 0xFFFFFF);
		
		
		if(getSelectedWeaponIDForGUI() != null) {
			Weapon weapon = getSelectedWeaponIDForGUI();
			if(weapon.getModernRecipe() != null && weapon.getModernRecipe().length != 0) {
				int c = 0;
				for(ItemStack stack : weapon.getModernRecipe()) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
					
					int x  =tX + 210 + (c*10);
					int y = tY + 122;
					
					if(GUIRenderHelper.checkInBox(mouseX, mouseY, x, y, 15, 15)) {
						renderToolTip(stack, mouseX, mouseY);
						GlStateManager.enableTexture2D();
						Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEX);
						GlStateManager.enableBlend();
						
					}
					
					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, 0);
					GlStateManager.scale(0.15, 0.15, 0.15);
					drawModalRectWithCustomSizedTexture(0, 0, 241f, 215f, 113, 114, 480, 370);
					GlStateManager.popMatrix();
					
					Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
					
					GUIRenderHelper.drawScaledString("x" + stack.getCount(),x + 8, y + 12, 0.6, 0x97E394);
					
					c += 1;
				}
			}
			
		
		}
try {
	
	if(getSelectedWeaponIDForGUI() != null) {
		GlStateManager.pushMatrix();
		//GlStateManager.translate(this.guiLeft, this.guiTop, 0);
		  GlStateManager.translate((float)tX + 315, (float)this.guiTop + 75, 100.0F + 0);
	        GlStateManager.translate(8.0F, 8.0F, 0.0F);
	        GlStateManager.scale(1.0F, -1.0F, 1.0F);
	        GlStateManager.scale(20.0F, 20.0F, 20.0F);
	        
	        GlStateManager.rotate(15, 1, 0, 0);
	        GlStateManager.rotate(120, 0, 1, 0);
	        GlStateManager.rotate(100, 0, 0, 1);
	        
	        GlStateManager.scale(4, 4, 4);
	        Weapon weap = getSelectedWeaponIDForGUI();
	        GlStateManager.enableLighting();
	     //  GlStateManager.enableDepth();
	      // GlStateManager.disableCull();
	      // RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(weap), TransformType.GROUND);
			GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}
		
			
			
			//Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(new ItemStack(weap), this.guiLeft, this.guiTop);
					
			/*
			GlStateManager.pushMatrix();
			GlStateManager.translate(Math.random()*1000 - 500, Math.random()*1000 - 500, Math.random()*1000 - 500);
			weap.getRenderer().getBuilder().getModel().render(null, 0f, 0f, 0f, 0f, 0f, 0.0625f);
			GlStateManager.popMatrix();
			*/
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	

		
		GlStateManager.popMatrix();
		
	
		
		
	}

	
	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}
	

}
