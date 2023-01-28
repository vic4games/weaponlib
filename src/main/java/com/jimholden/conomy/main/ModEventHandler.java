package com.jimholden.conomy.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Predicates;
import com.jimholden.conomy.Main;
import com.jimholden.conomy.blocks.tileentity.TileEntityNode;
import com.jimholden.conomy.capabilities.CreditProvider;
import com.jimholden.conomy.client.gui.NewInventory;
import com.jimholden.conomy.containers.ContainerInvExtend;
import com.jimholden.conomy.containers.InventoryInjectorServer;
import com.jimholden.conomy.containers.slots.AdvancedSlot;
import com.jimholden.conomy.containers.slots.BackpackSlots;
import com.jimholden.conomy.containers.slots.ISaveableSlot;
import com.jimholden.conomy.containers.slots.RigSlots;
import com.jimholden.conomy.containers.slots.SlotDisabled;
import com.jimholden.conomy.economy.data.EconomyDatabase;
import com.jimholden.conomy.entity.EntityBaseZombie;
import com.jimholden.conomy.inventory.IInvCapa;
import com.jimholden.conomy.inventory.InvProvider;
import com.jimholden.conomy.items.BackpackItem;
import com.jimholden.conomy.items.ISaveableItem;
import com.jimholden.conomy.items.RigItem;
import com.jimholden.conomy.medical.ConsciousCapability;
import com.jimholden.conomy.medical.ConsciousProvider;
import com.jimholden.conomy.medical.IConscious;
import com.jimholden.conomy.medical.PainUtility;
import com.jimholden.conomy.util.BoxUtil;
import com.jimholden.conomy.util.HitUtil;
import com.jimholden.conomy.util.Reference;
import com.jimholden.conomy.util.RopeUtil;
import com.jimholden.conomy.util.VectorUtil;
import com.jimholden.conomy.util.WorldListener;
import com.jimholden.conomy.util.aitools.ZombieUtil;
import com.jimholden.conomy.util.packets.AttackYawPacket;
import com.jimholden.conomy.util.packets.BloodParticlePacket;
import com.jimholden.conomy.util.packets.CreditSurveyPacket;
import com.jimholden.conomy.util.packets.InventorySurveyPacket;
import com.jimholden.conomy.util.packets.MessageUpdateCredits;
import com.jimholden.conomy.util.packets.ParticlePacket;
import com.jimholden.conomy.util.packets.PowerSurveyPacket;
import com.jimholden.conomy.util.packets.ZombieUpdatePacket;
import com.jimholden.conomy.util.packets.medical.ConsciousSurveyPacket;
import com.jimholden.conomy.vmwcompat.VMWSlotAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiContainerCreative.ContainerCreative;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.FlowerEntry;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.internal.FMLMessage.OpenGui;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import scala.swing.TextComponent;

public class ModEventHandler {
	private int counter = 0;
	private int seconds = 0;
	public ArrayList<NodeChooser> nodeList = new ArrayList<NodeChooser>();
	private static final InventoryInjectorServer injector = new InventoryInjectorServer();
	public static int reducerValue = 500;
	
	
	public static class NodeChooser extends WeightedRandom.Item
	{
		public final TileEntity tile;
		public NodeChooser(TileEntity tile, int itemWeightIn) {
			super(itemWeightIn);
			this.tile = tile;
			// TODO Auto-generated constructor stub
		}
		
	}
	
	
	

	/*
	public void itemEvent(ItemEvent event) {
		EntityItem entityItem = event.getEntityItem();
		ItemStack stack = entityItem.getItem();
		if(stack.getTagCompound().getDouble("weight") == 0) {
			stack.getTagCompound().setDouble("weight", 0.1);
		}
	}*/
	
	
	public void addNodeToSel(TileEntity tile, int weight)
	{
		this.nodeList.add(new NodeChooser(tile, weight));
	}
	
