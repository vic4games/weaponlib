package com.jimholden.conomy.proxy;


import java.util.Map;

import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityKeyDoor;
import com.jimholden.conomy.blocks.tileentity.TileEntityPistolStand;
import com.jimholden.conomy.client.gui.AdvancedTestGUI;
import com.jimholden.conomy.client.gui.BankingGUI;
import com.jimholden.conomy.client.gui.ModernLedgerGUI;
import com.jimholden.conomy.client.gui.engine.AdvancedGUI;
import com.jimholden.conomy.client.gui.engine.CustomFontRenderer;
import com.jimholden.conomy.client.gui.player.DeathGui;
import com.jimholden.conomy.client.gui.player.LedgerGui;
import com.jimholden.conomy.client.gui.player.StockGui;
import com.jimholden.conomy.entity.models.ClimbingRopeAnchor;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.HXSuit.HXHelmet;
import com.jimholden.conomy.items.models.backpacks.DuffleBag;
import com.jimholden.conomy.items.models.backpacks.F5SwitchbladeBackpack;
import com.jimholden.conomy.items.models.backpacks.OakleyMechanismBackpack;
import com.jimholden.conomy.items.models.bodyarmor.BodyArmorModelThree;
import com.jimholden.conomy.items.models.glasses.SunglassesModel;
import com.jimholden.conomy.items.models.headset.CatEars;
import com.jimholden.conomy.items.models.headset.Comtacs;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.items.models.headset.Plantronics800HD;
import com.jimholden.conomy.items.models.headset.USMCHeadsetModel;
import com.jimholden.conomy.items.models.jackets.AlphaFLJacket;
import com.jimholden.conomy.items.models.jackets.BlackHalwoodTuxedo;
import com.jimholden.conomy.items.models.jackets.JeepSpirit;
import com.jimholden.conomy.items.models.jackets.LeonsJacket;
import com.jimholden.conomy.items.models.masks.FaceBandanaModel;
import com.jimholden.conomy.items.models.masks.GhostMask;
import com.jimholden.conomy.items.models.masks.RedScarf;
import com.jimholden.conomy.items.models.masks.TokyoGhoulMask;
import com.jimholden.conomy.items.models.pants.StandardPantsModel;
import com.jimholden.conomy.items.models.rigs.AtlasT7;
import com.jimholden.conomy.items.models.rigs.MOLLEPlateCarrier;
import com.jimholden.conomy.items.models.rigs.TritonRig;
import com.jimholden.conomy.items.models.shirts.ShirtModel;
import com.jimholden.conomy.items.models.shoes.BlackShoes;
import com.jimholden.conomy.main.ModEventClientHandler;
import com.jimholden.conomy.main.ModEventHandler;
import com.jimholden.conomy.render.BakedModelCustom;
import com.jimholden.conomy.render.ConomyRenderLayer;
import com.jimholden.conomy.render.tesr.PistolStandTESR;
import com.jimholden.conomy.render.tesr.SlidingDoorTESR;
import com.jimholden.conomy.render.tesr.TestTESR;
import com.jimholden.conomy.teisr.BasicItemTEISR;
import com.jimholden.conomy.teisr.ChestItemTEISR;
import com.jimholden.conomy.teisr.HeadItemTEISR;
import com.jimholden.conomy.teisr.LegItemTEISR;
import com.jimholden.conomy.teisr.ShoeItemTEISR;
import com.jimholden.conomy.teisr.TEISRBase;
import com.jimholden.conomy.util.Keybinds;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.RenderHandler;

