package main;

import creature.Creature;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class TimeEvent implements Comparable<TimeEvent>
{
	private final long time;
	private final Creature creature;
	private final EventType type;

	public TimeEvent(long time, Creature creature, EventType type)
	{
		super();
		this.time = time;
		this.creature = requireNonNull(creature);
		this.type = requireNonNull(type);
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
