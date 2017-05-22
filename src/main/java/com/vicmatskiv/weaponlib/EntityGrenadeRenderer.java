package com.vicmatskiv.weaponlib;

import org.lwjgl.opengl.GL11;

import com.vicmatskiv.weaponlib.compatibility.CompatibleEntityRenderer;
import com.vicmatskiv.weaponlib.model.CameraModel;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityGrenadeRenderer extends CompatibleEntityRenderer {

    private ModelBase model = new CameraModel();
    private ResourceLocation textureLocation;
    private static final String TEXTURE = "weaponlib:/com/vicmatskiv/weaponlib/resources/gunmetaltexture.png";

    public EntityGrenadeRenderer() {
        textureLocation = new ResourceLocation(TEXTURE);
    }

    @Override
    public void doCompatibleRender(Entity entity, double x, double y, double z, float yaw, float tick) {

        if(model != null) {
            EntityBounceable entityGrenade = ( EntityBounceable) entity;
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0f, 0f);
            if(textureLocation != null) {
                bindTexture(textureLocation);
            }
            GL11.glTranslated(x, y, z);

            float rotationOffsetX = 0.13f;
            float rotationOffsetY = 0.12f;
            float rotationOffsetZ = 0.13f;
            GL11.glTranslatef(rotationOffsetX, rotationOffsetY, rotationOffsetZ);
            GL11.glRotatef(entityGrenade.getXRotation(), 1f, 0f, 0f);
            GL11.glRotatef(entityGrenade.getYRotation(), 0f, 1f, 0f);
            GL11.glTranslatef(-rotationOffsetX, -rotationOffsetY, -rotationOffsetZ);

            //GL11.glTranslated(0.0f, 0f, offset * Math.sin(entityGrenade.getXRotation() / 180 * Math.PI));


            //GL11.glRotatef(entityGrenade.getZRotation(), 0f, 0f, 1f);
            model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }

    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return textureLocation;
    }
}
