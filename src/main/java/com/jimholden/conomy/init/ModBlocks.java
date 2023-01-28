package com.jimholden.conomy.init;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.jimholden.conomy.blocks.ATMBlock;
import com.jimholden.conomy.blocks.BetterBlockBase;
import com.jimholden.conomy.blocks.BetterDebris;
import com.jimholden.conomy.blocks.Block3D;
import com.jimholden.conomy.blocks.BlockBase;
import com.jimholden.conomy.blocks.BlockBasicDrug;
import com.jimholden.conomy.blocks.BrickPackerBlock;
import com.jimholden.conomy.blocks.BuildingBlockBase;
import com.jimholden.conomy.blocks.ChemExtractorBlock;
import com.jimholden.conomy.blocks.CompoundMixerBlock;
import com.jimholden.conomy.blocks.CornerRotatingBlock;
import com.jimholden.conomy.blocks.Debris;
import com.jimholden.conomy.blocks.GeneratorBlock;
import com.jimholden.conomy.blocks.KeyDoorBlock;
import com.jimholden.conomy.blocks.LootBlockBoundType;
import com.jimholden.conomy.blocks.LootingBlockBase;
import com.jimholden.conomy.blocks.MinerBlock;
import com.jimholden.conomy.blocks.ModPlantBase;
import com.jimholden.conomy.blocks.NetworkNodeBlock;
import com.jimholden.conomy.blocks.PillPressBlock;
import com.jimholden.conomy.blocks.PistolStandBlock;
import com.jimholden.conomy.blocks.RingLightBlock;
import com.jimholden.conomy.blocks.SlidingKeyDoor;
import com.jimholden.conomy.blocks.SyringeLoaderBlock;

