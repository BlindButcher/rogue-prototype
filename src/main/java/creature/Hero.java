package creature;

import java.util.logging.Logger;

import main.LevelContext;
import core.Mechanics;

import static main.Delays.DEFAULT_DELAY;

public class Hero extends Creature
{
	private static final Logger MAIN = Logger.getLogger("MAIN");

	private int potions = 1;
	private int exp = 0;

	public Hero(CreatureTemplate template)
	{
		super(template);

		setRank(0);
		raiseRank();
	}

	public int getPotions()
	{
		return potions;
	}

	public void pickupPotion()
	{
		potions++;
		MAIN.fine(getName() + " picks up potion.");
	}

	public int drinkPotion()
	{
		if (potions > 0)
		{
			potions--;
			heal(Mechanics.DEFAULT_HEALTH / 2);
			return DEFAULT_DELAY.amount;
		}
		return 0;
	}

	@Override
	protected void onKill(Creature target)
	{
		super.onKill(target);
		addExp(target.getExpValue());
	}

	private void addExp(int exp)
	{
		MAIN.fine(getName() + " gets " + exp + " experience.");
		this.exp += exp;
		while (this.exp >= getRank() * getRank() * 5)
		{
			this.exp -= getRank() * getRank() * 5;
			raiseRank();
		}
	}

	public void enterLevel(LevelContext level)
	{
		this.level.getCreatures().remove(this);
		this.level = level;
		this.level.getCreatures().add(this);
	}
}
