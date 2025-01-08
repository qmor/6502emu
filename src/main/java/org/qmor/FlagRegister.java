package org.qmor;

import java.util.EnumMap;
import java.util.Map;

public class FlagRegister
{
    private final Map<Flag, Boolean> fields = new EnumMap<>(Flag.class);
    public void setFlag(Flag flag, boolean value)
    {
        fields.put(flag, value);
    }
    public int getAsInt(Flag flag)
    {
        return Boolean.TRUE.equals(fields.get(flag)) ? 1 : 0;
    }
    public boolean getAsBoolean(Flag flag)
    {
        return fields.get(flag);
    }

    public void reset()
    {
        for (var v:Flag.values())
            fields.put(v, false);
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

}