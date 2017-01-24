package inventory.armor;

import org.w3c.dom.Node;

public class ArmorQuality
{
	public final String namePrefix;
    public final int armorBonus;
    public final double speedFactorBonus;
    public final int combatPenalty;

	private ArmorQuality(String namePrefix, int armorBonus, double speedFactorBonus, int combatPenalty)
	{
		this.namePrefix = namePrefix;
		this.armorBonus = armorBonus;
		this.speedFactorBonus = speedFactorBonus;
		this.combatPenalty = combatPenalty;
	}

    public static ArmorQuality fromNode(Node node)
    {
        String namePrefix = node.getAttributes().getNamedItem("namePrefix").getNodeValue();
        int  armorBonus = Integer.valueOf(node.getAttributes().getNamedItem("armorBonus").getNodeValue());
        double speedFactorBonus = Double.valueOf(node.getAttributes().getNamedItem("speedFactorBonus").getNodeValue());
        int  combatPenalty = Integer.valueOf(node.getAttributes().getNamedItem("combatPenalty").getNodeValue());

        return new ArmorQuality(namePrefix, armorBonus, speedFactorBonus, combatPenalty);
    }
}
