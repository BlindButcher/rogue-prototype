package loader;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import creature.CreatureTemplate;
import dungeon.Cell;
import dungeon.Cells;
import inventory.Material;
import inventory.MaterialType;
import inventory.armor.ArmorQuality;
import inventory.armor.ArmorTemplate;
import inventory.shield.ShieldBase;
import inventory.weapon.WeaponBase;
import inventory.weapon.WeaponTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.util.Collections.min;

/**
 * Loads entities.
 * 
 * @author Blind
 */
public class EntityLoader
{
	private static final Logger LOG = Logger.getLogger(EntityLoader.class.getName());
	
	private static final String GRAPHIC_PATH = "graphic";
	private static final String CREATURE_PATH = GRAPHIC_PATH + "/creature";
	private static final String ITEM_PATH = GRAPHIC_PATH + "/item";
	public static final String WEAPON_PATH = ITEM_PATH + "/weapon";
	public static final String ARMOR_PATH = ITEM_PATH + "/armor";
	public static final String SHIELD_PATH = ITEM_PATH + "/shield";

	private String creatureFile, itemFile, mapFile, materialFile;

	private Set<CreatureTemplate> templates = newHashSet();
	private Set<WeaponBase> weaponBase = newHashSet();
	private Set<ArmorTemplate> armorTemplates = newHashSet();
	private Set<ArmorQuality> armorQualities = newHashSet();
	private Set<ShieldBase> shieldBases = newHashSet();
	private Set<Cells> maps = newHashSet();
    private Set<Material> materials = newHashSet();
    private Set<MaterialType> materialTypes = newHashSet();
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	@Inject
	public EntityLoader(@Named("creatureFile")
	String creature, @Named("itemFile")
	String itemFile, @Named("mapFile")
	String mapFile, @Named("materialFile") String materialFile)
	{
		this.creatureFile = creature;
		this.itemFile = itemFile;
		this.mapFile = mapFile;
        this.materialFile = materialFile;
		try
		{
            loadMaterials();
			loadItems();
			loadCreatureTemplate();
			loadMaps();
		} catch (Exception e)
		{
			throw new RuntimeException("template file is not loaded", e);
		}
	}

    private void loadMaterials() throws Exception
    {
        MaterialContainer container = JAXB.unmarshal(getClass().getClassLoader().getResourceAsStream(materialFile),
				MaterialContainer.class);
        materials = newHashSet(container.material);
        materialTypes = newHashSet(container.materialTypes);
    }

    private void loadMaps() throws Exception
	{
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(mapFile));
		NodeList nodeList = doc.getElementsByTagName("map");
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node cur = nodeList.item(i);
			String id = cur.getAttributes().getNamedItem("id").getNodeValue();
			String[] splitted = cur.getTextContent().trim().split("\\n");
			for (int j = 0; j < splitted.length; j++)
			{
				splitted[j] = splitted[j].trim();
			}

