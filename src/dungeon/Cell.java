package dungeon;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import view.IRenderable;

import com.google.common.base.Predicate;

public enum Cell implements IRenderable
{
	NULL(' ', false, "null.png"),
	BLANK('.', true, "blank.png"),
	WALL('#', false, "wall.png"),
	DOOR('D', false, "door.png"),
	OPENED_DOOR('_', true, "opened_door.png"),
	DOWN_STAIRS('>', true, "down_stairs.png"),
	UP_STAIRS('<', true, "up_stairs.png");

	final private char c;
	final private boolean passable;
	final String imagePath;

	private Cell(char c, boolean passable, String imagePath)
	{
		this.c = c;
		this.passable = passable;
		this.imagePath = "graphic/cell/" + imagePath;
	}

	public char getC()
	{
		return c;
	}

	public boolean isPassable()
	{
		return passable;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public String getId()
	{
		return this.name();
	}

	public static Cell fromChar(final char c)
	{
		return find(newArrayList(Cell.values()), new Predicate<Cell>()
		{
			public boolean apply(Cell cell)
			{
				return cell.getC() == c;
			}
		});
	}
}
