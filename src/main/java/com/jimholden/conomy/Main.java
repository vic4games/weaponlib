package com.jimholden.conomy;

import org.apache.logging.log4j.Logger;

import com.jimholden.conomy.blocks.tileentity.TileEntityATM;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.blocks.tileentity.TileEntityPistolStand;
import com.jimholden.conomy.client.gui.CredstickOverlay;
import com.jimholden.conomy.client.gui.LedgerOverlay;
import com.jimholden.conomy.client.gui.engine.CustomFontRenderer;
import com.jimholden.conomy.commands.CommandAdvtest;
import com.jimholden.conomy.commands.CommandBalance;
import com.jimholden.conomy.commands.CommandBank;
import com.jimholden.conomy.commands.CommandBlood;
import com.jimholden.conomy.commands.CommandCurrency;
import com.jimholden.conomy.commands.CommandCurrencyLedger;
import com.jimholden.conomy.commands.CommandData;
import com.jimholden.conomy.commands.CommandDupeM;
import com.jimholden.conomy.commands.CommandLigma;
import com.jimholden.conomy.commands.CommandPay;
import com.jimholden.conomy.commands.CommandPosTool;
import com.jimholden.conomy.commands.CommandRealBank;
import com.jimholden.conomy.commands.CommandRevive;
import com.jimholden.conomy.commands.CommandSpawnComp;
import com.jimholden.conomy.commands.CommandSpawnDrug;
import com.jimholden.conomy.commands.CommandStock;
import com.jimholden.conomy.commands.CommandTestLoot;
import com.jimholden.conomy.entity.data.Vec3dSerializer;
import com.jimholden.conomy.entity.data.VecListSerializer;
import com.jimholden.conomy.init.ModBlocks;
import com.jimholden.conomy.init.ModEntities;
import com.jimholden.conomy.init.ModItems;
import com.jimholden.conomy.items.models.headset.HeadsetModel;
import com.jimholden.conomy.commands.CommandClan;
import com.jimholden.conomy.main.ChunkManager;
import com.jimholden.conomy.main.CoreModLoader;
import com.jimholden.conomy.main.ModEventHandler;
import com.jimholden.conomy.main.OreDictManager;
import com.jimholden.conomy.proxy.ClientProxy;
import com.jimholden.conomy.proxy.CommonProxy;
import com.jimholden.conomy.render.ConomyRenderLayer;
import com.jimholden.conomy.render.tesr.PistolStandTESR;
import com.jimholden.conomy.render.tesr.TestTESR;
import com.jimholden.conomy.tabs.BuildingTab;
import com.jimholden.conomy.tabs.ClothingTab;
import com.jimholden.conomy.tabs.DrugTab;
import com.jimholden.conomy.tabs.LootableBlocksTab;
import com.jimholden.conomy.teisr.BackpackTESIR;
import com.jimholden.conomy.teisr.HeadItemTEISR;
import com.jimholden.conomy.teisr.MiniCache;
import com.jimholden.conomy.util.Keybinds;
import com.jimholden.conomy.util.ModEvents;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.handlers.CapabilityHandler;
import com.jimholden.conomy.util.handlers.GuiHandler;
import com.jimholden.conomy.util.handlers.RenderHandler;
import com.jimholden.conomy.util.handlers.SoundsHandler;
import com.jimholden.conomy.util.packets.AddItemPacket;
import com.jimholden.conomy.util.packets.AddItemPacketExtract;
import com.jimholden.conomy.util.packets.AddLootPacket;
import com.jimholden.conomy.util.packets.AdvGUIClientPacket;
import com.jimholden.conomy.util.packets.AdvGUIPacket;
import com.jimholden.conomy.util.packets.AttackYawPacket;
import com.jimholden.conomy.util.packets.BloodExplosionPacket;
import com.jimholden.conomy.util.packets.BloodParticlePacket;
import com.jimholden.conomy.util.packets.CreditSurveyPacket;
import com.jimholden.conomy.util.packets.GuiRedirectPacket;
import com.jimholden.conomy.util.packets.InventoryServerPacket;
import com.jimholden.conomy.util.packets.InventorySurveyPacket;
import com.jimholden.conomy.util.packets.LedgerTransfer;
import com.jimholden.conomy.util.packets.LedgerTransferThree;
import com.jimholden.conomy.util.packets.LedgerTransferTwo;
import com.jimholden.conomy.util.packets.LootItemPacket;
import com.jimholden.conomy.util.packets.LootUpdateClientPacket;
import com.jimholden.conomy.util.packets.LootUpdateClientPacket.LootUpdateClientPacketHandler;
import com.jimholden.conomy.util.packets.economy.FinancialClientPacket;
import com.jimholden.conomy.util.packets.economy.FinancialServerPacket;
import com.jimholden.conomy.util.packets.economy.TraderClientPacket;
import com.jimholden.conomy.util.packets.economy.TraderServerPacket;
import com.jimholden.conomy.util.packets.medical.BandageServerPacket;
import com.jimholden.conomy.util.packets.medical.ConsciousSurveyPacket;
import com.jimholden.conomy.util.packets.medical.RecalculateWeight;
import com.jimholden.conomy.util.packets.stock.RegisterStockPacket;
import com.jimholden.conomy.util.packets.stock.RemoveStockPacket;
import com.jimholden.conomy.util.packets.stock.RemoveStockSurveyPacket;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.MiniCacheUpdate;
import com.jimholden.conomy.util.packets.OpenInventoryServerPacket;
import com.jimholden.conomy.util.packets.ParticlePacket;
import com.jimholden.conomy.util.packets.PlayerMotionPacket;
import com.jimholden.conomy.util.packets.PowerSurveyPacket;
import com.jimholden.conomy.util.packets.RequestServerStockData;
import com.jimholden.conomy.util.packets.RopeDismountPacket;
import com.jimholden.conomy.util.packets.RopeKeyPacket;
import com.jimholden.conomy.util.packets.ServerStartMixPacket;
import com.jimholden.conomy.util.packets.StateAutoClient;
import com.jimholden.conomy.util.packets.StateAutoPacket;
import com.jimholden.conomy.util.packets.StockSurveyPacket;
import com.jimholden.conomy.util.packets.UpdateDeviceTile;
import com.jimholden.conomy.util.packets.UpdatePlayerRopePacket;
import com.jimholden.conomy.util.packets.ZombieScreamPacket;
import com.jimholden.conomy.util.packets.ZombieSleepSurvey;
import com.jimholden.conomy.util.packets.ZombieUpdatePacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;


