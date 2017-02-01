package inventory.shield;

import ability.Ability;
import ability.AbilityType;
import inventory.BasedEquipable;
import inventory.EquipStrategy;
import inventory.Material;

public class Shield extends BasedEquipable<ShieldBase> implements IShield
{
	private Material material;
	private int blockValue;
	private int level;

	public Shield(ShieldBase base, Material material)
	{
		super(base);
		this.name = getBase().getName();
		this.blockValue = getBase().getBlockValue();
		this.level = getBase().getLevel();

		this.material = material;
		this.enhance(material);

		Ability ability = new Ability(AbilityType.BLOCK, this);
		getAbilities().add(ability);
	}

	private boolean enhance(IShieldEnhance enhance)
	{
		if (!enhance.fits(this.getBase())) return false;
		enhance.apply(this.accessor, material.getTier());
		return true;
	}

	public int getLevel()
	{
		return level;
	}

	public int getBlockValue()
	{
		return blockValue;
	}

	@Override
	public EquipStrategy getEquipmentStrategy()
	{
		return EquipStrategy.SHIELD;
	}

    @Override public Material getMaterial() {
        return material;
    }

    @Override
	public String getImagePath()
	{
		return getBase().getImagePath();
	}

	@Override
	public String getId()
	{
		return getBase().getId();
	}

	private IShieldAccessor accessor = new IShieldAccessor()
	{

		@Override
		public String getName()
		{
			return Shield.this.getName();
		}

		@Override
		public int getBlockValue()
		{
			return Shield.this.getBlockValue();
		}

		@Override
		public int getLevel()
		{
			return Shield.this.getLevel();
		}

		@Override
		public ShieldBase getBase()
		{
			return Shield.this.getBase();
		}

		@Override
		public void setName(String name)
		{
			Shield.this.name = name;
		}

		@Override
		public void setBlockValue(int blockValue)
		{
			Shield.this.blockValue = blockValue;
		}

		@Override
		public void setLevel(int level)
		{
			Shield.this.level = level;
		}

	};
}
