package com.jimholden.conomy.entity.rope;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.jimholden.conomy.entity.rope.RopeSimulation.Point;
import com.jimholden.conomy.entity.rope.RopeSimulation.Stick;
import com.jimholden.conomy.items.ItemRope;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

public class ChainedRopeSimulation {

	public ArrayList<RopeSimulation> sims = new ArrayList<>();

	public static class Link {
		public RopeSimulation a;
		public RopeSimulation b;

		public Link(RopeSimulation a, RopeSimulation b) {
			this.a = a;
			this.b = b;
		}
	}
	
	public void setupLink(RopeSimulation a, RopeSimulation b) {
		
	}
	
	public RopeSimulation addSimpleJointedLigament(Vec3d p) {
		
		RopeSimulation arm = new RopeSimulation();
		
		Point top = new Point(new Vec3d(p.x, p.y, p.z), false);
		Point middle = new Point(new Vec3d(p.x, p.y-0.3, p.z), false);
		Point bottom = new Point(new Vec3d(p.x, p.y-0.75, p.z), false);
		
		arm.connect(top, middle);
		arm.connect(middle, bottom);
		
		this.sims.add(arm);
		
		return arm;
	}

	public void simulate(double x, double y, double z, double dt) {
		for(RopeSimulation sim : this.sims) {
			sim.simulate(x, y, z, dt);
		}
	}

	public void basicDebugRender(double x, double y, double z) {

		for (RopeSimulation sim : this.sims) {
			GL11.glPushMatrix();
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.disableDepth();
			GL11.glTranslated(x, y, z);

			Minecraft.getMinecraft().getTextureManager().bindTexture(ItemRope.ROPE_RED);
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();

			bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
			for (Stick stick : sim.sticks) {
				Vec3d a = stick.pointA.pos;
				Vec3d b = stick.pointB.pos;
				bb.pos(a.x, a.y, a.z).endVertex();
				bb.pos(b.x, b.y, b.z).endVertex();
			}

			t.draw();


			GL11.glPopMatrix();
			GlStateManager.enableDepth();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
		}

	}


}
