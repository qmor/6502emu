package org.qmor;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static org.qmor.OpFunctions.NO_AFFECTS;
import static org.qmor.OpFunctions.ZFIFZERO_NFIFNEG;
import static org.qmor.OpGroups.*;


@Getter

public enum OpCodes {
    JSR(0x20,NO_AFFECTS,6),
    RTS(0x60,NO_AFFECTS,6),

    JMP_ABSOLUTE(0x4C,NO_AFFECTS,3),
    JMP_INDIRECT(0x6C,NO_AFFECTS,5),

    LDA_IM(0xa9, ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES),
    LDA_ZP(0x5a, ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES),
    LDA_ZP_X(0xB5, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_ABSOLUTE(0xAD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_ABSOLUTE_X(0xBD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_ABSOLUTE_Y(0xB9, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    LDA_INDIRECT_X(0xA1, ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES),
    LDA_INDIRECT_Y(0xB1, ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES),

    LDX_IM(0xA2,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES),
    LDX_ZP(0xA6,ZFIFZERO_NFIFNEG,3,REG_X_LOAD_CODES),
    LDX_ZP_Y(0xB6,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES),
    LDX_ABSOLUTE(0xAE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES),
    LDX_ABSOLUTE_Y(0xBE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES),

    LDY_IM(0xA0,ZFIFZERO_NFIFNEG,2,REG_Y_LOAD_CODES),
    LDY_ZP(0xA4,ZFIFZERO_NFIFNEG,3,REG_Y_LOAD_CODES),
    LDY_ZP_X(0xB4,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES),
    LDY_ABSOLUTE(0xAC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES),
    LDY_ABSOLUTE_X(0xBC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES),

    TAX(0xAA,ZFIFZERO_NFIFNEG, 2,REG_X_LOAD_CODES),
    TXA(0x8A,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES),
    TAY(0xA8,ZFIFZERO_NFIFNEG, 2,REG_Y_LOAD_CODES),
    TYA(0x98,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES),


    STA_ZP(0x85,NO_AFFECTS,3),
    STA_ZP_X(0x95,NO_AFFECTS,4),
    STA_ABSOLUTE(0x8D,NO_AFFECTS,4),
    STA_ABSOLUTE_X(0x9D,NO_AFFECTS,5),
    STA_ABSOLUTE_Y(0x99,NO_AFFECTS,5),
    STA_INDIRECT_X(0x81,NO_AFFECTS,6),
    STA_INDIRECT_Y(0x91,NO_AFFECTS,6),

    STX_ZP(0x86, NO_AFFECTS,3),
    STX_ZP_Y(0x96, NO_AFFECTS,4),
    STX_ABSOLUTE(0x8e, NO_AFFECTS,4),

    STY_ZP(0x84, NO_AFFECTS,3),
    STY_ZP_X(0x94, NO_AFFECTS,4),
    STY_ABSOLUTE(0x8c, NO_AFFECTS,4),

    TSX(0xBA,ZFIFZERO_NFIFNEG,2, REG_X_LOAD_CODES),
    TXS(0x9A,NO_AFFECTS,2),
    PHA(0x48,NO_AFFECTS,3),
    PHP(0x08,NO_AFFECTS,3),
    PLA(0x64,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES),
    PLP(0x28,NO_AFFECTS, 4),

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
