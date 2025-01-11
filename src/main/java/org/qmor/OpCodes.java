package org.qmor;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static org.qmor.OpFunctions.NO_AFFECTS;
import static org.qmor.OpFunctions.ZFIFZERO_NFIFNEG;
import static org.qmor.OpGroups.REG_A_LOAD_CODES;
import static org.qmor.OpGroups.REG_X_LOAD_CODES;


@Getter

public enum OpCodes {
    JSR(0x20,NO_AFFECTS,6),
    RTS(0x60,NO_AFFECTS,6),

    LDA_IM(0xa9, ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES),
    LDA_ZP(0x5a, ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES),
    LDA_ZP_X(0xB5, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_ABSOLUTE(0xAD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_ABSOLUTE_X(0xBD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),

    LDX_IM(0xA2,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES),
    LDX_ZP(0xA6,ZFIFZERO_NFIFNEG,3,REG_X_LOAD_CODES),
    LDX_ZP_Y(0xB6,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES),
    LDX_ABSOLUTE(0xAE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES),

    NOP(0xEA,NO_AFFECTS,2);

    private final int opcode;
    private final List<BiFunction<FlagRegister, Short, FlagRegister>> functions;
    private final int cycles;

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles) {
        this.opcode = opcode;
        this.functions = functions;
        this.cycles = cycles;
    }

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles, Set<OpCodes> group) {
        this(opcode, functions, cycles);
        Optional.ofNullable(group).ifPresent(e->e.add(this));
    }




    public static OpCodes fromByte(int opcode) {
        for (var b: OpCodes.values()) {
            if (opcode == b.opcode) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown opcode %s" + "%02X".formatted(opcode));
    }


}
