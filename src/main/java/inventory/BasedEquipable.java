package inventory;

import ability.Ability;
import com.google.common.collect.Lists;

import java.util.List;

public abstract class BasedEquipable<B extends ItemBase> implements Equipable
{
	private B base;
	private final List<Ability> abilities = Lists.newArrayList();
	protected String name;

	protected BasedEquipable(B base)
	{
		if (base == null) throw new IllegalStateException();
		this.base = base;
	}

	public B getBase()
	{
		return base;
	}

	@Override
	public char getC()
	{
		return getBase().getC();
	}

	@Override
	public List<Ability> getAbilities()
	{
		return abilities;
	}

	@Override
	public String getName()
	{
		return name;
	}

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
	public String toString()
	{
		return name;
	}
}
