package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RenderStateManager<State> {
	
	public interface Positioning {

		void apply(EntityPlayer player, ItemStack itemStack);
		
		boolean isExpired(Queue<Positioning> positioningQueue);
	}
	
	private class StaticPositioning implements Positioning {
		
		private State state;

		public StaticPositioning(State state) {
			this.state = state;
		}

		@Override
		public void apply(EntityPlayer player, ItemStack itemStack) {
			List<BiConsumer<EntityPlayer, ItemStack>> positioning = positioningManager.getPositioning(state);
			positioning.get(positioning.size() - 1).accept(player, itemStack);
		}
		
		@Override
		public boolean isExpired(Queue<Positioning> positioningQueue) {
			return !positioningQueue.isEmpty();
		}
	}
	
	private class TransitionedPositioning implements Positioning {
		
		private long startTime;
		private long endTime;
		private long duration;
		
//		private State fromState;
//		private State toState;
//		private Matrix4f beforeMatrix;
//		private Matrix4f afterMatrix;
		
		private List<Matrix4f> matrices;
		private int currentIndex;
		
		private int segmentCount;
		private float segmentLength;
		private float segmentDuration;
		
		private List<BiConsumer<EntityPlayer, ItemStack>> fromPositioning;
		private List<BiConsumer<EntityPlayer, ItemStack>> toPositioning;
		
		TransitionedPositioning(State fromState, State toState, long duration) {
//			this.fromState = fromState;
//			this.toState = toState;
			this.duration = duration;
			
			fromPositioning = positioningManager.getPositioning(fromState);
			toPositioning = positioningManager.getPositioning(toState);
			
			segmentCount = toPositioning.size();
			segmentLength = (float)duration / segmentCount;
			segmentDuration = duration / segmentCount;
			
			matrices = new ArrayList<>(toPositioning.size() + 1);
		}
		
		@Override
		public boolean isExpired(Queue<Positioning> positioningQueue) {
			if(startTime == 0) return false;
			return System.currentTimeMillis() > endTime;
		}
		
		@Override
		public void apply(EntityPlayer player, ItemStack itemStack) {
			
			if(startTime == 0) {
				startTime = System.currentTimeMillis();
				endTime = startTime + duration;
				
				matrices.add(getMatrixForPositioning(fromPositioning.get(fromPositioning.size() - 1), player, itemStack));
				for(BiConsumer<EntityPlayer, ItemStack> p: toPositioning) {
					matrices.add(getMatrixForPositioning(p, player, itemStack));
				}
			}
			
			long currentOffset = System.currentTimeMillis() - startTime;
			int index = (int) Math.floorDiv(segmentCount * currentOffset, duration);
			
			if(index >= segmentCount) {
				return;
			}
			
			if(index != currentIndex) {
//				System.out.println("Switch!");
				currentIndex = index;
			}
			

			
			float segmentOffset = currentOffset - index * segmentLength;
			float segmentProgress = (float)segmentOffset / (segmentDuration);
			
//			System.out.println("Offset: " + currentOffset + ", index: " + index 
//					+ ", offset: " + segmentOffset
//					+ ", progress: " + segmentProgress);
			
			applyOnce(player, itemStack, matrices.get(index), matrices.get(index + 1), segmentProgress);
		}

		private void applyOnce(EntityPlayer player, ItemStack itemStack, Matrix4f beforeMatrix, Matrix4f afterMatrix, float progress) {
			
			/*
			 * 
			 * progress = (endTime - startTime) / duration
			 * 
			 * current = start + (end - start) * progress = start * (1 - progress)  + end * progress;
			 */
			
			//float progress = (float)(System.currentTimeMillis() - startTime) / (float)duration;
			
			Matrix4f m1 = scale(beforeMatrix, 1 - progress); //start * (1 - progress)
			Matrix4f m2 = scale(afterMatrix, progress);
			
			Matrix4f deltaMatrix = Matrix4f.add(m1, m2, null);
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			deltaMatrix.store(buf);
			buf.rewind();
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadMatrix(buf);
		}
		
		private Matrix4f scale(Matrix4f m, float factor) {
			Matrix4f result = new Matrix4f();
			
			result.m00 = m.m00 * factor;
			result.m01 = m.m01 * factor;
			result.m02 = m.m02 * factor;
			result.m03 = m.m03 * factor;
			
			result.m10 = m.m10 * factor;
			result.m11 = m.m11 * factor;
			result.m12 = m.m12 * factor;
			result.m13 = m.m13 * factor;

			result.m20 = m.m20 * factor;
			result.m21 = m.m21 * factor;
			result.m22 = m.m22 * factor;
			result.m23 = m.m23 * factor;
			
			result.m30 = m.m30 * factor;
			result.m31 = m.m31 * factor;
			result.m32 = m.m32 * factor;
			result.m33 = m.m33 * factor;
			
			return result;
		}
		
		private Matrix4f getMatrixForPositioning(BiConsumer<EntityPlayer, ItemStack> positioning, EntityPlayer player, ItemStack itemStack) {
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			positioning.accept(player, itemStack);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
			buf.rewind();
			Matrix4f matrix = new Matrix4f();
			matrix.load(buf);  
			GL11.glPopMatrix();
			return matrix;
		}
		
//		private Matrix4f getMatrixForState(State state, EntityPlayer player, ItemStack itemStack) {
//			GL11.glPushMatrix();
//			GL11.glMatrixMode(GL11.GL_MODELVIEW);
//			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
//			positioningManager.getPositioning(state).accept(player, itemStack);
//			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
//			buf.rewind();
//			Matrix4f matrix = new Matrix4f();
//			matrix.load(buf);  
//			GL11.glPopMatrix();
//			return matrix;
//		}
	}
	
	private State currentState;
	
	private PositionProvider<State> positioningManager;
	
	private Deque<Positioning> positioningQueue;

	public RenderStateManager(State initialState, PositionProvider<State> positioningManager) {
		this.positioningManager = positioningManager;
		this.positioningQueue = new LinkedList<>();
		setState(initialState, 0);
	}
	
	public void setState(State newState, long animationDuration) {
		if(newState == null) {
			throw new IllegalArgumentException("State cannot be null");
		}
		
		if(newState.equals(currentState)) {
			return;
		}

		if(animationDuration > 0) {
			positioningQueue.add(new TransitionedPositioning(currentState, newState, animationDuration));
		}
		
		positioningQueue.add(new StaticPositioning(newState));
		currentState = newState;
	}
	
	private Positioning getPositioning() {
		Positioning result = null;
		while(!positioningQueue.isEmpty()) {
			Positioning p = positioningQueue.poll();
			if(!p.isExpired(positioningQueue)) {
				positioningQueue.addFirst(p); // add it back to the head of the queue
				result = p;
				break;
			}
		}
		if(result == null) {
			throw new IllegalStateException("Position cannot be null");
		}
		return result;
	}
	
	public BiConsumer<EntityPlayer, ItemStack> getPosition() {
		return (p, i) -> {
			getPositioning().apply(p, i);
		};
	}
	
}
