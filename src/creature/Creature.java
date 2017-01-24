package creature;

import ability.Ability;
import ability.AbilityType;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import core.CoverageArea;
import core.Mechanics;
import creature.HitContext.Property;
import dungeon.Cell;
import effect.IEffect;
import inventory.Equipable;
import inventory.EquipmentSlot;
import inventory.armor.Armor;
import inventory.armor.ArmorType;
import inventory.shield.Shield;
import inventory.weapon.IWeapon;
import inventory.weapon.WeaponProperty;
import loader.UnifiedAttribute;
import main.*;
import util.PathFinder;
import util.Point;
import util.Utils;
import view.IRenderable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static ability.AbilityType.MELEE_ATTACK;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static creature.Attribute.DAMAGE;
import static creature.Statistics.DAMAGE_DONE;
import static creature.Statistics.KILLS_MADE;
import static effect.EffectType.*;
import static inventory.EquipmentSlot.TORSO;
import static inventory.armor.ArmorProperty.SPIKED;
import static inventory.weapon.WeaponProperty.AUTO_HIT;
import static java.lang.String.format;
import static main.Delays.DEFAULT_DELAY;
import static util.Point.point;

public class Creature implements TimeEventAware, IRenderable
{
	static public final int VISION = 10;

	private static final Logger MAIN = Logger.getLogger("MAIN");
    private static final Logger LOGGER = Logger.getLogger(Creature.class.getName());
    public static final int THROW_RANGE = 5;
    public static final int THROW_DAMAGE = 5;
	private CreatureTemplate template;

	protected LevelContext level;
	private EventHolder eventHolder;

	private String name;
	private String templateId;
    private Point xy;
	private char c;
	private String color;

	private Map<EquipmentSlot, Equipable> equipment = newHashMap();
	private Map<Attribute, Integer> attributes = newHashMap();
	private Map<AbilityType, Ability> abilities = newHashMap();
	private Set<AbilityType> coolDown = Sets.newHashSet();

	private int wounds;

	private int faction;
	private Creature target = null;
	private int rank = 1;
	private int expValue = 1;
	public boolean boss;

	private Set<IEffect> activeEffects = Sets.newHashSet();

	private StatHolder stat;

	protected Map<Attribute, Integer> growth = newHashMap();
	protected Map<Attribute, Integer> points = newHashMap();

