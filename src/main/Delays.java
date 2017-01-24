package main;

/**
 * Contains delays enum
 *
 * @author Blind
 */
public enum Delays
{
    DEFAULT_DELAY(10),
    MIN_ATTACK_DELAY(3),
    ZERO_DELAY(0),
    MIN_DELAY(1);

    public int amount;

    private Delays(int amount)
    {
        this.amount = amount;
    }
}
