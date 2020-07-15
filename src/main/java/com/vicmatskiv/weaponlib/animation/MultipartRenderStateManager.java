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
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;


public class MultipartRenderStateManager<State, Part, Context extends PartPositionProvider> {

	private static final Logger logger = LogManager.getLogger(MultipartRenderStateManager.class);

	Randomizer randomizer;
	
	private Supplier<Long> currentTimeProvider; // = System::currentTimeMillis;

	private Function<Context, Float> currentProgressProvider;

	WeakHashMap<Part, Matrix4f> lastApplied = new WeakHashMap<>(); // TODO: replace with cache?

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
		private boolean fromAnchored;

		TransitionedPositioning(State fromState, State toState, boolean fromAnchored) {
			this.fromState = fromState;
			this.toState = toState;
			this.fromAnchored = fromAnchored;
			fromPositioning = transitionProvider.getTransitions(fromState);
			toPositioning = transitionProvider.getTransitions(toState);
			segmentCount = toPositioning.size();

			for(MultipartTransition<Part, Context> t : toPositioning) {
				totalDuration += t.getDuration() + t.getPause();
			}
		}

		@Override
		public float getProgress() {
			return startTime != null ? (float)(currentTimeProvider.get() - startTime) / totalDuration : 0f;
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
					Matrix4f fromMatrix = null;
					if(fromAnchored) {
					    fromMatrix = lastApplied.get(p);
					}
					
					if(fromMatrix == null && fromPositioning != null) {
					    MultipartTransition<Part, Context> fromMultipart = fromPositioning.get(fromPositioning.size() - 1);

	                    if(fromMultipart.getPositioning(part) == (Object)MultipartTransition.anchoredPosition()) {
	                        fromMatrix = lastApplied.get(p);
	                        if(fromMatrix == null) {
	                            fromMatrix = new Matrix4f();
	                            fromMatrix.setIdentity();
	                        }
	                    } else {
	                        logger.trace("Getting part data for {}", part);
	                        fromMatrix = getMatrixForPositioning(fromMultipart, p, context);
	                    }

	                    fromMatrix = adjustToAttached(fromMatrix, fromMultipart.getAttachedTo(p),
	                            toPositioning.get(0).getAttachedTo(p), context);
					} 
					
					if(fromMatrix == null){
					    fromMatrix = new Matrix4f();
					    fromMatrix.setIdentity();
					}
					
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

			long currentTime = currentTimeProvider.get();
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

	private static class StateContainer<State> {
	    State state;
	    boolean isEndOfLoop;
	    
        StateContainer(State state, boolean isEndOfLoop) {
            this.state = state;
            this.isEndOfLoop = isEndOfLoop;
        }
        
        StateContainer(State state) {
            this(state, false);
        } 
	}
	
	private String name;
	
	private StateContainer<State> currentStateContainer;

	MultipartTransitionProvider<State, Part, Context> transitionProvider;

	private Deque<MultipartPositioning<Part, Context>> positioningQueue;
	
	public MultipartRenderStateManager(State initialState, MultipartTransitionProvider<State, Part, Context> transitionProvider) {
        this(initialState, transitionProvider, System::currentTimeMillis);
    }

	public MultipartRenderStateManager(State initialState, MultipartTransitionProvider<State, Part, Context> transitionProvider,
	        Supplier<Long> currentTimeProvider) {
//		this.transitionProvider = transitionProvider;
//		this.positioningQueue = new LinkedList<>();
//		this.randomizer = new Randomizer();
//		this.currentTimeProvider = currentTimeProvider;
//		setState(initialState, false, true);
	    this("anonymous", initialState, transitionProvider, currentTimeProvider, null);
	}
	
	public MultipartRenderStateManager(String name, State initialState, MultipartTransitionProvider<State, Part, Context> transitionProvider,
	        Supplier<Long> currentTimeProvider, Function<Context, Float> currentProgressProvider) {
	    this.name = name;
	    this.transitionProvider = transitionProvider;
	    this.positioningQueue = new LinkedList<>();
	    this.randomizer = new Randomizer();
	    this.currentTimeProvider = currentTimeProvider;
	    this.currentProgressProvider = currentProgressProvider;
	    setState(initialState, false, true);
	}

	public void setCycleState(State cycleState, /*, State endState, */ boolean immediate) {
	    if(cycleState == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        if(immediate) {
            positioningQueue.clear();
        }

        StateContainer<State> addedState;
        if(positioningQueue.size() <= 1) {
            /*
             * If the currentState is a start state, add the virtual end-of-loop state.
             * No transitions required
             */
            if(cycleState.equals(currentStateContainer.state) && !currentStateContainer.isEndOfLoop) {
                addedState = new StateContainer<>(cycleState, true);
            } else { 
                /*
                 * if the currentState is anything other than start (e.g. null or any non-cycle state), 
                 * then starting the cycle
                 */
                addedState = new StateContainer<>(cycleState, false);
                
                positioningQueue.add(new TransitionedPositioning(currentStateContainer.state, addedState.state, false));
                positioningQueue.add(new StaticPositioning<State, Part, Context>(transitionProvider, randomizer, addedState.state, lastApplied));
            }

            currentStateContainer = addedState; //new StateContainer<>(addedState.state);
        }
	}
	
	public void setState(State newState, boolean animated, boolean immediate) {
		setState(newState, animated, immediate, false);
	}
	
	public void setState(State newState, boolean animated, boolean immediate, boolean fromAnchored) {
        if(newState == null) {
            throw new IllegalArgumentException("State cannot be null");
        }

        if(currentStateContainer != null && newState.equals(currentStateContainer.state)) {
            return;
        }

        if(immediate) {
            positioningQueue.clear();
        }

        if(animated) {
            positioningQueue.add(new TransitionedPositioning(currentStateContainer != null ?
                    currentStateContainer.state : null, newState, fromAnchored));
        }

        positioningQueue.add(new StaticPositioning<State, Part, Context>(transitionProvider, randomizer, newState, lastApplied));
        currentStateContainer = new StateContainer<>(newState);
    }
	
	public void setContinousState(State newState, boolean animated, boolean immediate, boolean fromAnchored) {
	    if(newState == null) {
	        throw new IllegalArgumentException("State cannot be null");
	    }

	    if(currentStateContainer != null && newState.equals(currentStateContainer.state)) {
	        return;
	    }

	    if(immediate) {
	        positioningQueue.clear();
	    }

	    if(animated) {
	        positioningQueue.add(new ContinousPositioning2<State, Part, Context>(transitionProvider, 
	                currentProgressProvider, randomizer, currentStateContainer != null ?
	                        currentStateContainer.state : null, newState, fromAnchored, lastApplied));
	    }

	    positioningQueue.add(new StaticPositioning<State, Part, Context>(transitionProvider, randomizer, newState, lastApplied));
	    currentStateContainer = new StateContainer<>(newState);
	}
	   
	public State getLastState() {
	    return currentStateContainer != null ? currentStateContainer.state : null; 
	}
	
	public MultipartPositioning<Part, Context> nextPositioning() {
		MultipartPositioning<Part, Context> result = null;
		while(!positioningQueue.isEmpty()) {
			MultipartPositioning<Part, Context> p = positioningQueue.poll();
			if(!p.isExpired(positioningQueue)) { // TODO: this is rather a hack
		        //logger.trace("Fetched next positioning from {} to {}", p.getFromState(Object.class), p.getToState(Object.class));
				positioningQueue.addFirst(p); // add it back to the head of the queue
				result = p;
				break;
			} else {
			    //logger.trace("Fetched next expired positioning from {} to {}", p.getFromState(Object.class), p.getToState(Object.class));
			}
		}
		if(result == null) {
			throw new IllegalStateException("Position cannot be null");
		}
		return result;
	}
}