	public Creature(CreatureTemplate template)
	{
		this.templateId = template.getId();
		this.name = template.getName();
		this.c = template.getGlyph();
		this.expValue = template.getExpValue();
		this.boss = template.isBoss();
		this.color = template.getColor();

		for (UnifiedAttribute unified : template.getUnifiedAttributes())
		{
			setAttribute(unified.attribute, unified.base);
			setAttributeGrowth(unified.attribute, unified.growth);
		}

		for (Ability a : template.getAbilities())
		{
			getAbilities().put(a.getAbilityType(), a);
		}

		for (Attribute attr : Attribute.values())
		{
			points.put(attr, 0);
		}

		this.stat = new StatHolder();

		this.template = template;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getX()
	{
		return xy.x;
	}

	public void setX(int x)
	{
		this.xy = xy.setX(x);
	}

	public int getY()
	{
		return xy.y;
	}

	public void setY(int y)
	{
		this.xy = xy.setY(y);
	}

	public char getC()
	{
		return c;
	}

	public void setC(char c)
	{
		this.c = c;
	}

	public int getWounds()
	{
		return wounds;
	}

	public boolean isAlive()
	{
		return wounds < getMaxHp();
	}

	public int getMaxHp()
	{
		return getAttribute(Attribute.HEALTH);
	}

	public int getCurHp()
	{
		return getAttribute(Attribute.HEALTH) - wounds;
	}

	public int getMelee()
	{
		int melee = getAttribute(Attribute.MELEE);
		Armor torso = (Armor) getEquiped(TORSO);
		if (torso != null && torso.getCombatPenalty() > 0) melee -= torso.getCombatPenalty();
		return melee;
	}

	public int getRanged()
	{
		int ranged = getAttribute(Attribute.RANGED);
		Armor torso = (Armor) getEquiped(TORSO);
		if (torso != null && torso.getCombatPenalty() > 0) ranged -= torso.getCombatPenalty();
		return ranged;
	}

	public int getDefence()
	{
		int defence = getAttribute(Attribute.DEFENCE);
		Armor torso = (Armor) getEquiped(TORSO);
		if (torso != null && torso.getCombatPenalty() > 0) defence -= torso.getCombatPenalty();
		return defence;
	}

	public Map<AbilityType, Ability> getAbilities()
	{
		return abilities;
	}

	public int getArmor()
	{
		int armor = getAttribute(Attribute.ARMOR);
		Armor torso = (Armor) getEquiped(TORSO);
		if (torso != null) armor += torso.getArmor();
		return armor;
	}

	public void heal(int hp)
	{
		int cur = this.wounds;

		this.wounds -= hp;
		if (this.wounds < 0) this.wounds = 0;

		int healed = cur - this.wounds;
		MAIN.fine(getName() + " is healed for " + healed + ".");
	}

	public double getSpeedFactor()
	{
		double factor = 20.0 / getAttribute(Attribute.SPEED);
		Armor armor = (Armor) getEquiped(TORSO);
		if (armor != null && armor.getSpeedFactor() > 1) factor *= armor.getSpeedFactor();
		return factor;
	}

    public int getMoveDelay() {
        boolean pinned = getActiveEffects().stream().filter(e -> e.getType() == PIN).findAny().isPresent();
        return (int) ( (DEFAULT_DELAY.amount * getSpeedFactor()) * (pinned ? PIN_SPEED_DELAY : 1));
    }

	public int getFaction()
	{
		return faction;
	}

	public int hit(Point point, IWeapon weapon)
	{
        Creature go = level.getCreature(point);
        if (go != null && go != this)
        {

            int attackCount = 1 +  (weapon.isRanged() ? weapon.getProperty(WeaponProperty.AUTO_SHOT) : 0);
            int delay = 1;
            if (attackCount > 1) MAIN.fine(format("Shoot %s times", attackCount));
            for (int i = 0; i < attackCount; i++)
            {
                for (Point target : weapon.getCoverageArea().effectedPoints(this.getXY(), point(point.x, point.y)))
                {
                    go = level.getCreature(target);
                    if (go != null && go.getFaction() != getFaction())
                    {
                        delay = hitTarget(go, weapon);
                    }
                }

                if (delay != 1 && weapon.getCoverageArea() != CoverageArea.SINGLE_SQUARE)
                    MAIN.fine(weapon.getCoverageArea().text);
            }
            return delay;
        }

		return hitTarget(level.getCreature(point), weapon);
	}

    /**
     * Proceed hit for concrete target.
     *
     * @param target creature.
     * @param weapon inflicting damage.
     * @return delay
     */
    public int hitTarget(Creature target, IWeapon weapon)
    {
        Preconditions.checkNotNull(target);
        Preconditions.checkNotNull(weapon);

        HitContext context = new HitContext(weapon, target);
        stat.inc(Statistics.HITS_MADE);

        context.prepareAttackVal(this, target, weapon);
        context.put(Property.DEFENCE, target.getDefence());
        context.put(Property.ATTACKER, this);

        if (weapon.hasProperty(AUTO_HIT)|| (hitBody(context) && !shieldBlock(context) && !parried(context)))
        {
            context.put(Property.ATTACK_PULSED, tryPulse(context));

            Mechanics.damage(context, getAttribute(DAMAGE), tryCritical(context, weapon));
            final int damageDone = context.damageDone(), totalDamage = context.damageTotal();

            getArmorType().armorEffect(context);

            MAIN.fine(format("%s hits %s for %s damage.", getName(), target.getName(), context.damageDoneMessage()));

            Preconditions.checkArgument(damageDone >= 0);

            stat.inc(DAMAGE_DONE, damageDone);

            target.applyDamage(this, damageDone);

            if (!weapon.isRanged() && target.getEquiped(TORSO) != null && ((Armor) target.getEquiped(TORSO))
                    .hasProperty(SPIKED))
            {
                final int spiked = Mechanics.normal(((Armor) target.getEquiped(TORSO)).getPropertyPower(SPIKED));
                MAIN.fine(format("%s spikes %s for %s", target.getName(), getName(), spiked));
                this.applyDamage(target, spiked);
            }

            WeaponProperty.tryWeaponEffects(target, weapon, damageDone, totalDamage);

            drainLife(weapon, damageDone);
        }

        if (context.hasProperty(Property.ATTACK_PARRIED))
            stat.inc(Statistics.ATTACK_PARRIED);

        return HitResolver.getDelay(this, weapon);
    }

    private boolean tryPulse(HitContext context)
    {
        if (context.getTarget().hasAbility(AbilityType.PULSE) && !coolDown.contains(AbilityType.PULSE))
        {
            MAIN.fine(format("%s is protected by pulsing.", context.getTarget().getName()));
            coolDown.add(AbilityType.PULSE);
            addEvent(new RegainAbilityEvent(AbilityType.PULSE_REGAIN, context.getTarget(), AbilityType.PULSE));
            return true;
        }
        return false;
    }

    public ArmorType getArmorType()
    {
        Armor armor = (Armor) getEquiped(TORSO);
        return armor == null ? ArmorType.NONE : armor.getBase().getArmorType();
    }

    private void drainLife(IWeapon weapon, int damageDone)
	{
		int vampiricPower = weapon.getProperty(WeaponProperty.VAMPIRIC);
		if (vampiricPower > damageDone) vampiricPower = damageDone;
		if (vampiricPower > 0)
		{
			this.heal(vampiricPower);
		}
	}

	private int tryCritical(HitContext context, IWeapon weapon)
	{
		int attack = context.get(Property.ATTACK);
		int defence = context.get(Property.DEFENCE);
		int damageModifier = 100;
		int critBonus = 0;
		if (weapon.hasProperty(WeaponProperty.CRITICAL_CHANCE))
		{
			critBonus += Mechanics.CRIT_CHANCE_MODIFIER * (1 + weapon.getProperty(WeaponProperty.CRITICAL_CHANCE));
		}
		int critModifier = Mechanics.BASE_CRIT_MODIFIER;
		if (weapon.hasProperty(WeaponProperty.CRITICAL_DAMAGE))
		{
			critModifier += Mechanics.CRIT_DAMAGE_MODIFIER * (1 + weapon.getProperty(WeaponProperty.CRITICAL_DAMAGE));
		}
		if (Mechanics.critical(attack, defence, critBonus))
		{
			damageModifier = critModifier;
			MAIN.fine(format("%s scores critical hit.", getName()));
		}
		return damageModifier;
	}

	private boolean parried(HitContext context)
	{
		boolean isMeleeAttack = !context.getWeapon().isRanged();
		int attack = context.get(Property.ATTACK);
		IWeapon parry = (IWeapon) context.getTarget().getAbilitySource(AbilityType.PARRY);
		if (!coolDown.contains(AbilityType.PARRY) && isMeleeAttack && parry != null
				&& !Mechanics.hit(attack, parry.getProperty(WeaponProperty.PARRY) * Mechanics.PARRY_MODIFIER))
		{
			MAIN.fine(format("%s parries %s 's attack.", context.getTarget().getName(), getName()));
            context.addFlag(Property.ATTACK_PARRIED);
			coolDown.add(AbilityType.PARRY);
			return true;
		}
		return false;
	}

	private boolean shieldBlock(HitContext context)
	{
		int attack = context.get(Property.ATTACK);
		Shield shield = (Shield) context.getTarget().getAbilitySource(AbilityType.BLOCK);
		if (!coolDown.contains(AbilityType.BLOCK) && shield != null && !Mechanics.hit(attack, shield.getBlockValue()))
		{
			stat.inc(Statistics.HITS_BLOCK);
			MAIN.fine(context.getTarget().getName() + " blocks " + getName() + "'s attack.");
			coolDown.add(AbilityType.BLOCK);
			return true;
		}
		return false;
	}

	private boolean hitBody(HitContext context)
	{
		int attack = context.get(Property.ATTACK);
		int defence = context.get(Property.DEFENCE);
		boolean hit = Mechanics.hit(attack, defence);
		if (!hit)
		{
			MAIN.fine(getName() + " misses " + context.getTarget().getName() + ".");
			stat.inc(Statistics.HITS_MISS);
		}
		return hit;
	}

	public void applyDamage(Creature source, int damage)
	{
		stat.inc(Statistics.DAMAGE_RECEIVED, damage);
		wounds += damage;
		if (!isAlive())
		{
			if (source != null)
			{
				source.onKill(this);
			}
			addEvent(new TimeEvent(eventHolder.time, this, EventType.KILLED));
		}
	}

	public int update()
	{
		if (target == null || !target.isAlive())
		{
			target = searchTarget();
		}

		if (target != null && sees(target))
		{
			Ability ranged = abilities.get(AbilityType.RANGE_ATTACK);
			if (ranged != null && Utils.reach(this, target, ((IWeapon) ranged.getSource()).getRange()))
			{
				MAIN.fine("Preparing range attack to " + target.getName());
				return hit(target.getXY(), (IWeapon) ranged.getSource());
			}
			else
			{
				PathFinder pathFinder = new PathFinder();
				List<Point> pointList = pathFinder.findPath(level, this, getXY(), target.getXY(), 10);

				if (!pointList.isEmpty()) return goTo(pointList.get(0));

				int[] to = Utils.nextPoint(getX(), getY(), target.getX(), target.getY());
				return goTo(point(to[0], to[1]));
			}
		}
		else
		{
			target = null;
			int x = getX(), y = getY();
			int dx = Utils.roll(3) - 1, dy = Utils.roll(3) - 1;
			if (dx != 0 || dy != 0)
			{
				return goTo(point(x + dx, y + dy));
			}
			else
				return DEFAULT_DELAY.amount;
		}
	}

	public Object getAbilitySource(AbilityType type)
	{
		Ability ability = abilities.get(type);
		if (ability == null)
		{
			return type.getDefaultSource();
		}
		else
		{
			return ability.getSource();
		}
	}

    public boolean hasAbility(AbilityType abilityType)
    {
        return abilities.containsKey(abilityType);
    }

    public int goTo(Point point) {
        Optional<Creature> go = level.in(point);
        if (go.isPresent() && !go.get().equals(this)) {
            return hit(point, (IWeapon) getAbilitySource(MELEE_ATTACK));
        } else if (level.getMap().getCell(point) == Cell.DOOR) {
            level.openDoor(point.x, point.y);
            return DEFAULT_DELAY.amount;
        } else if (level.getMap().getCell(point).isPassable() || point.equals(getXY()) &&
                !getActiveEffects().stream().anyMatch(e -> e.getType() == DISABLED)) {
            setXY(point);
            return getMoveDelay();
        } else {
            return DEFAULT_DELAY.amount;
        }
    }

	public boolean inRange(int x, int y)
	{
		Ability ability = abilities.get(AbilityType.RANGE_ATTACK);
		int range = ability == null ? 0 : ((IWeapon) ability.getSource()).getRange();
		return sees(x, y) && Utils.reach(this.getX(), this.getY(), x, y, range);
	}

    public boolean inThrowRange(Point point)
    {
        Ability ability = abilities.get(AbilityType.THROW);
        int range = ability == null ? 0 : THROW_RANGE;
        return sees(point.x, point.y) && Utils.reach(this.getX(), this.getY(), point.x, point.y, range);
    }

	private Creature searchTarget()
	{
		Creature result = null;
		int dist2 = Integer.MAX_VALUE;
		for (Creature cur : level.getCreatures())
		{
			if (cur.getFaction() != getFaction() && sees(cur) && (result == null || Utils.dist2(this, cur) < dist2))
			{
				result = cur;
				dist2 = Utils.dist2(this, cur);
			}
		}
		return result;
	}

	public boolean sees(Creature other)
	{
		return sees(other.getX(), other.getY());
	}

    public boolean sees(Point point)
    {
        return sees(point.x, point.y);
    }

	public boolean sees(int x, int y)
	{
		return Utils.reach(getX(), getY(), x, y, VISION) && Utils.los(level.getMap(), getX(), getY(), x, y);
	}

	public int getAttribute(Attribute attribute)
	{
		return attributes.containsKey(attribute) ? attributes.get(attribute) : 0;
	}

	public void setAttribute(Attribute attribute, int value)
	{
		if (attributes.containsKey(attribute)) throw new RuntimeException("Attribute already set: " + attribute);
		attributes.put(attribute, value);
	}

	public void setAttributeGrowth(Attribute attribute, int value)
	{
		if (growth.containsKey(attribute)) throw new RuntimeException("Attribute growth already set: " + attribute);
		growth.put(attribute, value);
		points.put(attribute, 0);
	}

	public void addAttribute(Attribute attribute, int value)
	{
		MAIN.fine(getName() + " gets " + value + " " + attribute + ".");
		attributes.put(attribute, attributes.get(attribute) + value);
	}

	public Equipable getEquiped(EquipmentSlot slot)
	{
		return equipment.get(slot);
	}

	public void forceEquip(Equipable item)
	{
		item.getEquipmentStrategy().equip(this, item);
	}

	public void equip(Equipable item, EquipmentSlot slot)
	{
		Preconditions.checkArgument(getEquiped(slot) == null);
		for (Ability ability : item.getAbilities())
		{
			abilities.put(ability.getAbilityType(), ability);
		}
		MAIN.fine(getName() + " equips " + item.getName());
		equipment.put(slot, item);
	}

	public Equipable unequip(EquipmentSlot slot)
	{
		if (equipment.containsKey(slot))
		{
			Equipable item = equipment.remove(slot);
			for (Ability ability : item.getAbilities())
			{
				abilities.remove(ability.getAbilityType());
			}
			MAIN.fine(getName() + " unequips " + item.getName());
			return item;
		}
		return null;
	}

	protected void onKill(Creature target)
	{
		stat.inc(KILLS_MADE);
		MAIN.fine(getName() + " kills " + target.getName() + "!");
	}

	public void onMove()
	{
		coolDown.removeAll(newArrayList(filter(coolDown, new Predicate<AbilityType>()
        {
            public boolean apply(AbilityType abilityType)
            {
                return !abilityType.isLeaveOnMove();
            }
        })));
	}

	public int getExpValue()
	{
		return expValue + getRank();
	}

	public void setExpValue(int expValue)
	{
		this.expValue = expValue;
	}

	public void setFaction(int faction)
	{
		this.faction = faction;
	}

	public void setXY(Point loc)
	{
		this.xy = loc;
	}

	public Set<IEffect> getActiveEffects()
	{
		return activeEffects;
	}

	public void addEvent(TimeEvent event)
	{
		Preconditions.checkNotNull(eventHolder);
		eventHolder.addEvent(event);
	}

	public void setEventHolder(EventHolder eventHolder)
	{
		this.eventHolder = eventHolder;
	}

	public EventHolder getEventHolder()
	{
		return eventHolder;
	}

	public boolean isBoss()
	{
		return boss;
	}

	public void setBoss(boolean boss)
	{
		this.boss = boss;
	}

	public String getTemplateId()
	{
		return templateId;
	}

	public StatHolder getStat()
	{
		return stat;
	}

	public void raiseRank()
	{
		for (Attribute attr : Attribute.values())
		{
			int curPoints = points.get(attr);
			curPoints += growth.containsKey(attr) ? Mechanics.normal(growth.get(attr)) : 0;
			while (curPoints >= Mechanics.GROWTH_THRESHOLD)
			{
				addAttribute(attr, 1);
				curPoints -= Mechanics.GROWTH_THRESHOLD;
			}
			points.put(attr, curPoints);
		}
		setRank(getRank() + 1);
		MAIN.fine(getName() + " gets " + getRank() + " rank.");
	}

	public Creature register(LevelContext level, Point point, int faction)
	{
		Preconditions.checkNotNull(level);
		Preconditions.checkNotNull(point);

		this.level = level;
		setXY(point);
		setFaction(faction);
		level.addCreature(this);
		return this;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public Point getXY()
	{
		return xy;
	}

	@Override
	public String getImagePath()
	{
		return template.getImagePath();
	}

	@Override
	public String getId()
	{
		return template.getId();
	}

    public void activateAbility(AbilityType type)
    {
        Preconditions.checkNotNull(type);
        LOGGER.fine(format("%s was removed from cooldown for %s", type, getName()));
        coolDown.remove(type);
    }


}
