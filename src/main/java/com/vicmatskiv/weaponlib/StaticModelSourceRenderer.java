package com.vicmatskiv.weaponlib;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleStaticModelSourceRenderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class StaticModelSourceRenderer extends CompatibleStaticModelSourceRenderer {

	public static class Builder {
		private Consumer<ItemStack> entityPositioning;
		private Consumer<ItemStack> inventoryPositioning;
		private BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning;
		private BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning;
		
		private BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning;
		private BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning;
		private BiConsumer<ModelBase, ItemStack> inventoryModelPositioning;
		private BiConsumer<ModelBase, ItemStack> entityModelPositioning;
		
		private Consumer<RenderContext<RenderableState>> firstPersonLeftHandPositioning;
		private Consumer<RenderContext<RenderableState>> firstPersonRightHandPositioning;
		
		private String modId;
		private ModContext modContext;
		private boolean isHiddenInInventory;
		
		public Builder withModId(String modId) {
			this.modId = modId;
			return this;
		}
		
		public Builder withHiddenInventory(boolean isHiddenInInventory) {
		    this.isHiddenInInventory = isHiddenInInventory;
		    return this;
		}
		
		public Builder withModContext(ModContext modContext) {
		    this.modContext = modContext;
		    return this;
		}

		public Builder withFirstPersonPositioning(BiConsumer<EntityPlayer, ItemStack> firstPersonPositioning) {
			this.firstPersonPositioning = firstPersonPositioning;
			return this;
		}
		
		public Builder withFirstPersonHandPositioning(
                Consumer<RenderContext<RenderableState>> leftHand,
                Consumer<RenderContext<RenderableState>> rightHand) 
        {
            this.firstPersonLeftHandPositioning = leftHand;
            this.firstPersonRightHandPositioning = rightHand;
            return this;
        }
		
		public Builder withEntityPositioning(Consumer<ItemStack> entityPositioning) {
			this.entityPositioning = entityPositioning;
			return this;
		}
		
		public Builder withInventoryPositioning(Consumer<ItemStack> inventoryPositioning) {
			this.inventoryPositioning = inventoryPositioning;
			return this;
		}

		public Builder withThirdPersonPositioning(BiConsumer<EntityPlayer, ItemStack> thirdPersonPositioning) {
			this.thirdPersonPositioning = thirdPersonPositioning;
			return this;
		}

		public Builder withFirstPersonModelPositioning(BiConsumer<ModelBase, ItemStack> firstPersonModelPositioning) {
			this.firstPersonModelPositioning = firstPersonModelPositioning;
			return this;
		}
		
		public Builder withEntityModelPositioning(BiConsumer<ModelBase, ItemStack> entityModelPositioning) {
			this.entityModelPositioning = entityModelPositioning;
			return this;
		}
		
		public Builder withInventoryModelPositioning(BiConsumer<ModelBase, ItemStack> inventoryModelPositioning) {
			this.inventoryModelPositioning = inventoryModelPositioning;
			return this;
		}

		public Builder withThirdPersonModelPositioning(BiConsumer<ModelBase, ItemStack> thirdPersonModelPositioning) {
			this.thirdPersonModelPositioning = thirdPersonModelPositioning;
			return this;
		}

		public StaticModelSourceRenderer build() {
			if(modId == null) {
				throw new IllegalStateException("ModId is not set");
			}
			
			if(inventoryPositioning == null) {
				inventoryPositioning = itemStack -> {
				    if(isHiddenInInventory) GL11.glScalef(0f, 0f, 0f);
				    else GL11.glTranslatef(0,  0.12f, 0);
				};
			}
			
			if(entityPositioning == null) {
				entityPositioning = itemStack -> {
				};
			}
			
			if(firstPersonPositioning == null) {
				firstPersonPositioning = (player, itemStack) -> {
				};
			}
			
			if(thirdPersonPositioning == null) {
				thirdPersonPositioning = (player, itemStack) -> {
//					GL11.glTranslatef(-0.4F, 0.2F, 0.4F);
//					GL11.glRotatef(-45F, 0f, 1f, 0f);
//					GL11.glRotatef(70F, 1f, 0f, 0f);
				};
			}
			
			if(inventoryModelPositioning == null) {
				inventoryModelPositioning = (m, i) -> { 
				    if(isHiddenInInventory) GL11.glScalef(0f, 0f, 0f);
				 };
			}
			
			if(entityModelPositioning == null) {
				entityModelPositioning = (m, i) -> {};
			}
			
			if(firstPersonModelPositioning == null) {
				firstPersonModelPositioning = (m, i) -> {};
			}
			
			if(thirdPersonModelPositioning == null) {
				thirdPersonModelPositioning = (m, i) -> {};
			}
			
			if(firstPersonLeftHandPositioning == null) {
			    firstPersonLeftHandPositioning = c -> {GL11.glScalef(0f, 0f, 0f);};
			}
			
			if(firstPersonRightHandPositioning == null) {
			    firstPersonRightHandPositioning = c -> {GL11.glScalef(0f, 0f, 0f);};
            }
			
			return new StaticModelSourceRenderer(this);
		}

		public Consumer<ItemStack> getEntityPositioning() {
			return entityPositioning;
		}

		public Consumer<ItemStack> getInventoryPositioning() {
			return inventoryPositioning;
		}

		public BiConsumer<EntityPlayer, ItemStack> getThirdPersonPositioning() {
			return thirdPersonPositioning;
		}

		public BiConsumer<EntityPlayer, ItemStack> getFirstPersonPositioning() {
			return firstPersonPositioning;
		}

		public BiConsumer<ModelBase, ItemStack> getFirstPersonModelPositioning() {
			return firstPersonModelPositioning;
		}

		public BiConsumer<ModelBase, ItemStack> getThirdPersonModelPositioning() {
			return thirdPersonModelPositioning;
		}

		public BiConsumer<ModelBase, ItemStack> getInventoryModelPositioning() {
			return inventoryModelPositioning;
		}

		public BiConsumer<ModelBase, ItemStack> getEntityModelPositioning() {
			return entityModelPositioning;
		}
		
		public Consumer<RenderContext<RenderableState>> getFirstPersonLeftHandPositioning() {
            return firstPersonLeftHandPositioning;
        }
		
		public Consumer<RenderContext<RenderableState>> getFirstPersonRightHandPositioning() {
            return firstPersonRightHandPositioning;
        }

		public String getModId() {
			return modId;
		}
	}

	private StaticModelSourceRenderer(Builder builder) {
		super(builder);
	}

    @Override
    protected ModContext getModContext() {
        return builder.modContext;
    }
}
