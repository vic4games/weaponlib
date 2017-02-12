package com.vicmatskiv.weaponlib.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import com.vicmatskiv.weaponlib.state.Permit.Status;


/*
 * Use case:
 * 
 * 1) Target state is known
 * 
 * 2) Target state depends on the current state
 * 
 * Question: does each aspect need to be aware of all the transitons even outside of this aspect?
 * for example, can reload manager deal with reload-related states only?
 * 
 * No, because if we  want to capture last state transition timestamp.
 * 
 * Should this class be stateless? Where do we store last state transition timestamp?
 * With the state itself?
 */
public class StateManager {
	
	public class RuleBuilder<Context extends StateContext> {
		
		private Class<Context> contextClass;
		private ManagedState fromState;
		private ManagedState toState;
		private Action<Context> action;
		private Predicate<Context> predicate;
		private BiFunction<Context, ManagedState, Permit> permitProvider;
		private PermitManager<? super Context> permitManager;
			
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
		
		public RuleBuilder<Context> withPermit(BiFunction<Context, ManagedState, Permit> permitProvider,
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
			this.action = (context, from, to) -> { action.execute(context, from, to); return null;};
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
				action = (c, f, t) -> null;
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
	
				contextRules.add(new TransitionRule(contextClass, fromState, toState.permitRequested(), 
						(Predicate<Object>) predicate, 
						(c) -> permitProvider.apply(contextClass.cast(c), toState),
						(c, f, t) -> { permitManager.request(
								permitProvider.apply(contextClass.cast(c), toState), (Context) c, 
									(p1, c1) -> {StateManager.this.changeState(contextClass.cast(c1), toState);});
								return null;
						}));
				
				contextRules.add(new TransitionRule(contextClass, toState.permitRequested(), toState, 
						(c) -> permitProvider.apply(contextClass.cast(c), toState).getStatus() == Status.GRANTED,
						null,
						(c, f, t) -> action.execute(contextClass.cast(c), f, t)));
				
				contextRules.add(new TransitionRule(contextClass, toState.permitRequested(), fromState, 
						(c) -> {Status status = permitProvider.apply(contextClass.cast(c), toState).getStatus(); 
								return status == Status.DENIED || status == Status.TIMED_OUT;},
						null,
						(c, f, t) -> action.execute(contextClass.cast(c), f, t)));
				
			} else {
				contextRules.add(new TransitionRule(contextClass, fromState, toState,
						(Predicate<Object>) predicate, null,
						(c, f, t) -> action.execute(contextClass.cast(c), f, t)));
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
		public Object execute(Context stateContext, ManagedState fromState, ManagedState toState);
	}
	
	public static interface VoidAction<Context> {
		public void execute(Context stateContext, ManagedState fromState, ManagedState toState);
	}
	
	private static class TransitionRule {
		ManagedState fromState;
		ManagedState toState;
		Predicate<Object> predicate;
		Action<Object> action;
		Class<?> contextClass;
		
		TransitionRule(Class<?> contextClass, ManagedState fromState, ManagedState toState, 
				Predicate<Object> predicate, Function<Object, Permit> permitProvider, Action<Object> action) {
			this.contextClass = contextClass;
			this.fromState = fromState;
			this.toState = toState;
			this.predicate = predicate;
			this.action = action;
		}
		
		boolean matches(StateComparator stateComparator, Object context, ManagedState fromState, ManagedState...targetStates) {
			
			return stateComparator.compare(this.fromState, fromState) 
					&& (targetStates.length == 0 || Arrays.stream(targetStates).anyMatch(targetState -> stateComparator.compare(toState, targetState) 
						|| (targetState.permitRequested() != null 
							&& stateComparator.compare(toState, targetState.permitRequested()))))
					&& predicate.test(contextClass.cast(context));
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
	
	/**
	 * Changes state in the given context to any of the target states;
	 * 
	 * TODO: this method must be thread safe
	 * @param context
	 * @param targetStates
	 * @return
	 */
	public <Context extends StateContext> Result changeState(Context context, ManagedState...targetStates) {
		
		ManagedState currentState = context.getStateContainer().get();
		TransitionRule newStateRule = findNextStateRule(context, currentState, targetStates);
		Result result = null;
		if(newStateRule != null) {
			if(context.getStateContainer().compareAndSetState(currentState, newStateRule.toState)) {
				// Update state if the current state state did not change during this evaluation
				result = new Result(true, newStateRule.toState);
				if(newStateRule.action != null) {
					result.actionResult = newStateRule.action.execute(context, currentState, newStateRule.toState);
				}
			} else {
				// Transition rule was found, however the current state changed during this evaluation
			}
		} 
		
		if(result == null) {
			result = new Result(false, currentState);
		}
		
		return result;
	}


	private <Context> TransitionRule findNextStateRule(Context context, ManagedState currentState, ManagedState... targetStates) {
				
		return contextRules.entrySet().stream()
				.filter(e -> e.getKey().isAssignableFrom(context.getClass())) // check if a rule context class is a base class for the provided context class
				.map(e -> e.getValue()) // convert entry to a list of rules
				.flatMap(LinkedHashSet::stream) // merge the rule list
				.filter(rule -> rule.matches(stateComparator, context, currentState, targetStates)) // find matching rule
				.findFirst() // stop on the first found rule
				.orElse(null); // default to null if rule not found
	}
}