/**
 * Conomy © 2020 by Jim Holden is licensed under CC BY-NC-ND 4.0 
 */
@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
	
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
	public static final CreativeTabs BUILDINGTAB = new BuildingTab();
	public static final CreativeTabs DRUGTAB = new DrugTab();
	public static final CreativeTabs LOOTABLEBLOCKSTAB = new LootableBlocksTab();
	public static final CreativeTabs CLOTHINGITEMTAB = new ClothingTab();
	public static final String mcVersion = "1.12.2";
	
	public static ArmorMaterial shirtAndPantsMat = EnumHelper.addArmorMaterial(Reference.MOD_ID + ":cosmeticmat", Reference.MOD_ID + ":cosmeticmat", 1000, new int[] { 0, 0, 0, 0 }, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 2.0F);
	
	
	
	@Instance
	public static Main instance;
	
	
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	@SideOnly(Side.CLIENT)
	public static void registerTESRTEISR() {
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerRenderLayer() {
		
	}
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
		//System.out.println(CoreModLoader.class.getName());
		if(logger == null) logger = event.getModLog();
		
		CapabilityHandler.register();
		
		
		
		
		DataSerializers.registerSerializer(VecListSerializer.SERIALIZER);
		DataSerializers.registerSerializer(Vec3dSerializer.SERIALIZER);
		

		NETWORK.registerMessage(MessageUpdateCredits.HandleMessageCredits.class, MessageUpdateCredits.class, 0, Side.SERVER);
		NETWORK.registerMessage(LedgerTransfer.HandleLedgerTransfer.class, LedgerTransfer.class, 1, Side.SERVER);
		NETWORK.registerMessage(LedgerTransferTwo.HandleLedgerTransferTwo.class, LedgerTransferTwo.class, 2, Side.SERVER);
		NETWORK.registerMessage(LedgerTransferThree.HandleLedgerTransferThree.class, LedgerTransferThree.class, 3, Side.SERVER);
		NETWORK.registerMessage(CreditSurveyPacket.CreditSurveyHandler.class, CreditSurveyPacket.class, 4, Side.CLIENT);
		NETWORK.registerMessage(UpdateDeviceTile.HandleUpdateDeviceTile.class, UpdateDeviceTile.class, 5, Side.SERVER);
		NETWORK.registerMessage(PowerSurveyPacket.PowerSurveyHandler.class, PowerSurveyPacket.class, 6, Side.CLIENT);
		NETWORK.registerMessage(LootItemPacket.LootItemPacketHandler.class, LootItemPacket.class, 7, Side.SERVER);
		NETWORK.registerMessage(AddLootPacket.AddLootPacketHandler.class, AddLootPacket.class, 8, Side.SERVER);
		NETWORK.registerMessage(LootUpdateClientPacketHandler.class, LootUpdateClientPacket.class, 9, Side.SERVER);
		NETWORK.registerMessage(RequestServerStockData.RequestServerStockDataHandler.class, RequestServerStockData.class, 10, Side.SERVER);
		NETWORK.registerMessage(StockSurveyPacket.PowerSurveyHandler.class, StockSurveyPacket.class, 11, Side.CLIENT);
		NETWORK.registerMessage(RegisterStockPacket.RegisterStockHandler.class, RegisterStockPacket.class, 12, Side.SERVER);
		NETWORK.registerMessage(RemoveStockPacket.RemoveStockHandler.class, RemoveStockPacket.class, 13, Side.SERVER);
		NETWORK.registerMessage(RemoveStockSurveyPacket.PowerSurveyHandler.class, RemoveStockSurveyPacket.class, 14, Side.CLIENT);
		NETWORK.registerMessage(ConsciousSurveyPacket.ConsciousSurveyHandler.class, ConsciousSurveyPacket.class, 15, Side.CLIENT);
		NETWORK.registerMessage(AddItemPacket.AddLootPacketHandler.class, AddItemPacket.class, 16, Side.SERVER);
		NETWORK.registerMessage(AddItemPacketExtract.AddLootPacketHandler.class, AddItemPacketExtract.class, 17, Side.SERVER);
		NETWORK.registerMessage(StateAutoPacket.AddLootPacketHandler.class, StateAutoPacket.class, 18, Side.SERVER);
		NETWORK.registerMessage(StateAutoClient.AddLootPacketHandler.class, StateAutoClient.class, 19, Side.CLIENT);
		NETWORK.registerMessage(ServerStartMixPacket.AddLootPacketHandler.class, ServerStartMixPacket.class, 20, Side.SERVER);
		NETWORK.registerMessage(InventorySurveyPacket.CreditSurveyHandler.class, InventorySurveyPacket.class, 21, Side.CLIENT);
		NETWORK.registerMessage(InventoryServerPacket.CreditSurveyHandler.class, InventoryServerPacket.class, 22, Side.SERVER);
		NETWORK.registerMessage(OpenInventoryServerPacket.CreditSurveyHandler.class, OpenInventoryServerPacket.class, 23, Side.SERVER);
		NETWORK.registerMessage(MiniCacheUpdate.CreditSurveyHandler.class, MiniCacheUpdate.class, 24, Side.CLIENT);
		NETWORK.registerMessage(RecalculateWeight.RequestServerStockDataHandler.class, RecalculateWeight.class, 25, Side.SERVER);
		NETWORK.registerMessage(AttackYawPacket.CreditSurveyHandler.class, AttackYawPacket.class, 26, Side.CLIENT);
		NETWORK.registerMessage(BloodParticlePacket.CreditSurveyHandler.class, BloodParticlePacket.class, 27, Side.CLIENT);
		NETWORK.registerMessage(ZombieSleepSurvey.CreditSurveyHandler.class, ZombieSleepSurvey.class, 28, Side.CLIENT);
		NETWORK.registerMessage(ZombieScreamPacket.CreditSurveyHandler.class, ZombieScreamPacket.class, 29, Side.CLIENT);
		NETWORK.registerMessage(ParticlePacket.CreditSurveyHandler.class, ParticlePacket.class, 30, Side.CLIENT);
		NETWORK.registerMessage(UpdatePlayerRopePacket.LootUpdateClientPacketHandler.class, UpdatePlayerRopePacket.class, 31, Side.CLIENT);
		NETWORK.registerMessage(RopeKeyPacket.CreditSurveyHandler.class, RopeKeyPacket.class, 32, Side.SERVER);
		NETWORK.registerMessage(RopeDismountPacket.CreditSurveyHandler.class, RopeDismountPacket.class, 33, Side.SERVER);
		NETWORK.registerMessage(PlayerMotionPacket.LootUpdateClientPacketHandler.class, PlayerMotionPacket.class, 34, Side.CLIENT);
		NETWORK.registerMessage(BloodExplosionPacket.CreditSurveyHandler.class, BloodExplosionPacket.class, 35, Side.CLIENT);
		NETWORK.registerMessage(AdvGUIPacket.AddLootPacketHandler.class, AdvGUIPacket.class, 36, Side.SERVER);
		NETWORK.registerMessage(AdvGUIClientPacket.AddLootPacketHandler.class, AdvGUIClientPacket.class, 37, Side.CLIENT);
		NETWORK.registerMessage(GuiRedirectPacket.AddLootPacketHandler.class, GuiRedirectPacket.class, 38, Side.SERVER);
		
		NETWORK.registerMessage(FinancialClientPacket.FinancialHandler.class, FinancialClientPacket.class, 39, Side.CLIENT);
		NETWORK.registerMessage(FinancialServerPacket.FinancialHandler.class, FinancialServerPacket.class, 40, Side.SERVER);
		
		NETWORK.registerMessage(TraderClientPacket.FinancialHandler.class, TraderClientPacket.class, 41, Side.CLIENT);
		NETWORK.registerMessage(TraderServerPacket.FinancialHandler.class, TraderServerPacket.class, 42, Side.SERVER);
		
		NETWORK.registerMessage(BandageServerPacket.FinancialHandler.class, BandageServerPacket.class, 43, Side.SERVER);
		NETWORK.registerMessage(ZombieUpdatePacket.AddLootPacketHandler.class, ZombieUpdatePacket.class, 44, Side.CLIENT);
		
		
		
		
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkManager());
		
		//RenderLivingBase
		
		
		//RenderLiving
		
		//ModItems.PACKBACK.setTileEntityItemStackRenderer(BackpackTESIR.INSTANCE);
		
		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	    proxy.registerRenderInfo();
	    ModEntities.registerEntities();
	    
	  //  RenderHandler.registerEntityRenders();
	    

		
	}
	
	
	
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		// INITIALIZE OVERLAY
		MinecraftForge.EVENT_BUS.register(new CredstickOverlay());
		MinecraftForge.EVENT_BUS.register(new LedgerOverlay());
		
		//
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new ModEvents());
		
		OreDictManager.registerOres();
		SoundsHandler.registerSounds();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		
		
		
		
		PermissionAPI.registerNode("conomy.command.clans", DefaultPermissionLevel.ALL, "Permission to execute the command /faction");
	}
	@EventHandler
	public static void PostInit(FMLPostInitializationEvent event) {
	
		proxy.registerFont();
		proxy.registerRenderLayers();
		//registerRenderLayer();
	}
	
	
	
		
		
	
	@EventHandler
	public static void ServerInit(FMLServerStartingEvent event)
	{
		// REGISTER COMMANDS
		event.registerServerCommand(new CommandCurrency());
		event.registerServerCommand(new CommandCurrencyLedger());
		event.registerServerCommand(new CommandBank());
		event.registerServerCommand(new CommandRealBank());
		event.registerServerCommand(new CommandPay());
		event.registerServerCommand(new CommandBalance());
		event.registerServerCommand(new CommandData());
		event.registerServerCommand(new CommandDupeM());
		event.registerServerCommand(new CommandClan());
		event.registerServerCommand(new CommandStock());
		event.registerServerCommand(new CommandRevive());
		event.registerServerCommand(new CommandTestLoot());
		event.registerServerCommand(new CommandAdvtest());
		event.registerServerCommand(new CommandSpawnDrug());
		event.registerServerCommand(new CommandSpawnComp());
		event.registerServerCommand(new CommandBlood());
		event.registerServerCommand(new CommandLigma());
		event.registerServerCommand(new CommandPosTool());
	}
	
	
	
	public MinecraftServer getServer() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
	
	

	
}
