package command;

import org.newdawn.slick.command.Command;

public class MoveCommand implements Command
{
    private final int dx, dy;

    public MoveCommand(int dx, int dy)
    {
        super();
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx()
    {
        return dx;
    }

    public int getDy()
    {
        return dy;
    }

}
