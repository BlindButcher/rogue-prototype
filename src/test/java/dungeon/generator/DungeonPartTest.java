package dungeon.generator;

import dungeon.generator.DungeonPart;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Test DungeonPart class
 * 
 * @author Blind
 */
public class DungeonPartTest
{
	@Test
	public void testSplit()
	{
		DungeonPart part = new DungeonPart(new int[] { 13, 0, 23, 24 });
		part.split();
		Assert.assertEquals(2, part.children.size());
	}
}
