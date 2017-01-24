package main;

import creature.Creature;

public class TimeEvent implements Comparable<TimeEvent>
{
	private final long time;
	private final Creature creature;
	private final EventType type;

	public TimeEvent(long time, Creature creature, EventType type)
	{
		super();
		this.time = time;
		this.creature = creature;
		this.type = type;
	}

	public long getTime()
	{
		return time;
	}

	public Creature getCreature()
	{
		return creature;
	}

	public EventType getType()
	{
		return type;
	}

	@Override
	public int compareTo(TimeEvent other)
	{
		if (time == other.time) return 0;
		return time < other.time ? -1 : 1;
	}
}
