package dungeon;

import org.w3c.dom.Node;

/**
 * Contains winning condition.
 *
 * @author Blind
 */
public class WinCondition
{
    public enum WinConditionType
    {
        KILL_MONSTER
    }

    public final WinConditionType type;
    public final String id;

    public WinCondition(WinConditionType type, String id)
    {
        this.type = type;
        this.id = id;
    }

    public static WinCondition parse(Node node)
    {
        WinConditionType type = WinConditionType.valueOf(node.getAttributes().getNamedItem("type").getNodeValue());
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        return new WinCondition(type, id);
    }
}
