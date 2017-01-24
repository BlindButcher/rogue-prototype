package creature;

import java.util.Map;

public class StatHolder
{
	private Map<Statistics, Integer> typeMap = Statistics.prepare();

	public void inc(Statistics type)
	{
		int oldVal = typeMap.get(type);
		typeMap.put(type, ++oldVal);
	}

    public void inc(Statistics type, int inc)
    {
        int oldVal = typeMap.get(type);
        typeMap.put(type, oldVal + inc);
    }

	public Map<Statistics, Integer> getMap()
	{
		return typeMap;
	}
}
