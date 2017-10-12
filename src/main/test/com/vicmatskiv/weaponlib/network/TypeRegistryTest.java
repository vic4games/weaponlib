//package com.vicmatskiv.weaponlib.network;
//
//import java.util.UUID;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//public class TypeRegistryTest {
//
//	@Test
//	public void test() {
//		class Test {};
//		
//		TypeRegistry typeRegistry = TypeRegistry.getInstance();
//		
//		UUID uuid1 = typeRegistry.createUuid(Test.class);
//		
//		UUID uuid2 = typeRegistry.createUuid(Test.class);
//		
//		Assert.assertEquals(uuid1, uuid2);
//		
//		class Test2 {};
//		
//		UUID uuid3 = typeRegistry.createUuid(Test2.class);
//		
//		UUID uuid4 = typeRegistry.createUuid(Test2.class);
//		
//		Assert.assertEquals(uuid3, uuid4);
//		
//		Assert.assertNotEquals(uuid1, uuid3);
//	}
//}
