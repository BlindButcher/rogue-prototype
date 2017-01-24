package inventory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import util.INamedEntity;

@XmlAccessorType(XmlAccessType.FIELD)
public class MaterialType implements INamedEntity
{
	public static final MaterialType NATURAL = new MaterialType("NATURAL", "", true);

	@XmlID
	@XmlAttribute(name = "id")
	private String id;
	@XmlAttribute(name = "name")
	private String name;
	@XmlAttribute(name = "natural", required = false)
	private boolean natural = false;

	public MaterialType()
	{
	}

	public MaterialType(String id, String name, boolean natural)
	{
		this.id = id;
		this.name = name;
		this.natural = natural;
	}

	public boolean isNatural()
	{
		return natural;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MaterialType that = (MaterialType) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;

		return true;
	}

	public int hashCode()
	{
		return id != null ? id.hashCode() : 0;
	}
}
