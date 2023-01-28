package com.jimholden.conomy.client.gui.engine.display;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Vector2d;

import com.jimholden.conomy.client.gui.engine.GuiPage;
import com.jimholden.conomy.client.gui.engine.buttons.IHasInfo;

import akka.japi.Pair;

public class AlignmentBuffer {
	
	private HashMap<FrameAlignment, AlignmentData> frameBuffers = new HashMap<>();

	private GuiPage reference;
	
	
	public AlignmentBuffer(GuiPage gp) {
		this.reference = gp;
	}
	
	
	private class AlignmentData {
		
		protected double tn;
		
		protected double height;
		protected double width;
		protected ArrayList<IHasInfo> bufferObjects = new ArrayList<>();
		protected FrameAlignment fa;
		protected AlignmentBuffer buffer;
		
		private AlignmentData(AlignmentBuffer buffer, FrameAlignment fa, double width, double height) {
			this.fa = fa;
			this.width = width;
			this.height = height;
			this.buffer = buffer;
		}
		
		protected void setWidth(double w) {
			this.width = w;
		}
		
		protected void setHeight(double h) {
			this.height = h;
		}
		
		protected double getHeight() {
			if(fa.isHorizontal()) return 0.0;
			return this.height;
		}
		
		protected double getWidth() {
			if(!fa.isHorizontal()) return 0.0;
			return this.width;
		}
		
		protected void pushWidth(IHasInfo ihi, double w) {
			this.bufferObjects.add(ihi);
			this.width += w;
		}
		
		protected void pushHeight(IHasInfo ihi, double h) {
			this.bufferObjects.add(ihi);
			this.height += h;
		}
		
		protected void push(IHasInfo ihi, double w, double h) {
				
			
			
			for(IHasInfo i : this.bufferObjects) {
				i.setX(-w+i.getX());
			}

			
			this.bufferObjects.add(ihi);

			
			this.width += w;
			this.height += h;
			
		}
		
		
		
	}
	
	
	
	public void pushBuffer(FrameAlignment align, IHasInfo ihi, double x, double y) {
		if(!frameBuffers.containsKey(align)) {
			frameBuffers.put(align, new AlignmentData(this, align, x, y));
		} else {
			frameBuffers.get(align).push(ihi, x, y);
			
		}
	}

	
	public AlignmentData getAlignmentData(FrameAlignment fa) {
		if(!frameBuffers.containsKey(fa)) {
			frameBuffers.put(fa, new AlignmentData(this, fa, 0, 0));
		}
		return frameBuffers.get(fa);
	}
	
	public Vector2d getBufferOffset(FrameAlignment fa) {
		AlignmentData ad = getAlignmentData(fa);
		return new Vector2d(ad.getWidth(), ad.getHeight());
	}
	
	public void clearBuffers() {
		frameBuffers.clear();
	}

}
