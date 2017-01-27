package creature;

import static org.junit.Assert.assertTrue;

import loader.UnifiedAttribute;
import main.AbstractInjectionTest;

import org.junit.Test;

import view.CreaturePresenter;

public class CreatureTest extends AbstractInjectionTest
{
	private static final int LEVELS_TO_GO = 100;

	@Test
	public void testAttributeGrowth()
	{
		CreatureTemplate dummy = getDummy();

        dummy.getUnifiedAttributes().remove(new UnifiedAttribute("CUSTOM", Attribute.ARMOR, 1, 50));
		dummy.getUnifiedAttributes().add(new UnifiedAttribute("CUSTOM", Attribute.ARMOR, 1, 50));
		CreaturePresenter creature = new CreaturePresenter(new Creature(dummy));

		int startVal = creature.creature().getAttribute(Attribute.ARMOR);

		for (int i = 0; i < LEVELS_TO_GO; i++)
		{
			creature.creature().raiseRank();
			System.out.println(String.format("Level: %s %s", i, creature.baseAttributes()));
		}

		int endVal = creature.creature().getAttribute(Attribute.ARMOR);

		assertTrue(startVal < endVal);
	}

	@Test
	public void testAttributeGrowthToLevel()
	{
		for (int r = 1; r <= 100; r++) {
		CreatureTemplate dummy = getDummy();
        dummy.getUnifiedAttributes().remove(new UnifiedAttribute("CUSTOM", Attribute.ARMOR, 1, 300));
        dummy.getUnifiedAttributes().add(new UnifiedAttribute("CUSTOM", Attribute.ARMOR, 1, 300));
		CreaturePresenter creature = new CreaturePresenter(new Creature(dummy));

		int startVal = creature.creature().getAttribute(Attribute.ARMOR);

		for (int i = 1; i <= 10; i++)
		{
			creature.creature().raiseRank();
		}

		int endVal = creature.creature().getAttribute(Attribute.ARMOR);

		assertTrue(startVal < endVal);
		System.out.println(String.format("armor = %s", creature.creature().getAttribute(Attribute.ARMOR)));
		}
	}
}
