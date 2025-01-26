package org.qmor;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode
public class FlagRegister
{
    private final Set<Flag> fields = EnumSet.noneOf(Flag.class);

    public FlagRegister getCopy()
    {
        final var newF = new FlagRegister();
        fields.forEach(newF::setFlag);
        return newF;
    }

    public String printFlags()
    {
        final List<String> r = new ArrayList<>();
        fields.forEach(field -> r.add(field.toString()));

        return String.join(",",r);
    }
    public void setFlag(Flag flag, boolean value)
    {
        if (value)
            setFlag(flag);
        else
            clearFlag(flag);
    }
    public void setFlag(Flag flag)
    {
        fields.add(flag);
    }
    public void clearFlag(Flag flag)
    {
        fields.remove(flag);
    }
    public int getAsInt(Flag flag)
    {
        return fields.contains(flag)?1:0;
    }
    public boolean getAsBoolean(Flag flag)
    {
        return fields.contains(flag);
    }

    public void reset()
    {
        fields.clear();
    }
    public short getByteValue()
    {
        int res = 0;
        for (var r: Flag.values())
        {
            res = res | (getAsInt(r) << r.getOffset());
        }
        return (short)res;
    }
    public void setByteValue(short val)
    {
        fields.clear();
        for (var flag: Flag.values())
        {
            if (((val>>flag.getOffset())&1) == 1)
                setFlag(flag, true);
        }
    }

    @Override
    public String toString() {
        return "[%s]".formatted(printFlags());
    }
}