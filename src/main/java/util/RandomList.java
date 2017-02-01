package util;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class RandomList<T> implements Iterable<T>
{
	private Map<T, Integer> map = newHashMap();
    private int totalWeight = 0;

    public RandomList() { }

    /**
     * For equal chance random list.
     *
     * @param list of items.
     */
    public RandomList(Collection<T> list)
    {
        for (T object : list) add(object, 1);
    }

    public void add(T item, int weight)
	{
		map.put(item, weight);
		totalWeight = 0;
		for (T itm : map.keySet())
			totalWeight += map.get(itm);
	}

	public T getItem()
	{
		if (totalWeight == 0) return null;
		int roll = Utils.roll(totalWeight)+1;
		for (T itm : map.keySet())
		{
			int current = map.get(itm);
			if (current >= roll) return itm;
			roll -= current;
		}
		throw new RuntimeException("Smth weird happened...");
	}

	public Integer remove(T item)
	{
		if (map.containsKey(item))
		{
			totalWeight -= map.get(item);
			return map.remove(item);
		}
		return null;
	}

	@Override
	public Iterator<T> iterator()
	{
		return map.keySet().iterator();
	}

    public int size()
    {
        return map.size();
    }

    public static <T> T randomItem(Collection<T> items)
    {
        return new RandomList<>(items).getItem();
    }
}