	//Livingatt
	
	
	/*
	@SubscribeEvent
	public void soundEvent(SoundSourceEvent event) {
		
		
		World world = DimensionManager.getWorld(-1);
		//event.getSound().getCategory()
		//System.out.println();
		if(world != null) {
			float x = event.getSound().getXPosF();
			float y = event.getSound().getYPosF();
			float z = event.getSound().getZPosF();
			List<EntityBaseZombie> list = world.getEntitiesWithinAABB(EntityBaseZombie.class, new AxisAlignedBB(x-5, y-5, z-5, x + 5, y + 5, z + 5));
			if(list != null) {
				if(!list.isEmpty()) {
					System.out.println(list);
				}
			}
			
		}
		
		
		
		//System.out.println(event.getSound());
		
	}
	*/
	
	public static final AttributeModifier BUSTED_LEG_MODIFIER = new AttributeModifier("Broken Leg", -0.2, 2);
	
	public static ContainerInvExtend overlayCont = null;
	
	@SubscribeEvent
	public void entityFall(LivingFallEvent event) {
		
		if(event.getDistance() < 9.5f) return;
		if(event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if(!player.isCreative() && !player.isSpectator() && event.getDistance() >= 9.5f) {
				
				IConscious capa = player.getCapability(ConsciousProvider.CONSCIOUS, null);
				IAttributeInstance atty = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				capa.setLegHealth(0);
				atty.applyModifier(BUSTED_LEG_MODIFIER);
			}
		}
	}
	
	
	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		if(event.getWorld().isRemote) return;
		int dimID = event.getWorld().provider.getDimension();
		//CULog.dbg("adding ZA world listener for dimID: " + dimID + ", remote?: " + event.getWorld().isRemote);
		event.getWorld().addEventListener(new WorldListener(dimID));
		
