package ability;


/**
 * Some special actions, which can be performed by creature.
 * 
 * @author Blind
 */
public class Ability
{
	private AbilityType abilityType;
	private final Object source;

	public Ability(AbilityType abilityType, Object source)
	{
		this.abilityType = abilityType;
		this.source = source;
	}

	public AbilityType getAbilityType()
	{
		return abilityType;
	}

	public void setAbilityType(AbilityType abilityType)
	{
		this.abilityType = abilityType;
	}

	public Object getSource()
	{
		return source;
	}
}