			int rows = splitted.length, cols = splitted[0].length();
			Cell[][] map = new Cell[rows][cols];
			for (int j = 0; j < rows; j++)
				for (int k = 0; k < cols; k++)
				{
					map[j][k] = Cell.fromChar(splitted[j].charAt(k));
				}
			maps.add(new Cells(map, id));
		}

		LOG.info(format("Map file is loaded with %s maps", maps.size()));
	}

	private void loadItems() throws Exception
	{
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(itemFile));
		NodeList list = doc.getElementsByTagName("items").item(0).getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			if (list.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				switch (list.item(i).getNodeName())
				{
				case "weapons":
					loadWeapons(list.item(i).getChildNodes());
					break;
				case "armors":
					loadArmors(list.item(i).getChildNodes());
					break;
				case "armorQualities":
					loadArmorQualities(list.item(i).getChildNodes());
					break;
				case "shields":
					loadShields(list.item(i).getChildNodes());
					break;
				}
			}
		}

	}

	private void loadShields(NodeList childNodes)
	{
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			if ("shield".equals(childNodes.item(i).getNodeName()))
			{
				shieldBases.add(ShieldBase.fromNode(childNodes.item(i), materialTypes));
			}
		}
		LOG.info(format("Shields are loaded with %s ", shieldBases().size()));
	}

	private void loadArmorQualities(NodeList childNodes)
	{
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			if ("armorQuality".equals(childNodes.item(i).getNodeName()))
			{
				armorQualities.add(ArmorQuality.fromNode(childNodes.item(i)));
			}
		}
		LOG.info(format("Armour qualities are loaded with %s qualities", armorQualities.size()));
	}

	private void loadArmors(NodeList childNodes)
	{
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			if ("armor".equals(childNodes.item(i).getNodeName()))
			{
				armorTemplates.add(ArmorTemplate.fromNode(childNodes.item(i), materialTypes));
			}
		}

		LOG.info(format("Armours are loaded with %s armours", armorTemplates.size()));
	}

	private void loadWeapons(NodeList weapons)
	{
		for (int i = 0; i < weapons.getLength(); i++)
		{
			if ("weapon".equals(weapons.item(i).getNodeName()))
			{
				weaponBase.add(WeaponTemplate.fromNode(weapons.item(i), materialTypes));
			}
		}
	}

	public CreatureTemplate findCreature(final String id)
	{
		return Iterables.find(templates, new Predicate<CreatureTemplate>()
		{
			public boolean apply(CreatureTemplate creatureTemplate)
			{
				return creatureTemplate.getId().equals(id);
			}
		});
	}

	public WeaponBase findWeapon(final String id)
	{
		return Iterables.find(weaponBase, new Predicate<WeaponBase>()
		{
			public boolean apply(WeaponBase weapon)
			{
				return weapon.getId().equals(id);
			}
		});
	}

    public Material findMaterial(final String id)
    {
        return Iterables.find(materials, new Predicate<Material>()
        {
            public boolean apply(Material material)
            {
                return material.getId().equals(id);
            }
        });
    }

	public ArmorTemplate findArmor(final String id)
	{
		return Iterables.find(armorTemplates, new Predicate<ArmorTemplate>()
		{
			public boolean apply(ArmorTemplate armor)
			{
				return armor.getId().equals(id);
			}
		});
	}

	public Set<CreatureTemplate> templates()
	{
		return newHashSet(templates);
	}

	public Set<WeaponBase> weaponBase()
	{
		return newHashSet(weaponBase);
	}

	public Set<ArmorTemplate> armorTemplates()
	{
		return newHashSet(armorTemplates);
	}

	public Set<ArmorQuality> armorQualitySet()
	{
		return newHashSet(armorQualities);
	}

	public Set<Cells> mapBase()
	{
		return newHashSet(maps);
	}

	public Set<ShieldBase> shieldBases()
	{
		return newHashSet(shieldBases);
	}

    public Set<Material> getMaterials()
    {
        return newHashSet(materials);
    }

    public Set<MaterialType> getMaterialTypes()
    {
        return newHashSet(materialTypes);
    }

    public Collection<Material> getMaterialForType(final MaterialType type)
    {
        return filter(materials, new Predicate<Material>()
        {
            public boolean apply(Material material)
            {
                return type.equals(material.getMaterialType());
            }
        });
    }

    public Material lowTierForType(final MaterialType type)
    {
        return min(getMaterialForType(type), (o1, o2) -> Integer.valueOf(o1.getTier()).compareTo(o2.getTier()));
    }

    private void loadCreatureTemplate() throws Exception
	{
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream(creatureFile));

		Set<UnifiedAttribute> unifiedAttributes = newHashSet();
		NodeList nodeList = doc.getElementsByTagName("unifiedAttribute");
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			unifiedAttributes.add(UnifiedAttribute.fromNode(nodeList.item(i)));
		}

		nodeList = doc.getElementsByTagName("creatureTemplate");
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node cur = nodeList.item(i);
			CreatureTemplate template = new CreatureTemplate();
			template.setId(cur.getAttributes().getNamedItem("id").getNodeValue());
			template.setName(cur.getAttributes().getNamedItem("name").getNodeValue());
			template.setGlyph(cur.getAttributes().getNamedItem("glyph").getNodeValue().charAt(0));
			template.setExpValue(Integer.valueOf(cur.getAttributes().getNamedItem("exp").getNodeValue()));
			template.setImagePath(CREATURE_PATH + "/" + cur.getAttributes().getNamedItem("graphic").getNodeValue());

			if (cur.getAttributes().getNamedItem("color") != null) template.setColor(cur.getAttributes().getNamedItem("color").getNodeValue());

			if (cur.getAttributes().getNamedItem("isBoss") != null) template.setBoss(true);

			setParent(cur, template);

			NodeList stuff = cur.getChildNodes();
			for (int j = 0; j < stuff.getLength(); j++)
			{
				Node currStuff = stuff.item(j);
				if (currStuff.getNodeType() == Node.ELEMENT_NODE)
				{
					switch (currStuff.getNodeName())
					{
					case "weapon":
						template.getWeapons().add(findWeapon(currStuff.getTextContent()));
						break;
					case "attr":
						template.getUnifiedAttributes().add(UnifiedAttribute.fromNode(currStuff, unifiedAttributes));
						break;
					case "ability":
						break;
					case "armor":
						template.getArmors().add(findArmor(currStuff.getTextContent()));
						break;
					case "shield":
						template.getShields().add(findShield(currStuff.getTextContent()));
						break;
					case "predefinedWeapon":
						template.setPredefinedWeapon(WeaponTemplate.fromNode(currStuff, materialTypes));
						break;
					}
				}
			}

			templates.add(template);
		}
	}

	public ShieldBase findShield(final String textContent)
	{
		return Iterables.find(shieldBases, new Predicate<ShieldBase>()
		{
			public boolean apply(ShieldBase armor)
			{
				return armor.getId().equals(textContent);
			}
		});
	}

	private void setParent(Node cur, CreatureTemplate template)
	{
		if (cur.getAttributes().getNamedItem("parent") == null) return;

		final String parentId = cur.getAttributes().getNamedItem("parent").getNodeValue();

		Optional<CreatureTemplate> parent = tryFind(templates, new Predicate<CreatureTemplate>()
		{
			public boolean apply(CreatureTemplate template)
			{
				return template.getId().equals(parentId);
			}
		});

		if (parent.isPresent())
			template.setParentTemplate(parent.get());
		else
			throw new IllegalArgumentException(format("Parent with id: %s not found. Check xml elements order.", parentId));
	}
}
