package ability;

import inventory.Material;
import inventory.weapon.Weapon;
import inventory.weapon.WeaponTemplate;

/**
 * Some special type of actions, which can be performed by creature.
 * 
 * @author Blind
 */
public enum AbilityType
{
	MELEE_ATTACK(new Weapon(WeaponTemplate.UNARMED, Material.NATURAL)),
	RANGE_ATTACK(),
    THROW,
	BLOCK(),
	PARRY(),
    PULSE(true);

	private final Object defaultSource;
    private final boolean leaveOnMove;

    public final static long PULSE_REGAIN = 25;

	private AbilityType(Object defaultSource)
	{
		this.defaultSource = defaultSource;
        this.leaveOnMove = false;
	}

    private AbilityType(boolean removeOnMove)
    {
        this.defaultSource = null;
        this.leaveOnMove = removeOnMove;
    }

    private AbilityType()
    {
        this.defaultSource = null;
        this.leaveOnMove = false;
    }

    public Object getDefaultSource()
	{
		return defaultSource;
	}

    public boolean isLeaveOnMove()
    {
        return leaveOnMove;
    }
}
