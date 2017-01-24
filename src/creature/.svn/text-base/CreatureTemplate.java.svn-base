package creature;

import static com.google.common.collect.Sets.newHashSet;
import inventory.armor.ArmorTemplate;
import inventory.shield.ShieldBase;
import inventory.weapon.WeaponBase;
import inventory.weapon.WeaponTemplate;

import java.util.Set;

import loader.UnifiedAttribute;
import ability.Ability;

public class CreatureTemplate
{
	public static final String MONSTER_DEFAULT_COLOR = "#669999";

	private String id, name;
	private char glyph;
	private Set<UnifiedAttribute> unifiedAttributes = newHashSet();
	private Set<WeaponBase> weapons = newHashSet();
	private Set<ArmorTemplate> armors = newHashSet();
	private Set<Ability> abilities = newHashSet();
	private Set<ShieldBase> shields = newHashSet();
	private int expValue;
	private boolean boss = false;
	private WeaponTemplate predefinedWeapon;
	private String color = MONSTER_DEFAULT_COLOR;
	private CreatureTemplate parentTemplate;
	private String imagePath;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public char getGlyph()
	{
		return glyph;
	}

	public void setGlyph(char glyph)
	{
		this.glyph = glyph;
	}

	public Set<WeaponBase> getWeapons()
	{
		return weapons;
	}

	public void setWeapons(Set<WeaponBase> weapons)
	{
		this.weapons = weapons;
	}

	public Set<ArmorTemplate> getArmors()
	{
		return armors;
	}

	public void setArmors(Set<ArmorTemplate> armors)
	{
		this.armors = armors;
	}

	public Set<Ability> getAbilities()
	{
		return abilities;
	}

	public int getExpValue()
	{
		return expValue;
	}

	public void setExpValue(int expValue)
	{
		this.expValue = expValue;
	}

	public boolean isBoss()
	{
		return boss;
	}

	public void setBoss(boolean boss)
	{
		this.boss = boss;
	}

	public Set<ShieldBase> getShields()
	{
		return shields;
	}

	public void setShields(Set<ShieldBase> shields)
	{
		this.shields = shields;
	}

	public WeaponTemplate getPredefinedWeapon()
	{
		return predefinedWeapon;
	}

	public void setPredefinedWeapon(WeaponTemplate predefinedWeapon)
	{
		this.predefinedWeapon = predefinedWeapon;
	}

	public Set<UnifiedAttribute> getUnifiedAttributes()
	{
		return parentTemplate != null && unifiedAttributes.isEmpty() ? parentTemplate.unifiedAttributes : unifiedAttributes;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public CreatureTemplate getParentTemplate()
	{
		return parentTemplate;
	}

	public void setParentTemplate(CreatureTemplate parentTemplate)
	{
		this.parentTemplate = parentTemplate;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

}
