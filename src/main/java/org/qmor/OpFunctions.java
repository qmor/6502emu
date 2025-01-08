package org.qmor;

import java.util.List;
import java.util.function.BiFunction;

public class OpFunctions {
        private OpFunctions(){}

    public static final BiFunction<FlagRegister, Short, FlagRegister> ZeroFlagIfZeroValue = (reg, val) ->
    {
        reg.setFlag(Flag.Z,val == 0);
        return reg;
    };

    public static final BiFunction<FlagRegister, Short, FlagRegister> NegFlagIf7BitRaised = (reg, val) ->
    {
        reg.setFlag(Flag.N,(val &0x80 )== 0x80);
        return reg;
    };

    /**
     * Flag 	         New value
     * Z - Zero 	    result == 0
     * N - Negative 	result bit 7
     */
    public static final List<BiFunction<FlagRegister, Short, FlagRegister>> ZFIFZERO_NFIFNEG = List.of(ZeroFlagIfZeroValue,NegFlagIf7BitRaised);
}
