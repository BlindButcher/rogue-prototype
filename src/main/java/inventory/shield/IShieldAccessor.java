package inventory.shield;

public interface IShieldAccessor extends IShield
{
	void setName(String name);

	void setBlockValue(int blockValue);

	void setLevel(int level);
}
