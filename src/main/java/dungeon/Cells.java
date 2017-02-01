package dungeon;

import com.google.common.base.Preconditions;
import util.Point;
import util.Utils;

public class Cells
{
	private Cell[][] cells;
	private boolean[][] aware;
    private String id;

	public Cells(Cell[][] cells, String id)
	{
		this.cells = cells;
        this.id = id;
		aware = new boolean[cells.length][cells[0].length];
        validate();
	}

    private void validate()
    {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[0].length; j++)
                if (cells[i][j] == null || cells[i][j] == Cell.NULL)
                    throw new IllegalArgumentException("Map should not have NULL cells.");

        Preconditions.checkArgument(DungeonUtils.hasPassableCells(cells));
    }

    public int getRows()
	{
		return cells.length;
	}

	public int getCols()
	{
		return cells[0].length;
	}

	public Cell[][] getCells()
	{
		return cells;
	}

	public void setAware(int row, int col)
	{
		if (row < 0 || row >= cells.length) return;
		if (col < 0 || col >= cells[0].length) return;
		aware[row][col] = true;
	}

	public boolean isAware(int row, int col)
	{
		if (row < 0 || row >= cells.length) return false;
		if (col < 0 || col >= cells[0].length) return false;
		return aware[row][col];
	}

	public Cell getCell(int row, int col)
	{
		if (row < 0 || row >= cells.length) return Cell.NULL;
		if (col < 0 || col >= cells[0].length) return Cell.NULL;
		return cells[row][col];
	}

    public Cell getCell(Point point)
    {
        return getCell(point.x, point.y);
    }

	public void setCell(int row, int col, Cell cell)
	{
		if (row < 0 || row >= cells.length) return;
		if (col < 0 || col >= cells[0].length) return;
		cells[row][col] = cell;
	}

    public int[] getRandomPassableCell()
    {
        int x, y;
        do {
            x = Utils.roll(getRows());
            y = Utils.roll(getCols());
        } while (!getCell(x, y).isPassable());
        return new int[]{x, y};
    }

    public boolean hasCellType(Cell cellType) {
        int rows = getRows();
        int cols = getCols();
        boolean found = false;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (getCell(i, j) == cellType) {
                    found = true;
                    break;
                }
        return found;
    }

    public void remove(Cell type)
    {
        for (int i = 0; i < cells.length; i++)
            for (int j = 0; j < cells[0].length; j++)
                if (cells[i][j] == null || cells[i][j] == type)
                    setCell(i, j, Cell.BLANK);
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getRows(); i++) {
            sb.append("\r\n");
            for (int j = 0; j < getCols(); j++) {
                sb.append(getCell(i, j).getC());
            }
        }

        return sb.toString();
    }
}