		try {
			EconomyDatabase.load();
		} catch (Exception e) {
			
			System.err.println("Error loading Conomy's Economy Database!");
			e.printStackTrace();
		}
		System.out.println("Conomy has loaded the economy database! Ready to rumble.");
	}
	
	public void saveEconomyDatabase() {
		EconomyDatabase.saveEconomyDatabase();
		System.out.println("Economy database has been saved!");
	}
	
	@SubscribeEvent 
	public void shutDownWorld(WorldEvent.Save event) {
		if(event.getWorld().isRemote) return;
		saveEconomyDatabase();
	}
	
	
	
	@SubscribeEvent
	public void unloadWorld(WorldEvent.Unload event) {
		if(event.getWorld().isRemote) return;
		saveEconomyDatabase();
	}

	@SubscribeEvent
	public void livingAttack(LivingAttackEvent event) {
		if(event.getEntity().world.isRemote) return;
		//System.out.println(event.getSource().isProjectile());
		
		
		if(event.getEntity() instanceof EntityPlayer && event.getSource().isProjectile()) {
			WorldServer ws = (WorldServer) event.getEntity().world;
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			
			
			//System.out.println("yooo");
			
			//System.out.println("yes project");
			//System.out.println(player.posX);
			
			Vec3d vec2 = new Vec3d(event.getSource().getImmediateSource().motionX, event.getSource().getImmediateSource().motionY, event.getSource().getImmediateSource().motionZ);
			///System.out.println(vec2);
			
			//NEW
			Vec3d normalizedVec2 = vec2.normalize();
			int reducer = this.reducerValue;
			Vec3d speedVec = new Vec3d(vec2.x*(event.getAmount()/reducer), vec2.y*(event.getAmount()/reducer), vec2.z*(event.getAmount()/reducer));
			
			
			Vec3d posPlayer = new Vec3d(player.posX, player.posY, player.posZ);
			
			Entity ent = event.getSource().getImmediateSource();
			
			Vec3d projPosVec = new Vec3d(ent.posX, ent.posY, ent.posZ);
			Vec3d projPosVec2 = new Vec3d(ent.posX, ent.posY, ent.posZ);
			
			Vec3d vec = event.getSource().getDamageLocation();
			
			Vec3d newVec = projPosVec2.add(vec2);
			
			RayTraceResult result = player.getEntityBoundingBox().grow(0.3).calculateIntercept(projPosVec, newVec);
			//System.out.println(posPlayer);
			if(result != null) {
				//System.out.println("hi!");
				//System.out.println(result.hitVec);

				Main.NETWORK.sendTo(new BloodParticlePacket(result.hitVec.x, result.hitVec.y, result.hitVec.z, (float) speedVec.x, (float) speedVec.y,(float)  speedVec.z), (EntityPlayerMP) player);
				Main.NETWORK.sendToAllTracking(new BloodParticlePacket(result.hitVec.x, result.hitVec.y, result.hitVec.z, (float) speedVec.x, (float) speedVec.y,(float)  speedVec.z), (EntityPlayerMP) player);
				
				
				
				/* OLD SYS
				Main.NETWORK.sendTo(new BloodParticlePacket(result.hitVec.x, result.hitVec.y, result.hitVec.z, (float) vec2.x/2, (float) vec2.y/2,(float)  vec2.z/2), (EntityPlayerMP) player);
				Main.NETWORK.sendToAllTracking(new BloodParticlePacket(result.hitVec.x, result.hitVec.y, result.hitVec.z, (float) vec2.x/2, (float) vec2.y/2,(float)  vec2.z/2), (EntityPlayerMP) player);
				*/
				
			}
			
		//	ws.spawnParticle(particleType, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
			//ws.spawnParticle(particleType, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
			//System.out.println("spawning!");
			
			
			//ws.spawnP
			//ws.spawnPa
			
			//ws.spawn
			
			//ws.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, 0.1, 0.1, 0.1, 0);
			
		//	ws.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, 2000, 0, 0, 0, 1, null);
			
			
			//ws.spawnParticle(EnumParticleTypes.FLAME, player.posX, player.posY, player.posZ, vec2.x, vec2.y, vec2.z, 100, 10);
		
			
			//ws.spawnParticle(particleType, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
			
		}
		
	}
	
	@SubscribeEvent
	public void openContainer(PlayerContainerEvent.Open event) {
		
		if(CompatibilityChecker.shouldNotInjectGUI()) return;
		EntityPlayer player = event.getEntityPlayer();
		if(overlayCont == null && player != null) {
			overlayCont = new ContainerInvExtend(player.inventory, player.world.isRemote, player);
		}
		//if(event.getEntityPlayer().isCreative()) return;
		
		/*
		if(!(event.getContainer() instanceof ContainerCreative) && !(event.getContainer() instanceof ContainerInvExtend)) {
			Container c = event.getContainer();
			ItemStackHandler handler = new ItemStackHandler(1);
			addSlotToContainer(c, new SlotItemHandler(handler, 0, 0, 150));
			//c.inventorySlots.add(new SlotItemHandler(handler, 0, 0, 150));
			//c.inventorySlots.add(index, element);
		} */
		
		/*
		if(player != null) {
			Container c = event.getContainer();
			for(int x = 0; x < overlayCont.inventorySlots.size(); ++x) {
				addSlotToContainer(c, overlayCont.inventorySlots.get(x));
			}
			
		
			
		}*/
		if(!(event.getContainer() instanceof ContainerInvExtend)) {
			System.out.println("server injecting");
			injector.injectSlots(event);
		}
		

		
		
		
		try {
			Container c = event.getContainer();
			for(int i = 0; i < c.inventorySlots.size(); ++i) {
				Slot slot = c.inventorySlots.get(i);
				if(c.inventorySlots.get(i).getSlotIndex() >= 9 && c.inventorySlots.get(i).getSlotIndex() < 36 && c.inventorySlots.get(i).inventory == event.getEntityPlayer().inventory) {
					c.inventorySlots.remove(i);
					c.inventorySlots.add(i, new SlotDisabled(slot));
				}
				
			}
		} catch(Exception e) {
			
		}
		
		/*
	
		try
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            Container c = event.getContainer();

            for (int i = 0; i < c.inventorySlots.size(); ++i)
            {
                Slot s = c.getSlot(i);
                if(s instanceof BackpackSlots) {
                	AdvancedSlot wrapper = new AdvancedSlot(s, c);
                    c.inventorySlots.remove(i);
                    c.inventorySlots.add(i, wrapper);
                    s = c.getSlot(i);
                }
                
                
                
            }
        }
        catch (Exception ex)
        {
            
        }
		*/
	}
	
	@SubscribeEvent
	public void knockBack(LivingKnockBackEvent event) {
		
		

		
		
		
		if(event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving() != event.getAttacker()) {
			EntityPlayer p = (EntityPlayer) event.getEntityLiving();
			Main.NETWORK.sendTo(new AttackYawPacket(p.attackedAtYaw), (EntityPlayerMP) p);
		}
	}
	
	
	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerDied(LivingDeathEvent event) {
		
		
		if(event.getEntityLiving() instanceof EntityPlayer) {
			List<EntityBaseZombie> zombies = event.getEntityLiving().world.getEntities(EntityBaseZombie.class, EntitySelectors.IS_ALIVE);
			for(EntityBaseZombie zombie : zombies) {
				if(zombie.getAttackTarget() == event.getEntityLiving()) {
					Main.NETWORK.sendToServer(new ZombieUpdatePacket(zombie.getEntityId()));
					zombie.setAttackTarget(null);
					zombie.watcher = null;
				}
			}
		}
		
		if(event.getEntityLiving() instanceof EntityBaseZombie) {
			EntityBaseZombie zombie = ((EntityBaseZombie) event.getEntityLiving());
			if(zombie.getDecomposeTime() <= 0) return;
			event.setCanceled(true);
			
			
			Entity attacker = event.getSource().getImmediateSource();
			EnumFacing deathDirection = HitUtil.getDirectionHitMelee(attacker, zombie);
			if(deathDirection != null) zombie.putToDeathState(deathDirection);
			else zombie.putToDeathState(EnumFacing.NORTH);
			
			zombie.setHealth(0.1F);
		}
		/*
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			IConscious data = player.getCapability(ConsciousProvider.CONSCIOUS, null);
			
			if(data.isDowned() == 0) {
				
				
				data.setDowned(1);
				data.setDownTimer(2400);
				event.setCanceled(true);
				player.setHealth(0.5F);
			} else {
				data.setDowned(0);
				data.setDownTimer(2400);
				data.setBlood(12000);
				data.setIsBleed(false);
			}
			
			//player.setEntityInvulnerable(true);
			
			
		}*/
		
		
	}
	
	public static final Random rand = new Random();
	
	@SubscribeEvent
	public void hurtEvent(LivingHurtEvent event) {
		try {
			/*
			if(event.getSource().getTrueSource() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
				BlockPos playerPos = player.getPosition();
				BlockPos forbidden = new BlockPos(-168, 4, 630);
				if(VectorUtil.areBlockPosEqual(playerPos, forbidden)) {
					System.out.println("yup");
					event.setCanceled(true);
					return;
				}
			}
			*/
			
			/*
			 * TEST!! EXPERIMENTAL */
			 
			
			if(event.getEntityLiving() instanceof EntityBaseZombie && !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
				EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
				EntityBaseZombie zombie = (EntityBaseZombie) event.getEntityLiving();
			
			}
			
			
			if(event.getEntityLiving() instanceof EntityBaseZombie && !(event.getSource() == DamageSource.FALL)) {
				//System.out.println(event.getSource().getImmediateSource());
				Entity zombie = event.getEntityLiving();
				
				if(!event.getSource().isProjectile()) {
					Entity player = (Entity) event.getSource().getImmediateSource();
					
					RayTraceResult result = HitUtil.traceMeleeHit(player, zombie);
					if(result != null) {
						double distanceBetweenVec = result.hitVec.distanceTo(zombie.getPositionEyes(1.0F));
						if(!(distanceBetweenVec < 0.45)) {
							event.setAmount(event.getAmount()*0.1F);
						} else {
							zombieHeadShotEvent((EntityBaseZombie) zombie, event);
						}
						//System.out.println(dot);
						//System.out.println("Hit Vector: " + result.hitVec);
						//System.out.println("Entity Vector: " + zombie.getPositionVector());
						//result.hitVec
						//Main.NETWORK.sendToAll(new ParticlePacket(startVec, endVec, result.hitVec));
						
					} else {
						event.setAmount(event.getAmount()*0.1F);
					}
				} else {
					RayTraceResult result  = HitUtil.traceProjectilehit(event.getSource().getImmediateSource(), zombie);
					if(result != null) {
						//System.out.println("Hit Vector: " + result.hitVec);
						double distanceBetweenVec = result.hitVec.distanceTo(zombie.getPositionEyes(1.0F));
						if(!(distanceBetweenVec < 0.55)) {
							event.setAmount(event.getAmount()*0.1F);
						} else {
							
							zombieHeadShotEvent((EntityBaseZombie) zombie, event);
						}
						
						
						//System.out.println(distanceBetweenVec);
						//Main.NETWORK.sendToAll(new ParticlePacket(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), result.hitVec));
					
					}
					}
				//System.out.println(event.getAmount());
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(event.getEntity() instanceof EntityPlayer) {
			if(event.getAmount() >= ((EntityPlayer) event.getEntity()).getHealth()) return;
			if(event.getAmount() > 4.0F) {
				//System.out.println("yes");
				int randomNumber = rand.nextInt(100 - 0) + 0;
				IConscious conscious = ((EntityPlayer) event.getEntity()).getCapability(ConsciousProvider.CONSCIOUS, null);
				System.out.println(randomNumber);
				if(randomNumber < 75 && !conscious.isBleeding()) {
					if(!conscious.isBleeding()) {
						((EntityPlayer)event.getEntity()).sendMessage(new TextComponentString(TextFormatting.RED + "You took much damage at once, you are now " + TextFormatting.BOLD + "bleeding!"));
					//	conscious.setBlood(0);
						conscious.setIsBleed(true);
					}
				}
				
				
			}
			
			if(event.getAmount() > 10.0F) {
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				IConscious data = player.getCapability(ConsciousProvider.CONSCIOUS, null);
			//	data.setDowned(1);
				//event.setCanceled(true);
				//player.setHealth(0.5F);
			}
			
			// apply pain levels
			if(event.getEntityLiving() instanceof EntityPlayer) {
				
				EntityPlayer player = (EntityPlayer) event.getEntityLiving();
				
				// apply armor & potion calculations
				float damage = event.getAmount();
				if (!event.getSource().isUnblockable())
		        {
					// armor
		            damage = CombatRules.getDamageAfterAbsorb(damage, (float)player.getTotalArmorValue(), (float)player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
		        }

		       
				
				
				double painIncrement = PainUtility.getPainFromDamage(PainUtility.getPainFactor(player.getMaxHealth(), player.getHealth()), damage);
				IConscious data= player.getCapability(ConsciousProvider.CONSCIOUS, null);
				System.out.println("painful :( " + painIncrement);
				data.setPain(data.getPainLevel()+painIncrement);
			}
			
		}
	}
	
	@SubscribeEvent
	public void healEvent(LivingHealEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			
			
			
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		
			IConscious con = player.getCapability(ConsciousProvider.CONSCIOUS, null);
			
			if(con.getWaterLevel() == 0) {
				event.setAmount(event.getAmount()*0.5f);
			} else if(con.getWaterLevel() < 5) {
				event.setAmount(event.getAmount()*0.8f);
			}
			double painIncrement = PainUtility.getPainFromDamage(PainUtility.getPainFactor(player.getMaxHealth(), player.getHealth())*2, event.getAmount());
			con.setPain(con.getPainLevel()-painIncrement);
			
		}
	}
	
	public void zombieHeadShotEvent(EntityBaseZombie zombie, LivingHurtEvent evt) {
		if(evt.getAmount() >= zombie.getHealth()) {
			zombie.boomHead();
		}
	}
	

	@SubscribeEvent
	public void Clone(Clone event)
	{
		if(event.getOriginal().hasCapability(CreditProvider.CREDIT_CAP, null) && event.isWasDeath())
		{
			int initialBal = event.getOriginal().getCapability(CreditProvider.CREDIT_CAP, null).getBalance();
			event.getEntity().getCapability(CreditProvider.CREDIT_CAP, null).set(initialBal);
			ItemStackHandler handle = event.getOriginal().getCapability(InvProvider.EXTRAINV, null).getHandler();
			IInvCapa capa = event.getEntity().getCapability(InvProvider.EXTRAINV, null);
			if(event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
				for(int x = 0; x < handle.getSlots(); ++x) {
					capa.setStackInSlot(x, handle.getStackInSlot(x));
				}
			}
			
			//event.getEntity().getCapability(InvProvider.EXTRAINV, null).set
		}
	}
	/*
	@SubscribeEvent
	public void LivingDeathEvent(LivingDeathEvent event)
	{
		if(event.getEntity().hasCapability(CreditProvider.CREDIT_CAP, null))
		{
			System.out.println("1 " + event.getEntity());
			System.out.println("2 " + event.getEntityLiving());
			int initialBal = event.getEntity().getCapability(CreditProvider.CREDIT_CAP, null).getBalance();
			event.getEntity().getCapability(CreditProvider.CREDIT_CAP, null).set(initialBal);
		}
	}
	*/
	
	
	
	
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity e = event.getTarget();
		if(event.getWorld().isRemote) return;
		if(!(e instanceof EntityPlayer)) return;
		EntityPlayerMP target = (EntityPlayerMP) e;
		if(e.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned() == 1) {
			player.openGui(Main.instance, Reference.GUI_LOOTBODY, event.getWorld(), target.getEntityId(), 0, 0);
		}
		
		
	}
	
	@SubscribeEvent
	public static void moveEvent(LivingUpdateEvent event) {
		System.out.println("shqty");

	}
	
	@SubscribeEvent
	public void loginEvent(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if(player.hasCapability(InvProvider.EXTRAINV, null)) {
			for(int x = 0; x < 7; x++) {
				Main.NETWORK.sendTo(new InventorySurveyPacket(x, player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(x), player.getEntityId()), (EntityPlayerMP) player);
			}
			
		}
	}
	
	public double tick = 0;
	public HashMap<EntityPlayer, Float> walkTime = new HashMap<>();
	
	public boolean isPlayerWalking(EntityPlayer player) {
		if(!walkTime.containsKey(player)) {
			walkTime.put(player, player.distanceWalkedModified);
			return false;
		}
		
		
		float diff = player.distanceWalkedModified - walkTime.get(player);

		if(diff != 0.0) {
			walkTime.put(player, player.distanceWalkedModified);
			return true;
		} else {
			walkTime.put(player, player.distanceWalkedModified);
		}
		return false;
	}
	
	public static SplittableRandom fastRandom = new SplittableRandom(404094);
	
	
	@SubscribeEvent
	public void spawnEvent(LivingSpawnEvent event) {
		if(event.getEntity() instanceof EntityZombie ||
				event.getEntity() instanceof EntitySkeleton ||
				event.getEntity() instanceof EntityCreeper ||
				event.getEntity() instanceof EntitySpider ||
				event.getEntity() instanceof EntityZombieVillager ||
				event.getEntity() instanceof EntityEnderman || 
				event.getEntity() instanceof EntityWitch) {
			//
			//event.setCanceled(true);
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public void worldTick(WorldTickEvent event)
	{
		
		if(!event.world.isRemote) {
			
		}
		
		if(event.phase == Phase.END) {
			tick++;
			if(tick == Integer.MAX_VALUE) tick = 0;
		}
		
		counter += 1;
		if(event.world != null && !event.world.isRemote) {
			List pList = event.world.loadedTileEntityList;
			ArrayList<TileEntity> nList = new ArrayList<TileEntity>();
			for(int x = 0; x < pList.size(); x++)
			{
				if(pList.get(x) instanceof TileEntityNode)
				{
					nList.add((TileEntity) pList.get(x));
				}
			}
			
			
			if(!event.world.loadedEntityList.isEmpty()) {
				for(EntityPlayer player : event.world.playerEntities) {
					if(!(player instanceof EntityPlayerMP))
						continue;
					
					
					if(event.phase == Phase.END) {
						// Send ghoul sound
						
						if(!player.isSneaking() && isPlayerWalking(player) && !player.isCreative() && !player.isSpectator()) {
							
							int odds = 12;
							if(player.isSprinting()) {
								odds = 16;
							}
							
							
							if(fastRandom.nextInt(101) < odds) {
								ZombieUtil.ghoulAlertEvent(player.world, player, 10);
								
							}
							
							//System.out.println("plug walk " + player.getDisplayNameString());
						}
					}
					//player.respawnPlayer();
					
					player.getCapability(ConsciousProvider.CONSCIOUS, null).markDirty(true);
					if(player.hasCapability(CreditProvider.CREDIT_CAP, null)) {
						//Main.NETWORK.sendTo(new MessageUpdateCredits(player.getCapability(CreditProvider.CREDIT_CAP, null).getBalance()), (EntityPlayerMP) player);
						Main.NETWORK.sendTo(new CreditSurveyPacket(player.getCapability(CreditProvider.CREDIT_CAP, null).getBalance()), (EntityPlayerMP) player);
					}
					
					/*
					if(player.hasCapability(InvProvider.EXTRAINV, null)) {
						for(int x = 0; x < 7; x++) {
							Main.NETWORK.sendTo(new InventorySurveyPacket(x, player.getCapability(InvProvider.EXTRAINV, null).getStackInSlot(x)), (EntityPlayerMP) player);
						}
						
					}
					*/
					
					if(player.hasCapability(ConsciousProvider.CONSCIOUS, null)) {
						//System.out.println("uf: " + player.getCapability(ConsciousProvider.CONSCIOUS, null).isDowned());
						
						IConscious conscious = player.getCapability(ConsciousProvider.CONSCIOUS, null);
						
						if(player.hasCapability(InvProvider.EXTRAINV, null)) {
							IInvCapa invCapa = player.getCapability(InvProvider.EXTRAINV, null);
									if(conscious.isDirty()) {
										for(int m = 0; m < invCapa.getHandler().getSlots(); ++m) {
											Main.NETWORK.sendToAllTracking(new InventorySurveyPacket(m, invCapa.getHandler().getStackInSlot(m), player.getEntityId()), player);
										}
										
									}
						}
						
						/*
						if(conscious.isDowned() == 1 && event.phase == Phase.END) {
							conscious.tickDowned();
							
							if(conscious.getDownTimer() <= 0) {
								conscious.setDownTimer(600);
								conscious.setDowned(0);
								player.setEntityInvulnerable(false);

							}
							
						}*/
						
						
						if(event.phase == Phase.END && !player.isCreative() && conscious.getWaterLevel() > 0) {
							
							if(player.isSprinting()) {
								if(this.tick % 750 == 0) {
									conscious.decreaseWaterTick();
								}
							} else {
								if(this.tick % 1500 == 0) {
									conscious.decreaseWaterTick();
									
								}
							}
							
							
							
						}
						
						if(event.phase == Phase.END && !player.isCreative() && conscious.getWaterLevel() == 0) {
							if(this.tick%300 == 0) {
								player.attackEntityFrom(DamageSource.STARVE, 3.0f);
							}
						}
						
						if(conscious.isBleeding() && event.phase == Phase.END && conscious.isDowned() == 0) {
							conscious.tickBleed();
							
							
							if(conscious.getBlood() < 0) {
								player.attackEntityFrom(DamageSource.MAGIC, 5.0F);
							}
			
						}
						
						if(!conscious.isBleeding() && event.phase == Phase.END && conscious.getBlood() < ConsciousCapability.MAX_BLOOD) {
							conscious.setBlood(conscious.getBlood()+1);
						}
						
						if(event.phase == Phase.END && conscious.hasSplint() && conscious.getLegHealth() < ConsciousCapability.MAX_LEG_HEALTH) {
							conscious.setLegHealth(conscious.getLegHealth()+1);
							
							if(conscious.getLegHealth() >= ConsciousCapability.MAX_LEG_HEALTH) {
								conscious.setLegHealth(ConsciousCapability.MAX_LEG_HEALTH);
								conscious.setHasSplint(false);
							}
						}
						
						double rF = 0.999+0.0002*(((player.getMaxHealth()-player.getHealth()))/20.0);
						//System.out.println(rF);
						
						conscious.setPain(conscious.getPainLevel()*rF);
						
						conscious.updateApplicator();
						Main.NETWORK.sendTo(new ConsciousSurveyPacket(conscious.isDowned(), conscious.getBlood(), conscious.isBleeding(), player.getEntityId(), conscious.getDownTimer(), conscious.isDirty(), conscious.getWeight(), conscious.getWaterLevel(), conscious.getPainLevel(), conscious.getApplicator(), conscious.getLegHealth(), conscious.hasSplint()), (EntityPlayerMP) player);
						Main.NETWORK.sendToAllTracking(new ConsciousSurveyPacket(conscious.isDowned(), conscious.getBlood(), conscious.isBleeding(), player.getEntityId(), conscious.getDownTimer(), conscious.isDirty(), conscious.getWeight(), conscious.getWaterLevel(), conscious.getPainLevel(), conscious.getApplicator(), conscious.getLegHealth(), conscious.hasSplint()), (EntityPlayerMP) player);
					}
					
					if(!nList.isEmpty())
					{
						for(TileEntity tilen : nList)
						{
							int bal = ((TileEntityNode) tilen).totalPower;
							Main.NETWORK.sendTo(new PowerSurveyPacket(bal, tilen.getPos().getX(), tilen.getPos().getY(), tilen.getPos().getZ()), (EntityPlayerMP) player);
						}
					}
					
					
					/*
					else {
						Main.NETWORK.sendTo(new )
					}
					*/	

						//PacketDispatcher.sendTo(new RadSurveyPacket(player.getCapability(RadiationCapability.EntityRadiationProvider.ENT_RAD_CAP, null).getRads()), (EntityPlayerMP) player);
						
				}
 
			}
		}
		if(!event.world.isRemote)
		{
			if(counter == 20)
			{
				counter = 0;
				seconds += 1;
			}
			if(seconds == 10)
			{
				List tList = event.world.loadedTileEntityList;
				ArrayList<TileEntity> newList = new ArrayList<TileEntity>();
				//ArrayList<Integer> weights = new ArrayList<Integer>();
				//System.out.println(tList);
				for(int x = 0; x < tList.size(); x++)
				{
					//System.out.println(tList.get(x).getClass() + " | " + (tList.get(x) instanceof TileEntityNode) + " | ");
					if((tList.get(x) instanceof TileEntityNode))
					{
						if(((TileEntityNode) tList.get(x)).getPower() != 0)
						{
							newList.add((TileEntity) tList.get(x));
						}
						
					}
				}
				
				if(!newList.isEmpty())
				{
					/* WORKS BUT OLD
					Random randomizer = new Random();
					TileEntity choice = newList.get(randomizer.nextInt(newList.size()));
					if(((TileEntityNode) choice).isCompatItem())
					{
						int bal = ((TileEntityNode) choice).deviceBalance();
						((TileEntityNode) choice).setDeviceBalance(bal + 2750);
					}
					*/
					for(TileEntity tile : newList)
					{
						addNodeToSel(tile, ((TileEntityNode) tile).totalPower);
					}
					Random rand = new Random();
					//System.out.println(this.nodeList + "");
					//System.out.println(newList + "");
					NodeChooser winner = (NodeChooser) WeightedRandom.getRandomItem(rand, nodeList);
					this.nodeList.clear();
					double bal = ((TileEntityNode) winner.tile).deviceBalance();
					((TileEntityNode) winner.tile).setDeviceBalance((int) (bal + 2750));
					
					/*
					for(EntityPlayer player : event.world.playerEntities)
					{
						//player.sendMessage(new TextComponentString("30 seconds has elapsed"));
			
						//player.sendMessage(new TextComponentString(TextFormatting.GOLD + ">> " + choice.toString() + " | " + ((TileEntityNode) choice).getPower()));
						//player.sendMessage(new TextComponentString(TextFormatting.GOLD + ">> " + winner.tile + " POWER: " + winner.itemWeight));
						
						
					}
					*/
					
				}
				
				
				//Item selectedItem = new EnumeratedDistribution<>(itemWeights).sample();
				//System.out.println(WeightedRandom.getRandomItem(newList, weights));
				//System.out.println()
				
				seconds = 0;
			}
		}
		
		
		
		
	}
	
	
	
	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save event) {
		
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load event) {
		
	}

}
