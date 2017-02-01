package inventory.weapon;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import core.CoverageArea;
import core.DamageType;
import core.RangeInfluence;
import inventory.MaterialType;
import loader.EntityLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static main.Delays.DEFAULT_DELAY;

/**
 * Weapon template.
 * 
 * @author Blind
 */
public class WeaponTemplate implements WeaponBase
{
	private static Map<DamageType, Integer> defaultDamage = newHashMap();

	static
	{
		defaultDamage.put(DamageType.PHYSICAL, 1);
	}

	private final int delay;
	private final Map<DamageType, Integer> damageMap;
	private final double bonusFactor;
	private final boolean twoHanded, ranged;
	private final int range;
	private final String name, id;
	private final char c;
	private final MaterialType materialType;
	private final Map<WeaponProperty, Integer> properties;
	private final CoverageArea coverageArea;
    private final RangeInfluence rangeInfluence;
	private final String imagePath;

	public static final WeaponTemplate UNARMED = new WeaponTemplate(defaultDamage, DEFAULT_DELAY.amount, 1.0, false, false, 1, "Unarmed", 'u', "UNARMED",
			MaterialType.NATURAL, Collections.<WeaponProperty, Integer> emptyMap(), CoverageArea.SINGLE_SQUARE, null, RangeInfluence.DEFAULT);

    public static final WeaponTemplate UNARMED_RANGE = new WeaponTemplate(defaultDamage, DEFAULT_DELAY.amount, 1.0, false, true, 10, "Unarmed", 'u', "UNARMED",
            MaterialType.NATURAL, Collections.<WeaponProperty, Integer> emptyMap(), CoverageArea.SINGLE_SQUARE, null, RangeInfluence.DEFAULT);

	private WeaponTemplate(Map<DamageType, Integer> damageMap, int delay, double bonusFactor, boolean twoHanded, boolean ranged, int range,
			String name, char c, String id, MaterialType type, Map<WeaponProperty, Integer> properties, CoverageArea coverageArea, String imagePath, RangeInfluence rangeInfluence)
	{
		this.damageMap = Collections.unmodifiableMap(damageMap);
		this.delay = delay;
		this.bonusFactor = bonusFactor;
		this.twoHanded = twoHanded;
		this.ranged = ranged;
		this.range = range;
		this.name = name;
		this.c = c;
		this.id = id;
		this.materialType = type;
		this.properties = Collections.unmodifiableMap(properties);
		this.coverageArea = coverageArea;
        this.rangeInfluence = rangeInfluence;
		this.imagePath = imagePath;
	}

	public int getDelay()
	{
		return delay;
	}

	public Map<DamageType, Integer> getDamageMap()
	{
		return damageMap;
	}

    public RangeInfluence getRangeInfluence()
    {
        return rangeInfluence;
    }

    public double getBonusFactor()
	{
		return bonusFactor;
	}

	public boolean isTwoHanded()
	{
		return twoHanded;
	}

	public int getRange()
	{
		return range;
	}

	public boolean isRanged()
	{
		return ranged;
	}

	public MaterialType getMaterialType()
	{
		return materialType;
	}

	public String getName()
	{
		return name;
	}

	public char getC()
	{
		return c;
	}

	public String getId()
	{
		return id;
	}

	public Map<WeaponProperty, Integer> getProperties()
	{
		return newHashMap(properties);
	}

	public boolean hasProperty(WeaponProperty property)
	{
		return properties.containsKey(property);
	}

	public CoverageArea getCoverageArea()
	{
		return coverageArea;
	}

	public static WeaponTemplate fromNode(Node node, Set<MaterialType> types)
	{
		String id = node.getAttributes().getNamedItem("id") == null ? null : node.getAttributes().getNamedItem("id").getNodeValue();
		String name = node.getAttributes().getNamedItem("name").getNodeValue();
		String imagePath = node.getAttributes().getNamedItem("graphic") == null ? null : EntityLoader.WEAPON_PATH + "/"
				+ node.getAttributes().getNamedItem("graphic").getNodeValue();

		Element curElement = (Element) node;
		NodeList damNode = curElement.getElementsByTagName("damage");
		Map<DamageType, Integer> damageMap = newHashMap();
		for (int i = 0; i < damNode.getLength(); i++)
		{
			Node damageNode = damNode.item(i);
			damageMap.put(DamageType.valueOf(damageNode.getAttributes().getNamedItem("damageType").getNodeValue()),
					Integer.valueOf(damageNode.getTextContent()));
		}

		String delay = curElement.getElementsByTagName("delay").item(0).getTextContent();
		double bonusFactor = Double.valueOf(curElement.getElementsByTagName("bonusFactor").item(0).getTextContent());
		int range = Integer.valueOf(curElement.getElementsByTagName("range").item(0).getTextContent());

		boolean ranged = Boolean.valueOf(node.getAttributes().getNamedItem("ranged").getNodeValue());
		boolean twoHanded = Boolean.valueOf(node.getAttributes().getNamedItem("twoHanded").getNodeValue());

		char c = name.toLowerCase().charAt(0);
		final String materialId = curElement.getElementsByTagName("materialType").item(0).getTextContent();
		MaterialType materialType = Iterables.find(types, new Predicate<MaterialType>()
		{
			public boolean apply(MaterialType materialType)
			{
				return materialType.getId().equals(materialId);
			}
		});

		String[] delays = delay.split("/");

		NodeList properties = curElement.getElementsByTagName("property");
		Map<WeaponProperty, Integer> propertyMap = newHashMap();
		for (int j = 0; j < properties.getLength(); j++)
		{
			Node property = properties.item(j);
			int value = Integer.valueOf(property.getAttributes().getNamedItem("value").getNodeValue());
			propertyMap.put(WeaponProperty.valueOf(property.getTextContent()), value);
		}

		CoverageArea coverageArea = CoverageArea.SINGLE_SQUARE;
		if (curElement.getElementsByTagName("coverageArea").getLength() > 0)
		{
			coverageArea = CoverageArea.valueOf(curElement.getElementsByTagName("coverageArea").item(0).getTextContent());
		}

        RangeInfluence rangeInfluence = RangeInfluence.DEFAULT;
        if (curElement.getElementsByTagName("rangeInfluence").getLength() > 0)
        {
            rangeInfluence = RangeInfluence.valueOf(curElement.getElementsByTagName("rangeInfluence").item(0).getAttributes().getNamedItem("type").getNodeValue());
        }

		return new WeaponTemplate(damageMap, DEFAULT_DELAY.amount * Integer.valueOf(delays[0]) / Integer.valueOf(delays[1]), bonusFactor, twoHanded,
				ranged, range, name, c, id, materialType, propertyMap, coverageArea, imagePath, rangeInfluence);
	}

	@Override
	public String toString()
	{
		return "WeaponTemplate: " + name;
	}

	@Override
	public String getImagePath()
	{
		return imagePath;
	}
}
