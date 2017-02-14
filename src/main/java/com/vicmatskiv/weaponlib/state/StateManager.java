package com.vicmatskiv.weaponlib.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Permit.Status;

public class StateManager {
	
	public class RuleBuilder<Context extends StateContext> {
		
		private static final long DEFAULT_REQUEST_TIMEOUT = 5000L;
		
		private Class<Context> contextClass;
		private ManagedState fromState;
		private ManagedState toState;
		private Action<Context> action;
		private Predicate<Context> predicate;
		private BiFunction<ManagedState, Context, Permit> permitProvider;
		//private PermitProvider<Context> permitProvider;
		private PermitManager<? super Context> permitManager;
		private long requestTimeout = DEFAULT_REQUEST_TIMEOUT;
			
		public RuleBuilder(Class<Context> contextClass) {
			this.contextClass = contextClass;
		}
		
		public RuleBuilder<Context> change(ManagedState fromState) {
			this.fromState = fromState;
			return this;
		}

		public RuleBuilder<Context> to(ManagedState state) {
			this.toState = state;
			return this;
		}
		
		public RuleBuilder<Context> when(Predicate<Context> predicate) {
			this.predicate = predicate;
			return this;
		}
		
		public RuleBuilder<Context> withPermit(
				BiFunction<ManagedState, Context, Permit> permitProvider,
				PermitManager<? super Context> permitManager) {
			this.permitProvider = permitProvider;
			this.permitManager = permitManager;
			return this;
		}
		
		public RuleBuilder<Context> withAction(Action<Context> action) {
			this.action = action;
			return this;
		}
		
		public RuleBuilder<Context> withAction(VoidAction<Context> action) {
			this.action = (context, from, to, permit) -> { action.execute(context, from, to, permit); return null;};
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public StateManager allowed() {
			
			LinkedHashSet<TransitionRule> contextRules = StateManager.this.contextRules.computeIfAbsent(
					contextClass, (c) -> new LinkedHashSet<>());
			
			if(predicate == null) {
				predicate = c -> true;
			}
			
			if(action == null) {
				action = (c, f, t, p) -> null;
			}
			
			if(permitProvider != null) {
				
				/*
				 *  Break state transition into 2 phases: 
				 *  
				 *  1) fromState -> toState.permitRequested 
				 *	2) toState.permitRequested -> toState (if permit granted)
				 *    or 
				 *     toState.permitRequested -> fromState (if permit denied)
				 */
	
				contextRules.add(new TransitionRule(fromState, toState.permitRequested(), 
						(c, p) -> predicate.test(contextClass.cast(c)), 
						(c, f, t, p) -> { permitManager.request(
								permitProvider.apply(toState, contextClass.cast(c)), (Context) c, 
									(p1, c1) -> {
										System.out.println("Applying permit with status " + p1.getStatus()
											+ ", trying to change state _ to " + toState);
										StateManager.this.changeStateWithPermit(contextClass.cast(c1), p1, toState.permitRequested(), toState);
									});
								return null;
						}));
				
				contextRules.add(new TransitionRule(toState.permitRequested(), toState, 
						(c, p) -> p != null? p.getStatus() == Status.GRANTED : false,
						(c, f, t, p) -> action.execute(contextClass.cast(c), f, t, p)));
				
				BiPredicate<Object, Permit> permitDenied = (c, p) -> p != null ? p.getStatus() == Status.DENIED : false;

				BiPredicate<Object, Permit> requestTimedout = (c, p) -> 
						System.currentTimeMillis() > contextClass.cast(c).getStateContainer().getLastManagedStateUpdateTimestamp() + requestTimeout;
				
				contextRules.add(new TransitionRule(toState.permitRequested(), fromState, 
						permitDenied.or(requestTimedout),
						(c, f, t, p) -> action.execute(contextClass.cast(c), f, t, p)));
				
			} else {
				contextRules.add(new TransitionRule(fromState, toState,
						(c, p) -> predicate.test(contextClass.cast(c)), (c, f, t, p) -> action.execute(contextClass.cast(c), f, t, p)));
			}
			
			return StateManager.this;
		}
	}

	
	public static interface StateComparator {
		public boolean compare(ManagedState state1, ManagedState state2);
	}
	
