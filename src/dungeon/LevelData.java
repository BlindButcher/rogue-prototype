package dungeon;

import util.RandomList;

/**
 * Contains information about concrete level.
 * 
 * @author Blind
 */
class LevelData
{
	private int minCreature, maxCreature;
	private RandomList<String> randomList;

	LevelData(int minCreature, int maxCreature, RandomList<String> randomList)
	{
		this.minCreature = minCreature;
		this.maxCreature = maxCreature;
		this.randomList = randomList;
	}

	public int getMaxCreature()
	{
		return maxCreature;
	}

	public void setMaxCreature(int maxCreature)
	{
		this.maxCreature = maxCreature;
	}

	public int getMinCreature()
	{
		return minCreature;
	}

	public void setMinCreature(int minCreature)
	{
		this.minCreature = minCreature;
	}

	public RandomList<String> getRandomList()
	{
		return randomList;
	}

	public void setRandomList(RandomList<String> randomList)
	{
		this.randomList = randomList;
	}
}
