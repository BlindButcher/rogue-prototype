package creature;

import com.google.common.collect.Maps;

import java.util.Map;

public enum Statistics
{
    HITS_MADE("Attacks made"), HITS_MISS("Attacks missed"), ATTACK_PARRIED("Attacks parried"),
    HITS_BLOCK("Attacks blocked"), DAMAGE_DONE("Damage done"),
    KILLS_MADE("Kills made"), DAMAGE_RECEIVED("Damage received");

    public String desc;

    private Statistics(String desc)
    {
        this.desc = desc;
    }

    public static Map<Statistics, Integer> prepare()
    {
        Map<Statistics, Integer> map = Maps.newHashMap();
        for (Statistics type : Statistics.values())
        {
            map.put(type, 0);
        }
        return map;
    }
}
