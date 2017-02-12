package command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.newdawn.slick.command.Command;

public class MoveCommand implements Command
{
    private final int dx, dy;

    @JsonCreator
    public MoveCommand(@JsonProperty("dx") int dx, @JsonProperty("dy") int dy)
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
