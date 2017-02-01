package inventory;

import core.Mechanics;
import inventory.shield.IShieldAccessor;
import inventory.shield.IShieldEnhance;
import inventory.shield.ShieldBase;
import util.INamedEntity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.FIELD)
public class Material implements IShieldEnhance, INamedEntity
{
    public static final Material NATURAL = new Material("Hands", "", MaterialType.NATURAL, 1);

	public final static int MAX_TIER = 4;

    @XmlAttribute(name = "name")
	private String name;
    @XmlAttribute(name = "id")
    private String id;
    @XmlIDREF
    @XmlAttribute(name = "type")
	private MaterialType materialType;
    @XmlAttribute(name = "tier")
	private int tier;

    public Material() { }

    public Material(String id, String name, MaterialType materialType, int tier)
	{
		this.name = name;
		this.materialType = materialType;
		this.tier = tier;
	}

    public String getId()
    {
        return id;
    }

    public String getName()
	{
		return name;
	}

	public MaterialType getMaterialType()
	{
		return materialType;
	}

	public int getTier()
	{
		return tier;
	}

	@Override
	public void apply(IShieldAccessor shield, int tier)
	{
		if (!fits(shield.getBase())) throw new IllegalStateException();
		if (tier != this.tier) throw new IllegalStateException();

		if (name != null) shield.setName(name + " " + shield.getName());
		shield.setBlockValue(Mechanics.DEFAULT_BLOCK / 2 * (tier - 1) + shield.getBlockValue());
		shield.setLevel(Mechanics.TIER_LEVEL * (tier - 1) + shield.getLevel());
	}

	@Override
	public boolean fits(ShieldBase shield)
	{
		return shield.getMaterialType() == getMaterialType();
	}
}
