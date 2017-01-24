package inventory;

import ability.Ability;
import ability.AbilityType;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Test class for throwing things.
 *
 * @author Blind
 */
public class Grenade implements Equipable
{
    @Override
    public List<Ability> getAbilities()
    {
        return asList(new Ability(AbilityType.THROW, this));
    }

    @Override
    public EquipStrategy getEquipmentStrategy()
    {
        return EquipStrategy.MISC;
    }

    @Override
    public void setName(String name)
    {
    }

    @Override public Material getMaterial() {
        return null;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public char getC()
    {
        return 0;
    }

    @Override
    public String getId()
    {
        return null;
    }

    @Override
    public String getImagePath()
    {
        return null;
    }
}
