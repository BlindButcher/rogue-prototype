package creature;

public enum Attribute
{
	MELEE("MEL"),
	RANGED("RAN"),
	DEFENCE("DEF"),
	DAMAGE("DAM"),
	SPEED("SPD"),
	HEALTH("HEA"),
	ARMOR("ARM"),
    PENETRATION("PENETRATION");

	private final String id;

	private Attribute(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public static Attribute getById(String id)
	{
		if (id == null) return null;
		for (Attribute attr : Attribute.values())
		{
			if (id.equals(attr.getId())) return attr;
		}
		return null;
	}
}
