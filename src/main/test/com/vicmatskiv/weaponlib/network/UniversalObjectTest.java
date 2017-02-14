package com.vicmatskiv.weaponlib.network;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class UniversalObjectTest {

	@Test
	public void test() {
		UniversalObject o1 = new UniversalObject() {};
		
		UUID uuid1 = o1.createUuid();
		
		UUID uuid2 = o1.createUuid();
		
		Assert.assertEquals(uuid1, uuid2);
		
		UniversalObject o2 = new UniversalObject() {};
		
		UUID uuid3 = o2.createUuid();
		
		UUID uuid4 = o2.createUuid();
		
		Assert.assertEquals(uuid3, uuid4);
		
		Assert.assertNotEquals(uuid1, uuid3);
	}
}