	public class Result {
		private boolean stateChanged;
		private ManagedState state;
		protected Object actionResult;

		private Result(boolean stateChanged, ManagedState targetState) {
			this.stateChanged = stateChanged;
			this.state = targetState;
		}

		public boolean isStateChanged() {
			return stateChanged;
		}

		public ManagedState getState() {
			return state;
		}
		
		public Object getActionResult() {
			return actionResult;
		}
	}
	
	public static interface Action<Context> {
		public Object execute(Context stateContext, ManagedState fromState, ManagedState toState, Permit permit);
	}
	
	public static interface VoidAction<Context> {
		public void execute(Context stateContext, ManagedState fromState, ManagedState toState, Permit permit);
	}
	
	private static class TransitionRule {
		ManagedState fromState;
		ManagedState toState;
		BiPredicate<Object, Permit> predicate;
		Action<Object> action;
		
		TransitionRule(
				ManagedState fromState, 
				ManagedState toState, 
				BiPredicate<Object, Permit> predicate, 
				Action<Object> action) {
			if(fromState == null) {
				throw new IllegalArgumentException("From-state cannot be null");
			}
			if(toState == null) {
				throw new IllegalArgumentException("To-state cannot be null");
			}
			this.fromState = fromState;
			this.toState = toState;
			this.predicate = predicate;
			this.action = action;
		}
		
		boolean matches(StateComparator stateComparator, Object context, Permit permit, ManagedState fromState, ManagedState...targetStates) {
			
			return stateComparator.compare(this.fromState, fromState) 
					&& (targetStates.length == 0 || Arrays.stream(targetStates).anyMatch(targetState -> stateComparator.compare(toState, targetState) 
						|| (targetState.permitRequested() != null 
							&& stateComparator.compare(toState, targetState.permitRequested()))))
					&& predicate.test(context, permit);
		}
	}
	
	private StateComparator stateComparator;
	private Map<Class<?>, LinkedHashSet<TransitionRule>> contextRules = new HashMap<>();
	
	public StateManager(StateComparator stateComparator) {
		this.stateComparator = stateComparator;
	}
	
	public <T extends StateContext> RuleBuilder<T> in(Class<T> contextClass) {
		return new RuleBuilder<T>(contextClass);
	}
	
	public <Context extends StateContext> Result changeState(Context context, ManagedState...targetStates) {
		ManagedState currentState = context.getStateContainer().getManagedState();
		return changeStateWithPermit(context, null, currentState, targetStates);
	}
	
	protected <Context extends StateContext> Result changeStateWithPermit(Context context, 
			Permit permit, ManagedState fromState, ManagedState...targetStates) {
		
		//System.out.println("Current state: " + currentState);
		// If current state matches any of the target states, return immediately
		if(Arrays.stream(targetStates).anyMatch(target -> stateComparator.compare(fromState, target))) {
			return new Result(false, fromState);
		}
		TransitionRule newStateRule = findNextStateRule(context, permit, fromState, targetStates);
		Result result = null;
		if(newStateRule != null) {
			if(context.getStateContainer().compareAndSetManagedState(fromState, newStateRule.toState)) {
				// Update state if the current state state did not change during this evaluation
				System.out.println("State changed from " + fromState + " to "+ newStateRule.toState);
				result = new Result(true, newStateRule.toState);
				if(newStateRule.action != null) {
					result.actionResult = newStateRule.action.execute(context, fromState, newStateRule.toState, permit);
				}
			}
		} 
		
		if(result == null) {
			result = new Result(false, fromState);
		}
		
		return result;
	}


	private <Context> TransitionRule findNextStateRule(Context context, Permit permit, ManagedState currentState, ManagedState... targetStates) {
				
		return contextRules.entrySet().stream()
				.filter(e -> e.getKey().isAssignableFrom(context.getClass())) // check if a rule context class is a base class for the provided context class
				.map(e -> e.getValue()) // convert entry to a list of rules
				.flatMap(LinkedHashSet::stream) // merge the rule list
				.filter(rule -> rule.matches(stateComparator, context, permit, currentState, targetStates)) // find matching rule
				.findFirst() // stop on the first found rule
				.orElse(null); // default to null if rule not found
	}
}