import akka.japi.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientProxy extends CommonProxy {
	public static Map<Item, Pair<TEISRBase, ModelBiped>> registerTEList;
	
	public void registerItemRenderer(Item item, int meta, String id) {
		//System.out.println("RER: " + item.getRegistryName());
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
		
	}
	
	public static void swapModels(Item item, IRegistry<ModelResourceLocation, IBakedModel> reg) {
		ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
		IBakedModel model = reg.getObject(loc);
		TileEntityItemStackRenderer render = item.getTileEntityItemStackRenderer();
		if(render instanceof TEISRBase) {
			((TEISRBase) render).itemModel = model;
			reg.putObject(loc, new BakedModelCustom((TEISRBase) render));
		}

	}
	
	@Override
	public void registerTEISRUnique(Item item, TEISRBase teisr, ModelBiped model) {
		Pair pair = new Pair<TEISRBase, ModelBiped>(teisr, model);
		this.registerTEList.put(item, pair);
		//super.registerTEISRUnique(item, teisr, model);
	}
	
	public void bakeShit() {
		TritonRig rigy = new TritonRig();
		//Minecraft.getMinecraft().renderGlobal
	}
	
	public static void registerTEISR() {
		
		//ITEMS
		ModItems.CLIMBING_ANCHOR.setTileEntityItemStackRenderer(new BasicItemTEISR(new ClimbingRopeAnchor()));
		
		//HEAD ITEMS
		ModItems.HEADSET.setTileEntityItemStackRenderer(new HeadItemTEISR(new HeadsetModel()));
		ModItems.USMCHEADSET.setTileEntityItemStackRenderer(new HeadItemTEISR(new USMCHeadsetModel()));
		ModItems.GHOSTFACEMASK.setTileEntityItemStackRenderer(new HeadItemTEISR(new GhostMask()));
		ModItems.SUNGLASSES.setTileEntityItemStackRenderer(new HeadItemTEISR(new SunglassesModel()));
		ModItems.COMTACS.setTileEntityItemStackRenderer(new HeadItemTEISR(new Comtacs()));
		ModItems.PLANTRONICS.setTileEntityItemStackRenderer(new HeadItemTEISR(new Plantronics800HD()));
		ModItems.TOKYOGHOULMASK.setTileEntityItemStackRenderer(new HeadItemTEISR(new TokyoGhoulMask()));
		ModItems.REDSCARF.setTileEntityItemStackRenderer(new HeadItemTEISR(new RedScarf()));
		ModItems.HXHELMET.setTileEntityItemStackRenderer(new HeadItemTEISR(new HXHelmet()));
		ModItems.CATEARS.setTileEntityItemStackRenderer(new HeadItemTEISR(new CatEars()));
		ModItems.FACEBANDANA.setTileEntityItemStackRenderer(new HeadItemTEISR(new FaceBandanaModel()));
		
		//CHEST ITEMS
		ModItems.LEONSJACKET.setTileEntityItemStackRenderer(new ChestItemTEISR(new LeonsJacket()));
		ModItems.F5SWITCHBLADE.setTileEntityItemStackRenderer(new ChestItemTEISR(new F5SwitchbladeBackpack()));
		ModItems.ALPHAFLJACKET.setTileEntityItemStackRenderer(new ChestItemTEISR(new AlphaFLJacket()));
		ModItems.JEEPSPIRITJACKET.setTileEntityItemStackRenderer(new ChestItemTEISR(new JeepSpirit()));
		ModItems.OAKLEYMECHANISM.setTileEntityItemStackRenderer(new ChestItemTEISR(new OakleyMechanismBackpack()));
		ModItems.TRITONRIG.setTileEntityItemStackRenderer(new ChestItemTEISR(new TritonRig()));
		ModItems.MOLLEPLATECARRIER.setTileEntityItemStackRenderer(new ChestItemTEISR(new MOLLEPlateCarrier()));
		ModItems.ATLAST7.setTileEntityItemStackRenderer(new ChestItemTEISR(new AtlasT7()));
		ModItems.BODYARMORIII.setTileEntityItemStackRenderer(new ChestItemTEISR(new BodyArmorModelThree()));
		ModItems.BLACKFORMALSHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.BLACKSHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.BLACKMULTICAMOSHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.FORESTMILITARYSHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.NAVYBLUESHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.FORMALSHIRT.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.HXBODY.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.HXVEST.setTileEntityItemStackRenderer(new ChestItemTEISR(new ShirtModel()));
		ModItems.BLACKHALWOODTUXEDO.setTileEntityItemStackRenderer(new ChestItemTEISR(new BlackHalwoodTuxedo()));
		ModItems.TRITONRIG.setTileEntityItemStackRenderer(new ChestItemTEISR(new TritonRig()));
		ModItems.BODYARMORIII.setTileEntityItemStackRenderer(new ChestItemTEISR(new BodyArmorModelThree()));
		ModItems.F5SWITCHBLADE.setTileEntityItemStackRenderer(new ChestItemTEISR(new F5SwitchbladeBackpack()));
		ModItems.OAKLEYMECHANISM.setTileEntityItemStackRenderer(new ChestItemTEISR(new OakleyMechanismBackpack()));
		ModItems.DUFFLEBAG.setTileEntityItemStackRenderer(new ChestItemTEISR(new DuffleBag()));
		
		//PANTS
		ModItems.GYMPANTS.setTileEntityItemStackRenderer(new LegItemTEISR(new StandardPantsModel()));
		ModItems.JEANS.setTileEntityItemStackRenderer(new LegItemTEISR(new StandardPantsModel()));
		ModItems.BLACKJEANS.setTileEntityItemStackRenderer(new LegItemTEISR(new StandardPantsModel()));
		ModItems.KHAKIJEANS.setTileEntityItemStackRenderer(new LegItemTEISR(new StandardPantsModel()));
		
		ModItems.BLACKSHOES.setTileEntityItemStackRenderer(new ShoeItemTEISR(new BlackShoes()));
		
		
		
		
		/*
		if(registerTEList != null) {
			for(int x = 0; x < registerTEList.size(); ++x) {
				System.out.println("registrar" + registerTEList.get(x));
			}
		}*/
		
	}
	
	public void showAdvancedGUI(EntityPlayer player, int id) {
		switch(id) {
			case 0:
				Minecraft.getMinecraft().displayGuiScreen(new BankingGUI(player));
			
		}
	}
	
	public void openBankWRedirect(EntityPlayer player, BlockPos pos, int gui) {
		System.out.println("FUFKC  DKDKK ");
		Minecraft.getMinecraft().displayGuiScreen(new BankingGUI(player, gui, pos));
		
	}
	
	public void showLedgerGUI(EntityPlayer player) {
		Minecraft.getMinecraft().displayGuiScreen(new ModernLedgerGUI(player));
		//Minecraft.getMinecraft().displayGuiScreen(new LedgerGui(player));
		
	}
	
	public void showStockGUI(EntityPlayer player) {
		Minecraft.getMinecraft().displayGuiScreen(new StockGui(player));
	}
	
	public void showDeathGUI(EntityPlayer player) {
		Minecraft.getMinecraft().displayGuiScreen(new DeathGui(player));
	}
	

	public static void registerTESRRenderC() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityATM.class, new TestTESR());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPistolStand.class, new PistolStandTESR());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeyDoor.class, new SlidingDoorTESR());
	}
	
	@Override
	public void registerRenderLayers() {
		Minecraft.getMinecraft().getRenderManager().getSkinMap().forEach((p, r) -> {
            r.addLayer(new ConomyRenderLayer(r));
        });
	}

	@Override
	public void registerRenderInfo() {
		
		
		
		MinecraftForge.EVENT_BUS.register(new ModEventClientHandler());
		Keybinds.registerAll();
		ClientProxy.registerTESRRenderC();
		ClientProxy.registerTEISR();
		RenderHandler.registerEntityRenders();
	}
	
	public static CustomFontRenderer newFontRenderer;
	@Override
	public void registerFont() {
		newFontRenderer = new CustomFontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation(Reference.MOD_ID + ":textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
		
		
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(newFontRenderer);
	}
	
	
	
	
}
