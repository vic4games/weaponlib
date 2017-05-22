package com.vicmatskiv.weaponlib.animation;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MultipartRenderStateManager<State, Part, Context extends PartPositionProvider> {

	private static final Logger logger = LogManager.getLogger(MultipartRenderStateManager.class);

	private Randomizer randomizer;

	private WeakHashMap<Part, Matrix4f> lastApplied = new WeakHashMap<>(); // TODO: replace with cache?

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
						MultipartTransition<Part, Context> multipartTransition = transitions.get(transitions.size() - 1);
						Part attachedTo = multipartTransition.getAttachedTo(part);
						if(attachedTo != null) {
						    MatrixHelper.loadMatrix(context.getPartPosition(attachedTo));
						}
						if(multipartTransition.getPositioning(part) == (Object)MultipartTransition.anchoredPosition()) {
						    Matrix4f m = lastApplied.get(part);
						    MatrixHelper.applyMatrix(m);
						} else {
						    multipartTransition.position(part, context);
						}

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
			Part attachedTo;
		}
		private Map<Part, PartData> partDataMap = new HashMap<>();

		private Long startTime;
		private long totalDuration;

		private int currentIndex;
		private long currentStartTime;
		private boolean expired;

		private int segmentCount;

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

		private Matrix4f adjustToAttached(Matrix4f matrix, Part fromAttached, Part toAttached, Context context) {

            if(fromAttached == toAttached) {
                return matrix;
            }

            Matrix4f fromMatrix = context.getPartPosition(fromAttached);
            if(fromMatrix == null) {
                return matrix;
            }

            Matrix4f toMatrix = context.getPartPosition(toAttached);
            if(toMatrix == null) {
                return matrix;
            }

            Matrix4f invertedToMatrix = Matrix4f.invert(toMatrix, null);
            if(invertedToMatrix == null) {
                return matrix;
            }

            Matrix4f correctionMatrix = Matrix4f.mul(invertedToMatrix, fromMatrix, null);
            return Matrix4f.mul(correctionMatrix, matrix, null);
        }

		private PartData getPartData(Part part, Context context) {
			try {
				return partDataMap.computeIfAbsent(part, p -> {
					PartData pd = new PartData();
					MultipartTransition<Part, Context> fromMultipart = fromPositioning.get(fromPositioning.size() - 1);

                    Matrix4f fromMatrix;
                    if(fromMultipart.getPositioning(part) == (Object)MultipartTransition.anchoredPosition()) {
                        fromMatrix = lastApplied.get(p);
                        if(fromMatrix == null) {
                            fromMatrix = new Matrix4f();
                            fromMatrix.setIdentity();
                        }
                    } else {
                        fromMatrix = getMatrixForPositioning(fromMultipart, p, context);
                    }

                    fromMatrix = adjustToAttached(fromMatrix, fromMultipart.getAttachedTo(p),
                            toPositioning.get(0).getAttachedTo(p), context);

                    pd.matrices.add(fromMatrix);
                    pd.attachedTo = toPositioning.get(0).getAttachedTo(p);

                    Matrix4f previous = fromMatrix;
					for(MultipartTransition<Part, Context> t: toPositioning) {
					    Matrix4f current;
					    if(t.getPositioning(part) == (Object)MultipartTransition.anchoredPosition()) {
					        current = previous;
					    } else {
					        current = getMatrixForPositioning(t, p, context);
					    }

					    pd.matrices.add(current);
					    previous = current;
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
				logger.debug("Starting transition {}, duration {}ms, pause {}ms", currentIndex, currentDuration, currentPause);
				startTime = currentTime;
			}

			if(currentStartTime == 0) {
				currentStartTime = currentTime;
			} else if(currentTime > currentStartTime + currentDuration + currentPause) {
				logger.debug("Completed transition {}, duration {}ms, pause {}ms", currentIndex, currentDuration, currentPause);
				currentIndex++;
				if(logger.isDebugEnabled() && currentIndex < toPositioning.size()) {
					MultipartTransition<Part, Context> multipartTransition = toPositioning.get(currentIndex);
					logger.debug("Starting transition {}, duration {}ms, pause {}ms", currentIndex,
							multipartTransition.getDuration(), multipartTransition.getPause());
				}
				currentStartTime = currentTime;
			}

			long currentOffset = currentTime - currentStartTime;

			float currentProgress = (float)currentOffset / currentDuration;

			if(currentProgress > 1f) {
				currentProgress = 1f;
			}

			float finalCurrentProgress = currentProgress;

			if(currentIndex >= segmentCount) {
				expired = true;
				return new Positioner<Part, Context>() {

					@Override
					public void position(Part part, Context context) {
						PartData partData = getPartData(part, context);
						applyOnce(part, context,
						        partData.matrices.get(currentIndex - 1),
						        partData.matrices.get(currentIndex),
						        partData.attachedTo,
						        1f);
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
					applyOnce(part, context,
					    partData.matrices.get(currentIndex),
						partData.matrices.get(currentIndex + 1),
						partData.attachedTo,
						finalCurrentProgress);
				}

				@Override
				public void randomize(float rate, float amplitude) {
					randomizer.update(0f, 0f);
				}
			};
		}

		private void applyOnce(Part part, Context context, Matrix4f beforeMatrix, Matrix4f afterMatrix,
		        Part attachedTo, float progress) {

		    logger.trace("Applying position for part {}", part);


			/*
			 *
			 * progress = (endTime - startTime) / duration
			 *
			 * current = start + (end - start) * progress = start * (1 - progress)  + end * progress;
			 */

			//float progress = (float)(System.currentTimeMillis() - startTime) / (float)duration;

		    Matrix4f currentMatrix = null;

		    if(attachedTo != null) {
		        currentMatrix = context.getPartPosition(attachedTo);
		    }

		    /*
		     * Otherwise capture current position
		     */
		    if(currentMatrix == null) {
				currentMatrix = MatrixHelper.captureMatrix();
			}

			Matrix4f m1 = MatrixHelper.interpolateMatrix(beforeMatrix, 1 - progress); //start * (1 - progress)
			Matrix4f m2 = MatrixHelper.interpolateMatrix(afterMatrix, progress);

			Matrix4f deltaMatrix = Matrix4f.add(m1, m2, null);

			lastApplied.put(part, deltaMatrix);

			Matrix4f composite = Matrix4f.mul(currentMatrix, deltaMatrix, null);

			MatrixHelper.loadMatrix(composite);
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
