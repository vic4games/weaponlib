//package com.vicmatskiv.weaponlib.state;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.BiConsumer;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.vicmatskiv.weaponlib.network.TypeRegistry;
//
//import io.netty.buffer.ByteBuf;
//
//public class StateManagerTest {
//	
//	private static enum MyState implements ManagedState<MyState> {
//		STATE1, STATE2_REQUESTED, STATE2(STATE2_REQUESTED), STATE3;
//
//		private MyState permitRequested;
//		
//		private MyState() {
//			this(null);
//		}
//		
//		private MyState(MyState permitRequested) {
//			this.permitRequested = permitRequested;
//		}
//		
//		@Override
//		public MyState permitRequested() {
//			return permitRequested;
//		}
//
//		@Override
//		public void init(ByteBuf buf) {
//		}
//
//		@Override
//		public void serialize(ByteBuf buf) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		
//	};
//	
//	private static class MyStateContainer implements ExtendedState<MyState> {
//		
//		private AtomicReference<MyState> ref;
//		
//		MyStateContainer(MyState state) {
//			this.ref = new AtomicReference<>(state);
//		}
//
//		@Override
//		public boolean setState(MyState updateToState) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public MyState getState() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getStateUpdateTimestamp() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		
//		
//	}
//	
//	private static class MyStateContext implements StateContext<MyState> {
//		
//		private MyState state;
//		public boolean flag;
//		
//		public MyStateContext() {}
//
//		public MyStateContext(MyState state) {
//			this.state = state;
//		}
//
//		@Override
//		public boolean setState(MyState state) {
//			this.state = state;
//			return true;
//		}
//
//		@Override
//		public MyState getState() {
//			return state;
//		}
//
//		@Override
//		public long getStateUpdateTimestamp() {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//		
//	}
//	
//
//	@Test
//	public void testAlways() {
//		
//		MyStateContext context = new MyStateContext(MyState.STATE1);
//		StateManager<MyState> stateManager = new StateManager<MyState>((s1, s2) -> s1 == s2)
//				.in(MyStateContext.class)
//				.change(MyState.STATE1).to(MyState.STATE2).allowed();
//		
//		StateManager<MyState>.Result result;
//		result = stateManager.changeState(context, MyState.STATE2);
//		Assert.assertTrue(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE2, result.getState());
//	}
//	
//	static class MyNetworkPermit extends Permit<MyState> {
//
//		public MyNetworkPermit(MyState state) {
//			super(state);
//		}
//		
//		@Override
//		public Status getStatus() {
//			return Status.GRANTED;
//		}
//
//	}
//	
//	@Test
//	public void testAlwaysWithPermit() {
//		
//		List<BiConsumer<Permit<MyState>, MyStateContext>> callbacks = new ArrayList<>();
//
//		PermitManager permitManager = new PermitManager() {
//
//			@Override
//			public <T extends Permit<MyState>, Context> void request(T permit, Context context,
//					BiConsumer<Permit<MyState>, MyStateContext> callback) {
//				// TODO Auto-generated method stub
//				System.out.println("Requesting permit");
//				callbacks.add(callback);
//				
//				
//			}
//
//			@Override
//			public <T extends Permit<MyState, Context>, Context> void registerEvaluator(Class<T> permitClass,
//					BiConsumer<T, MyStateContext> evaluator) {
//				System.out.println("Registering permit evaluator for " + permitClass);
//			}
//		};
//		
//		MyNetworkPermit permit = new MyNetworkPermit(MyState.STATE2);
//		
//		StateManager<MyState> stateManager = new StateManager<>((s1, s2) -> s1 == s2);
//		stateManager
//				.in(MyStateContext.class)
//				.change(MyState.STATE1).to(MyState.STATE2)
//				.withPermit((s, c) -> permit, permitManager)
//				.allowed();
//		
//		MyStateContext context = new MyStateContext(MyState.STATE1);
//		
//		StateManager<MyState>.Result result;
//		result = stateManager.changeState(context, MyState.STATE2);
//		Assert.assertTrue(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE2.permitRequested(), result.getState());
//		
//		callbacks.get(0).accept(permit, context);
//		result = stateManager.changeState(context);
//
//		Assert.assertEquals(MyState.STATE2, result.getState());
//	}
//	
//	@Test
//	public void testAlwaysWithAction() {
//		
//		
//		final String actionResult = "result";
//		StateManager<MyState> stateManager = new StateManager<MyState>(
//				(s1, s2) -> s1 == s2)
//				.in(MyStateContext.class)
//				.change(MyState.STATE1).to(MyState.STATE2).withAction((context, from, to, permit) -> actionResult).allowed();
//		
//		MyStateContext context = new MyStateContext(MyState.STATE1);		
//		StateManager<MyState>.Result result;
//		result = stateManager.changeState(context, MyState.STATE2);
//		Assert.assertTrue(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE2, result.getState());
//		Assert.assertEquals(actionResult, result.getActionResult());
//	}
//	
//	@Test
//	public void testNoTransitionDefined() {
//		MyStateContext context = new MyStateContext(MyState.STATE1);		
//		StateManager<MyState> stateManager = new StateManager<MyState>(
//				(s1, s2) -> s1 == s2)
//				.in(MyStateContext.class)
//				.change(MyState.STATE1).to(MyState.STATE2).allowed();
//		
//		StateManager<MyState>.Result result;
//		result = stateManager.changeState(context, MyState.STATE3);
//		Assert.assertFalse(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE1, result.getState());
//	}
//	
//	@Test
//	public void testConditionalTransition() {
//		
//		StateManager<MyState> stateManager = new StateManager<MyState>(
//				(s1, s2) -> s1 == s2)
//				.in(MyStateContext.class)
//				.change(MyState.STATE1).to(MyState.STATE3).when((context) -> context.flag).allowed();
//		
//		StateManager<MyState>.Result result;
//		MyStateContext context = new MyStateContext(MyState.STATE1);		
//		// Try change state without condition evaluated to true
//		result = stateManager.changeState(context, MyState.STATE3);
//		Assert.assertFalse(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE1, result.getState());
//		
//		// Now change state with condition evaluated to true
//		context.flag = true;
//		result = stateManager.changeState(context, MyState.STATE3);
//		Assert.assertTrue(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE3, result.getState());
//	}
//	
//	@Test
//	public void testMultipleTransitions() {
//		
//		String actionResult = "result";
//		StateManager<MyState> stateManager = new StateManager<MyState>((s1, s2) -> s1 == s2)
//				.in(MyStateContext.class)
//				
//				.change(MyState.STATE1).to(MyState.STATE2).allowed()
//				
//				.in(MyStateContext.class)
//				.change(MyState.STATE2).to(MyState.STATE3)
//					.withAction((context, from, to, permit) -> actionResult)
//					.when((context) -> context.flag)
//				.allowed();
//		
//		StateManager<MyState>.Result result;
//		MyStateContext context = new MyStateContext(MyState.STATE1);		
//		result = stateManager.changeState(context, MyState.STATE2);
//		Assert.assertEquals(MyState.STATE2, result.getState());
//		
//		context.flag = true;
//		result = stateManager.changeState(context, MyState.STATE3);
//		Assert.assertTrue(result.isStateChanged());
//		Assert.assertEquals(MyState.STATE3, result.getState());
//		
//		Assert.assertEquals(actionResult, result.getActionResult());
//	}
//}
