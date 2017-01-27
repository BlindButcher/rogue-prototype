package dungeon;

import util.RandomList;

/**
 * Contains dungeon choice function.
 * 
 * @author Blind
 */
class Choice
{
	public final RandomList<String> mapIds;
	public final int rows, cols;
	public final boolean any;
	public final boolean first, last;

	public Choice(RandomList<String> mapIds, int rows, int cols, boolean first, boolean last)
	{
		this.mapIds = mapIds;
		this.any = mapIds.size() == 0;
		this.rows = rows;
		this.cols = cols;
		this.first = first;
		this.last = last;
	}
}
