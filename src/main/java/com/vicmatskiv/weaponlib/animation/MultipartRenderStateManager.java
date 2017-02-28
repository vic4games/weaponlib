package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MultipartRenderStateManager<State, Part, Context> {

	private Randomizer randomizer;
	
	private class StaticPositioning implements MultipartPositioning<Part, Context> {
		
		private State state;

		public StaticPositioning(State state) {
			this.state = state;
		}
		
		
		@Override
		public float getProgress() {
			return 1f;
		}
		
		@Override
		public boolean isExpired(Queue<MultipartPositioning<Part, Context>> positioningQueue) {
			return !positioningQueue.isEmpty();
		}

		@Override
		public Positioner<Part, Context> getPositioner() {
			List<MultipartTransition<Part, Context>> transitions = transitionProvider.getPositioning(state);
			return new Positioner<Part, Context>() {

				@Override
				public void position(Part part, Context context) {
					try {
						transitions.get(transitions.size() - 1).position(part, context);
					} catch(Exception e) {
						System.err.println("Failed to find static position for " + part + " in " + state);
						throw e;
					}
				}
				
				@Override
				public void randomize(float rate, float amplitude) {
					randomizer.update(rate, amplitude);
				}
			};
		}

		@Override
		public <T> T getFromState(Class<T> stateClass) {
			return stateClass.cast(state);
		}
		
		@Override
		public <T> T getToState(Class<T> stateClass) {
			return stateClass.cast(state);
		}
	}
	
	private class TransitionedPositioning implements MultipartPositioning<Part, Context> {
		
		private class PartData {
			List<Matrix4f> matrices = new ArrayList<>();
		}
		private Map<Part, PartData> partDataMap = new HashMap<>();
//		private Map<Part, List<Matrix4f>> partMatrices = new HashMap<>();
		
		private Long startTime;
		private long totalDuration;
		
		private int currentIndex;
		private long currentStartTime;
		private boolean expired;
		
		private int segmentCount;
		
		//private boolean expired;
		
		private List<MultipartTransition<Part, Context>> fromPositioning;
		private List<MultipartTransition<Part, Context>> toPositioning;
		
		private State fromState;
		private State toState;
		
		
		
		TransitionedPositioning(State fromState, State toState) {
			this.fromState = fromState;
			this.toState = toState;
			fromPositioning = transitionProvider.getPositioning(fromState);
			toPositioning = transitionProvider.getPositioning(toState);
			segmentCount = toPositioning.size();
			
			for(MultipartTransition<Part, Context> t : toPositioning) {
				totalDuration += t.getDuration() + t.getPause();
			}
		}
		
		@Override
		public float getProgress() {
			return startTime != null ? (float)(System.currentTimeMillis() - startTime) / totalDuration : 0f;
		}
		
		@Override
		public boolean isExpired(Queue<MultipartPositioning<Part, Context>> positioningQueue) {
			return expired;
		}

		@Override
		public <T> T getFromState(Class<T> stateClass) {
			return stateClass.cast(fromState);
		}
		
		@Override
		public <T> T getToState(Class<T> stateClass) {
			return stateClass.cast(toState);
		}

		private PartData getPartData(Part part, Context context) {
			try {
				return partDataMap.computeIfAbsent(part, p -> { 
					PartData pd = new PartData();
					pd.matrices.add(getMatrixForPositioning(fromPositioning.get(fromPositioning.size() - 1), p, context));
					for(MultipartTransition<Part, Context> t: toPositioning) {
						pd.matrices.add(getMatrixForPositioning(t, p, context));
					}
					return pd;
				});
			} catch(Exception e) {
				System.err.println("Failed to get data for part " + part + " for transition from [" + fromState + "] to [" + toState + "]");
				throw e;
			}
		}
		
		@Override
		public Positioner<Part, Context> getPositioner() {
			
			long currentTime = System.currentTimeMillis();
			MultipartTransition<Part, Context> targetState = toPositioning.get(currentIndex);
			
			long currentDuration = targetState.getDuration();
			long currentPause = targetState.getPause();
			
			if(currentIndex == 0 && startTime == null) {
				startTime = currentTime;
			}
			
			//boolean uglyFlag = false;
			if(currentStartTime == 0) {
				currentStartTime = currentTime;
			} else if(currentTime > currentStartTime + currentDuration + currentPause) {
				currentIndex++;
				currentStartTime = currentTime;
				//uglyFlag = true;
			}
			
			long currentOffset = currentTime - currentStartTime;
			
			float currentProgress = (float)currentOffset / currentDuration;
			
			if(currentProgress > 1f) {
				currentProgress = 1f;
			}
			
			float finalCurrentProgress = /*uglyFlag ? 1f : */ currentProgress;
			
			if(currentIndex >= segmentCount) {
				expired = true;
				return new Positioner<Part, Context>() {

					@Override
					public void position(Part part, Context context) {
						PartData partData = getPartData(part, context);
						applyOnce(part, context, partData.matrices.get(currentIndex - 1), partData.matrices.get(currentIndex), 1f);
					}
					
					@Override
					public void randomize(float rate, float amplitude) {
						randomizer.update(0f, 0f);
					}
				};
			}

			
			return new Positioner<Part, Context> () {
				@Override
				public void position(Part part, Context context) {
					PartData partData = getPartData(part, context);
					applyOnce(part, context, partData.matrices.get(currentIndex), 
						partData.matrices.get(currentIndex + 1), finalCurrentProgress);
				}
				
				@Override
				public void randomize(float rate, float amplitude) {
					randomizer.update(0f, 0f);
				}
			};
		}

		private void applyOnce(Part part, Context context, Matrix4f beforeMatrix, Matrix4f afterMatrix, float progress) {
			
			/*
			 * 
			 * progress = (endTime - startTime) / duration
			 * 
			 * current = start + (end - start) * progress = start * (1 - progress)  + end * progress;
			 */
			
			//float progress = (float)(System.currentTimeMillis() - startTime) / (float)duration;
			Matrix4f currentMatrix;
			{
				// Load current matrix
				FloatBuffer buf = BufferUtils.createFloatBuffer(16);
				GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
				buf.rewind();
				currentMatrix = new Matrix4f();
				currentMatrix.load(buf); 
			}

			Matrix4f m1 = scale(beforeMatrix, 1 - progress); //start * (1 - progress)
			Matrix4f m2 = scale(afterMatrix, progress);
			
			Matrix4f deltaMatrix = Matrix4f.add(m1, m2, null);
			
			Matrix4f composite = Matrix4f.mul(currentMatrix, deltaMatrix, null);
			
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);

			composite.store(buf);
			
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
		
		private Matrix4f getMatrixForPositioning(MultipartTransition<Part, Context> transition, Part part, Context context) {
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			FloatBuffer buf = BufferUtils.createFloatBuffer(16);
			transition.position(part, context);
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
			buf.rewind();
			Matrix4f matrix = new Matrix4f();
			matrix.load(buf);  
			GL11.glPopMatrix();
			return matrix;
		}

	}
	
	private State currentState;
	
	private MultipartTransitionProvider<State, Part, Context> transitionProvider;
	
	private Deque<MultipartPositioning<Part, Context>> positioningQueue;

	public MultipartRenderStateManager(State initialState, MultipartTransitionProvider<State, Part, Context> transitionProvider, Part mainPart) {
		this.transitionProvider = transitionProvider;
		this.positioningQueue = new LinkedList<>();
		this.randomizer = new Randomizer();
		setState(initialState, false, true);
	}
	
	public void setState(State newState, boolean animated, boolean immediate) {
		if(newState == null) {
			throw new IllegalArgumentException("State cannot be null");
		}
		
		if(newState.equals(currentState)) {
			return;
		}

		if(immediate) {
			positioningQueue.clear();
		}
		
		if(animated) {
			positioningQueue.add(new TransitionedPositioning(currentState, newState));
		}
		
		positioningQueue.add(new StaticPositioning(newState));
		currentState = newState;
	}
	
	public MultipartPositioning<Part, Context> nextPositioning() {
		MultipartPositioning<Part, Context> result = null;
		while(!positioningQueue.isEmpty()) {
			MultipartPositioning<Part, Context> p = positioningQueue.poll();
			if(!p.isExpired(positioningQueue)) { // TODO: this is rather a hack
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
}