import com.jimholden.conomy.blocks.building.ModdedFence;
import com.jimholden.conomy.blocks.building.ModdedFenceGate;
import com.jimholden.conomy.blocks.building.ModdedSlab;
import com.jimholden.conomy.blocks.building.ModdedStairs;
import com.jimholden.conomy.blocks.building.glass.GlassDebris;
import com.jimholden.conomy.blocks.building.glass.GlassFence;
import com.jimholden.conomy.blocks.building.glass.GlassFenceGate;
import com.jimholden.conomy.blocks.building.glass.GlassSlab;
import com.jimholden.conomy.blocks.building.glass.GlassStairs;
import com.jimholden.conomy.blocks.building.glass.GlassWall;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom.EnumType;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPurpurSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	
	//MINERS
	public static final Block BASE_MINER = new MinerBlock("base_miner", Material.IRON, 4, 50);
	
	
	
	public static final Block ATM = new ATMBlock("atm", Material.IRON);
	public static final Block ROUTER = new NetworkNodeBlock("router", Material.IRON);
	
	public static final Block COMPOUNDMIXER = new CompoundMixerBlock("compound_mixer", Material.IRON);
	public static final Block CHEMEXTRACTOR = new ChemExtractorBlock("chemextractor", Material.IRON);
	public static final Block BRICKPACKER = new BrickPackerBlock("brickpacker", Material.IRON);
	public static final Block PILLPRESS = new PillPressBlock("pillpress", Material.IRON);
	public static final Block SYRINGELOADER = new SyringeLoaderBlock("syringeloader", Material.IRON);
	
	
	// doors
	public static final KeyDoorBlock LAB_DOOR = new KeyDoorBlock("lab_door", Material.WOOD);
	
	public static final SlidingKeyDoor SLIDING_DOOR = new SlidingKeyDoor("sliderdoor", Material.ROCK);
	
	// LOOTING BLOCKS
	public static final Block LOOTCRATE = new LootingBlockBase("lootcrate", Material.IRON, 100.0F, LootBlockBoundType.BOX, Duration.ofMinutes(5), true, 0);
	/*public static final Block LOOTABLESHELF = new LootingBlockBase("lootableshelf", Material.IRON, 50.0F, 4, LootBlockBoundType.DOUBLEWIDEHIGH);
	public static final Block OUTDOORTRASHCAN = new LootingBlockBase("outdoortrashcan", Material.IRON, 30.0F, 2, LootBlockBoundType.TALLRECT);
	public static final Block MEDICALBAG = new LootingBlockBase("medicalbag", Material.IRON, 70.0F, 5, LootBlockBoundType.BOX);
	public static final Block RIFLECASE = new LootingBlockBase("riflecase", Material.IRON, 70.0F, 1, LootBlockBoundType.FLATTENEDRECT);
	public static final Block WALLMOUNTEDFIRSTAID = new LootingBlockBase("wallmountedfirstaid", Material.IRON, 70.0F, 5, LootBlockBoundType.WALL);
	public static final Block WALLMOUNTEDFIRSTAID2 = new LootingBlockBase("wallmountedfirstaid2", Material.IRON, 70.0F, 5, LootBlockBoundType.WALL);
	public static final Block MAGAZINECRATE = new LootingBlockBase("magazinecrate", Material.IRON, 70.0F, 4, LootBlockBoundType.BOX);
	public static final Block DEBRISPILE = new LootingBlockBase("debrispile", Material.IRON, 20.0F, 4, LootBlockBoundType.FLATTENEDRECT);
	public static final Block CARDBOARDBOX = new LootingBlockBase("cardboardbox", Material.WOOD, 10.0F, 4, LootBlockBoundType.BOX);
	public static final Block CARDBOARDBOX2 = new LootingBlockBase("cardboardbox2", Material.WOOD, 10.0F, 10, LootBlockBoundType.FLATTENEDRECT);
	public static final Block BEDSIDEDRAWER = new LootingBlockBase("bedsidedrawer", Material.WOOD, 70.0F, 5, LootBlockBoundType.BOX);
	public static final Block COOLERRED = new LootingBlockBase("coolerred", Material.IRON, 30.0F, 3, LootBlockBoundType.BOX);
	public static final Block COOLERBLUE = new LootingBlockBase("coolerblue", Material.ICE, 40.0F, 3, LootBlockBoundType.BOX);
	public static final Block DOUBLECRATE = new LootingBlockBase("doublecrate", Material.WOOD, 100.0F, 8, LootBlockBoundType.DOUBLEWIDE);
	public static final Block FURNITURECHEST = new LootingBlockBase("furniturechest", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block BROKENFLOORBOARD = new LootingBlockBase("brokenfloorboard", Material.WOOD, 75.0F, 8, LootBlockBoundType.FLAT);
	public static final Block CAMPINGBAG = new LootingBlockBase("campingbag", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block DRESSER1 = new LootingBlockBase("dresser1", Material.WOOD, 75.0F, 8, LootBlockBoundType.DOUBLEWIDE);
	public static final Block DRESSER2 = new LootingBlockBase("dresser2", Material.WOOD, 75.0F, 8, LootBlockBoundType.DOUBLEWIDE);
	public static final Block KITCHENCABINET1 = new LootingBlockBase("kitchencabinet1", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block KITCHENCABINET2 = new LootingBlockBase("kitchencabinet2", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block KITCHENCABINET3 = new LootingBlockBase("kitchencabinet3", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block LARGECARDBOARDBOX = new LootingBlockBase("largecardboardbox", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block MEDICINECABINETOPEN = new LootingBlockBase("medicinecabinetclosed", Material.WOOD, 75.0F, 8, LootBlockBoundType.WALL);
	public static final Block MEDICINECABINETCLOSED = new LootingBlockBase("medicinecabinetopen", Material.WOOD, 75.0F, 8, LootBlockBoundType.WALL);
	public static final Block TALLCABINETCLOSED = new LootingBlockBase("tallcabinetclosed", Material.WOOD, 75.0F, 8, LootBlockBoundType.DOUBLEHIGH);
	public static final Block TALLCABINETOPEN = new LootingBlockBase("tallcabinetopen", Material.WOOD, 75.0F, 8, LootBlockBoundType.DOUBLEHIGH);
	public static final Block TOOLBOX = new LootingBlockBase("toolbox", Material.WOOD, 75.0F, 8, LootBlockBoundType.FLATTENEDRECT);
	public static final Block TRAVELBAGLEANING1 = new LootingBlockBase("travelbagleaning1", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block TRAVELBAGLEANING2 = new LootingBlockBase("travelbagleaning2", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block TRAVELBAGLEANING3 = new LootingBlockBase("travelbagleaning3", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block TRAVELBAGLYING1 = new LootingBlockBase("travelbaglying1", Material.WOOD, 75.0F, 8, LootBlockBoundType.FLATTENEDRECT);
	public static final Block TRAVELBAGLYING2 = new LootingBlockBase("travelbaglying2", Material.WOOD, 75.0F, 8, LootBlockBoundType.FLATTENEDRECT);
	public static final Block TRAVELBAGLYING3 = new LootingBlockBase("travelbaglying3", Material.WOOD, 75.0F, 8, LootBlockBoundType.FLATTENEDRECT);
	public static final Block TRAVELBAGSTANDING1 = new LootingBlockBase("travelbagstanding1", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block TRAVELBAGSTANDING2 = new LootingBlockBase("travelbagstanding2", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);
	public static final Block TRAVELBAGSTANDING3 = new LootingBlockBase("travelbagstanding3", Material.WOOD, 75.0F, 8, LootBlockBoundType.BOX);*/

	//public static final Block GENERATOR = new GeneratorBlock("generator", Material.IRON);
	
	// Building Blocks
	public static final Block WHITEBRICK = new BetterBlockBase("whitebrick", Material.ROCK);
	//public static final Block WHITEBRICK = new BlockBase("whitebrick", Material.ROCK);
	public static final Block MOSSYPLANKSOAK = new BetterBlockBase("mossyplanks_oak", Material.WOOD);
	public static final Block FADEDWHITEBRICK = new BetterBlockBase("fadedwhitebrick", Material.ROCK);
	public static final Block DEBRIS = new Debris("debris", Material.GROUND);
	public static final Block RINGLIGHT = new RingLightBlock("ringlight", Material.GLASS);
	
	public static final Block PISTOLSTAND = new PistolStandBlock("pistolstand", Material.IRON);
	
	// PLANTISH BLOCKS
	public static final Block ROOTS = new ModPlantBase("roots", Material.PLANTS);
	public static final Block VINELONG = new ModPlantBase("vinelong", Material.PLANTS);
	public static final Block THICKVINE = new ModPlantBase("thickvine", Material.PLANTS);
	
	public static final Block BARBED_WIRE = new ModPlantBase("barbedwire", Material.IRON);
	
	//public static final Block BuildingBlockTest = new BuildingBlockBase("tester", Material.PLANTS);
	
	
	
	
	public static final Block OPIUM_POPPY = new BlockBasicDrug("opiumpoppy", Material.PLANTS, ModItems.POPPYSTRAW);
	public static final Block COCA_LEAVES = new BlockBasicDrug("cocaleaves", Material.PLANTS, ModItems.POPPYSTRAW);
	
	
	// building bloccks
	//public static final Block GLASS_STAIRS = new ModdedStairs("glass_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	
	//public static final Block GLASS_WALL = new GlassWall("glass_wall", Material.GLASS);
	
	//public static final Block YELLOW_GLASS_WALL = new GlassWall("glass_yellow_wall", Material.GLASS);
	
	
	public static final Block YELLOW_GLASS_STAIRS = new GlassStairs("glass_yellow_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block YELLOW_GLASS_FENCE = new GlassFence("glass_yellow_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block YELLOW_GLASS_FENCE_GATE = new GlassFenceGate("glass_yellow_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block YELLOW_GLASS_WALL = new GlassWall("glass_yellow_wall", Material.GLASS);
	public static final Block YELLOW_GLASS_GlassDebris = new GlassDebris("glass_yellow_debris", Material.GLASS);
	public static final Block YELLOW_GLASS_SLAB = new GlassSlab("glass_yellow_slab");
	
	public static final Block GLASS_RED_STAIRS = new GlassStairs("glass_red_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_RED_FENCE = new GlassFence("glass_red_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_RED_FENCE_GATE = new GlassFenceGate("glass_red_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_RED_WALL = new GlassWall("glass_red_wall", Material.GLASS);
	public static final Block GLASS_RED_GlassDebris = new GlassDebris("glass_red_debris", Material.GLASS);
	public static final Block GLASS_RED_SLAB = new GlassSlab("glass_red_slab");
public static final Block GLASS_BROWN_STAIRS = new GlassStairs("glass_brown_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_BROWN_FENCE = new GlassFence("glass_brown_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_BROWN_FENCE_GATE = new GlassFenceGate("glass_brown_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_BROWN_WALL = new GlassWall("glass_brown_wall", Material.GLASS);
	public static final Block GLASS_BROWN_GlassDebris = new GlassDebris("glass_brown_debris", Material.GLASS);
	public static final Block GLASS_BROWN_SLAB = new GlassSlab("glass_brown_slab");
public static final Block GLASS_STAIRS = new GlassStairs("glass_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_FENCE = new GlassFence("glass_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_FENCE_GATE = new GlassFenceGate("glass_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_WALL = new GlassWall("glass_wall", Material.GLASS);
	public static final Block GLASS_GlassDebris = new GlassDebris("glass_debris", Material.GLASS);
	public static final Block GLASS_SLAB = new GlassSlab("glass_slab");
public static final Block GLASS_BLACK_STAIRS = new GlassStairs("glass_black_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_BLACK_FENCE = new GlassFence("glass_black_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_BLACK_FENCE_GATE = new GlassFenceGate("glass_black_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_BLACK_WALL = new GlassWall("glass_black_wall", Material.GLASS);
	public static final Block GLASS_BLACK_GlassDebris = new GlassDebris("glass_black_debris", Material.GLASS);
	public static final Block GLASS_BLACK_SLAB = new GlassSlab("glass_black_slab");
public static final Block GLASS_GRAY_STAIRS = new GlassStairs("glass_gray_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_GRAY_FENCE = new GlassFence("glass_gray_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_GRAY_FENCE_GATE = new GlassFenceGate("glass_gray_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_GRAY_WALL = new GlassWall("glass_gray_wall", Material.GLASS);
	public static final Block GLASS_GRAY_GlassDebris = new GlassDebris("glass_gray_debris", Material.GLASS);
	public static final Block GLASS_GRAY_SLAB = new GlassSlab("glass_gray_slab");
public static final Block GLASS_LIGHT_BLUE_STAIRS = new GlassStairs("glass_light_blue_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_LIGHT_BLUE_FENCE = new GlassFence("glass_light_blue_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_LIGHT_BLUE_FENCE_GATE = new GlassFenceGate("glass_light_blue_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_LIGHT_BLUE_WALL = new GlassWall("glass_light_blue_wall", Material.GLASS);
	public static final Block GLASS_LIGHT_BLUE_GlassDebris = new GlassDebris("glass_light_blue_debris", Material.GLASS);
	public static final Block GLASS_LIGHT_BLUE_SLAB = new GlassSlab("glass_light_blue_slab");
public static final Block GLASS_LIME_STAIRS = new GlassStairs("glass_lime_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_LIME_FENCE = new GlassFence("glass_lime_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_LIME_FENCE_GATE = new GlassFenceGate("glass_lime_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_LIME_WALL = new GlassWall("glass_lime_wall", Material.GLASS);
	public static final Block GLASS_LIME_GlassDebris = new GlassDebris("glass_lime_debris", Material.GLASS);
	public static final Block GLASS_LIME_SLAB = new GlassSlab("glass_lime_slab");
public static final Block GLASS_MAGENTA_STAIRS = new GlassStairs("glass_magenta_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_MAGENTA_FENCE = new GlassFence("glass_magenta_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_MAGENTA_FENCE_GATE = new GlassFenceGate("glass_magenta_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_MAGENTA_WALL = new GlassWall("glass_magenta_wall", Material.GLASS);
	public static final Block GLASS_MAGENTA_GlassDebris = new GlassDebris("glass_magenta_debris", Material.GLASS);
	public static final Block GLASS_MAGENTA_SLAB = new GlassSlab("glass_magenta_slab");
public static final Block GLASS_WHITE_STAIRS = new GlassStairs("glass_white_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block GLASS_WHITE_FENCE = new GlassFence("glass_white_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block GLASS_WHITE_FENCE_GATE = new GlassFenceGate("glass_white_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block GLASS_WHITE_WALL = new GlassWall("glass_white_wall", Material.GLASS);
	public static final Block GLASS_WHITE_GlassDebris = new GlassDebris("glass_white_debris", Material.GLASS);
	public static final Block GLASS_WHITE_SLAB = new GlassSlab("glass_white_slab");
public static final Block HARDENED_CLAY_STAIRS = new ModdedStairs("hardened_clay_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_FENCE = new ModdedFence("hardened_clay_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_FENCE_GATE = new ModdedFenceGate("hardened_clay_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_WALL = new GlassWall("hardened_clay_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_DEBRIS = new Debris("hardened_clay_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_SLAB = new ModdedSlab.Half("hardened_clay_slab");
public static final Block HARDENED_CLAY_STAINED_BLUE_STAIRS = new ModdedStairs("hardened_clay_stained_blue_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_BLUE_FENCE = new ModdedFence("hardened_clay_stained_blue_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_BLUE_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_blue_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_BLUE_WALL = new GlassWall("hardened_clay_stained_blue_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_BLUE_DEBRIS = new Debris("hardened_clay_stained_blue_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_BLUE_SLAB = new ModdedSlab.Half("hardened_clay_stained_blue_slab");
public static final Block HARDENED_CLAY_STAINED_BROWN_STAIRS = new ModdedStairs("hardened_clay_stained_brown_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_BROWN_FENCE = new ModdedFence("hardened_clay_stained_brown_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_BROWN_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_brown_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_BROWN_WALL = new GlassWall("hardened_clay_stained_brown_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_BROWN_DEBRIS = new Debris("hardened_clay_stained_brown_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_BROWN_SLAB = new ModdedSlab.Half("hardened_clay_stained_brown_slab");
public static final Block HARDENED_CLAY_STAINED_CYAN_STAIRS = new ModdedStairs("hardened_clay_stained_cyan_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_CYAN_FENCE = new ModdedFence("hardened_clay_stained_cyan_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_CYAN_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_cyan_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_CYAN_WALL = new GlassWall("hardened_clay_stained_cyan_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_CYAN_DEBRIS = new Debris("hardened_clay_stained_cyan_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_CYAN_SLAB = new ModdedSlab.Half("hardened_clay_stained_cyan_slab");
public static final Block HARDENED_CLAY_STAINED_GREEN_STAIRS = new ModdedStairs("hardened_clay_stained_green_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_GREEN_FENCE = new ModdedFence("hardened_clay_stained_green_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_GREEN_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_green_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_GREEN_WALL = new GlassWall("hardened_clay_stained_green_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_GREEN_DEBRIS = new Debris("hardened_clay_stained_green_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_GREEN_SLAB = new ModdedSlab.Half("hardened_clay_stained_green_slab");
public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_STAIRS = new ModdedStairs("hardened_clay_stained_light_blue_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_FENCE = new ModdedFence("hardened_clay_stained_light_blue_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_light_blue_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_WALL = new GlassWall("hardened_clay_stained_light_blue_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_DEBRIS = new Debris("hardened_clay_stained_light_blue_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_LIGHT_BLUE_SLAB = new ModdedSlab.Half("hardened_clay_stained_light_blue_slab");
public static final Block HARDENED_CLAY_STAINED_LIME_STAIRS = new ModdedStairs("hardened_clay_stained_lime_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_LIME_FENCE = new ModdedFence("hardened_clay_stained_lime_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_LIME_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_lime_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_LIME_WALL = new GlassWall("hardened_clay_stained_lime_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_LIME_DEBRIS = new Debris("hardened_clay_stained_lime_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_LIME_SLAB = new ModdedSlab.Half("hardened_clay_stained_lime_slab");
public static final Block HARDENED_CLAY_STAINED_MAGENTA_STAIRS = new ModdedStairs("hardened_clay_stained_magenta_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_MAGENTA_FENCE = new ModdedFence("hardened_clay_stained_magenta_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_MAGENTA_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_magenta_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_MAGENTA_WALL = new GlassWall("hardened_clay_stained_magenta_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_MAGENTA_DEBRIS = new Debris("hardened_clay_stained_magenta_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_MAGENTA_SLAB = new ModdedSlab.Half("hardened_clay_stained_magenta_slab");
public static final Block HARDENED_CLAY_STAINED_ORANGE_STAIRS = new ModdedStairs("hardened_clay_stained_orange_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_ORANGE_FENCE = new ModdedFence("hardened_clay_stained_orange_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_ORANGE_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_orange_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_ORANGE_WALL = new GlassWall("hardened_clay_stained_orange_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_ORANGE_DEBRIS = new Debris("hardened_clay_stained_orange_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_ORANGE_SLAB = new ModdedSlab.Half("hardened_clay_stained_orange_slab");
public static final Block HARDENED_CLAY_STAINED_PINK_STAIRS = new ModdedStairs("hardened_clay_stained_pink_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_PINK_FENCE = new ModdedFence("hardened_clay_stained_pink_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_PINK_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_pink_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_PINK_WALL = new GlassWall("hardened_clay_stained_pink_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_PINK_DEBRIS = new Debris("hardened_clay_stained_pink_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_PINK_SLAB = new ModdedSlab.Half("hardened_clay_stained_pink_slab");
public static final Block HARDENED_CLAY_STAINED_PURPLE_STAIRS = new ModdedStairs("hardened_clay_stained_purple_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_PURPLE_FENCE = new ModdedFence("hardened_clay_stained_purple_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_PURPLE_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_purple_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_PURPLE_WALL = new GlassWall("hardened_clay_stained_purple_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_PURPLE_DEBRIS = new Debris("hardened_clay_stained_purple_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_PURPLE_SLAB = new ModdedSlab.Half("hardened_clay_stained_purple_slab");
public static final Block HARDENED_CLAY_STAINED_RED_STAIRS = new ModdedStairs("hardened_clay_stained_red_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_RED_FENCE = new ModdedFence("hardened_clay_stained_red_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_RED_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_red_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_RED_WALL = new GlassWall("hardened_clay_stained_red_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_RED_DEBRIS = new Debris("hardened_clay_stained_red_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_RED_SLAB = new ModdedSlab.Half("hardened_clay_stained_red_slab");
public static final Block HARDENED_CLAY_STAINED_WHITE_STAIRS = new ModdedStairs("hardened_clay_stained_white_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_WHITE_FENCE = new ModdedFence("hardened_clay_stained_white_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_WHITE_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_white_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_WHITE_WALL = new GlassWall("hardened_clay_stained_white_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_WHITE_DEBRIS = new Debris("hardened_clay_stained_white_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_WHITE_SLAB = new ModdedSlab.Half("hardened_clay_stained_white_slab");
public static final Block HARDENED_CLAY_STAINED_YELLOW_STAIRS = new ModdedStairs("hardened_clay_stained_yellow_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_YELLOW_FENCE = new ModdedFence("hardened_clay_stained_yellow_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_YELLOW_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_yellow_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_YELLOW_WALL = new GlassWall("hardened_clay_stained_yellow_wall", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_YELLOW_DEBRIS = new Debris("hardened_clay_stained_yellow_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_YELLOW_SLAB = new ModdedSlab.Half("hardened_clay_stained_yellow_slab");

public static final Block WOOL_COLORED_BLUE_STAIRS = new ModdedStairs("wool_colored_blue_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block WOOL_COLORED_BLUE_FENCE = new ModdedFence("wool_colored_blue_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block WOOL_COLORED_BLUE_FENCE_GATE = new ModdedFenceGate("wool_colored_blue_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block WOOL_COLORED_BLUE_WALL = new GlassWall("wool_colored_blue_wall", Material.GLASS);
	public static final Block WOOL_COLORED_BLUE_DEBRIS = new Debris("wool_colored_blue_debris", Material.GLASS);
	public static final Block WOOL_COLORED_BLUE_SLAB = new ModdedSlab.Half("wool_colored_blue_slab");
public static final Block WOOL_COLORED_LIGHT_BLUE_STAIRS = new ModdedStairs("wool_colored_light_blue_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block WOOL_COLORED_LIGHT_BLUE_FENCE = new ModdedFence("wool_colored_light_blue_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block WOOL_COLORED_LIGHT_BLUE_FENCE_GATE = new ModdedFenceGate("wool_colored_light_blue_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block WOOL_COLORED_LIGHT_BLUE_WALL = new GlassWall("wool_colored_light_blue_wall", Material.GLASS);
	public static final Block WOOL_COLORED_LIGHT_BLUE_DEBRIS = new Debris("wool_colored_light_blue_debris", Material.GLASS);
	public static final Block WOOL_COLORED_LIGHT_BLUE_SLAB = new ModdedSlab.Half("wool_colored_light_blue_slab");
public static final Block WOOL_COLORED_RED_STAIRS = new ModdedStairs("wool_colored_red_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block WOOL_COLORED_RED_FENCE = new ModdedFence("wool_colored_red_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block WOOL_COLORED_RED_FENCE_GATE = new ModdedFenceGate("wool_colored_red_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block WOOL_COLORED_RED_WALL = new GlassWall("wool_colored_red_wall", Material.GLASS);
	public static final Block WOOL_COLORED_RED_DEBRIS = new Debris("wool_colored_red_debris", Material.GLASS);
	public static final Block WOOL_COLORED_RED_SLAB = new ModdedSlab.Half("wool_colored_red_slab");
	
	public static final Block HARDENED_CLAY_STAINED_BLACK_STAIRS = new ModdedStairs("hardened_clay_stained_black_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_BLACK_FENCE = new ModdedFence("hardened_clay_stained_black_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_BLACK_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_black_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_BLACK_WALL = new GlassWall("hardened_clay_stained_black_wall", Material.ROCK);
	public static final Block HARDENED_CLAY_STAINED_BLACK_DEBRIS = new Debris("hardened_clay_stained_black_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_BLACK_SLAB = new ModdedSlab.Half("hardened_clay_stained_black_slab");
public static final Block HARDENED_CLAY_STAINED_SILVER_STAIRS = new ModdedStairs("hardened_clay_stained_silver_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_SILVER_FENCE = new ModdedFence("hardened_clay_stained_silver_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_SILVER_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_silver_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_SILVER_WALL = new GlassWall("hardened_clay_stained_silver_wall", Material.ROCK);
	public static final Block HARDENED_CLAY_STAINED_SILVER_DEBRIS = new Debris("hardened_clay_stained_silver_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_SILVER_SLAB = new ModdedSlab.Half("hardened_clay_stained_silver_slab");
public static final Block HARDENED_CLAY_STAINED_GRAY_STAIRS = new ModdedStairs("hardened_clay_stained_gray_stairs", Material.GLASS, Blocks.GLASS.getDefaultState(), true);
	public static final Block HARDENED_CLAY_STAINED_GRAY_FENCE = new ModdedFence("hardened_clay_stained_gray_fence", Material.GLASS, BlockPlanks.EnumType.OAK.getMapColor());
	public static final Block HARDENED_CLAY_STAINED_GRAY_FENCE_GATE = new ModdedFenceGate("hardened_clay_stained_gray_fence_gate", BlockPlanks.EnumType.OAK);
	public static final Block HARDENED_CLAY_STAINED_GRAY_WALL = new GlassWall("hardened_clay_stained_gray_wall", Material.ROCK);
	public static final Block HARDENED_CLAY_STAINED_GRAY_DEBRIS = new Debris("hardened_clay_stained_gray_debris", Material.GLASS);
	public static final Block HARDENED_CLAY_STAINED_GRAY_SLAB = new ModdedSlab.Half("hardened_clay_stained_gray_slab");


	//public static final Block YELLOW_GLASS_SLAB = new ModdedSlab("glass_yellow_slab", Material.GLASS, true);
	/*DEBRIS BLOCKS
	
	public static final Block DEBRIS1 = new BetterDebris("debri1", Material.GROUND, 0);
	public static final Block DEBRIS2 = new BetterDebris("debri2", Material.GROUND, 1);
	public static final Block DEBRIS3 = new BetterDebris("debri3", Material.GROUND, 2);
	public static final Block DEBRIS4 = new BetterDebris("debri4", Material.GROUND, 3);
	public static final Block DEBRIS5 = new BetterDebris("debri5", Material.GROUND, 4);
	public static final Block DEBRIS6 = new BetterDebris("debri6", Material.GROUND, 5);
	public static final Block DEBRIS7 = new BetterDebris("debri7", Material.GROUND, 6);
	public static final Block DEBRIS8 = new BetterDebris("debri8", Material.GROUND, 7);
	
	*/
	//public static final Block TRANSFERBLOCK = new BlockBase("transferblock", Material.IRON);
}
