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
    JSR(0x20,NO_AFFECTS,6,AddressMode.ABSOLUTE),
    RTS(0x60,NO_AFFECTS,6,AddressMode.NONE),

    JMP_ABSOLUTE(0x4C,NO_AFFECTS,3,AddressMode.ABSOLUTE),
    JMP_INDIRECT(0x6C,NO_AFFECTS,5,AddressMode.INDIRECT),

    LDA_IM(0xa9, ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES, AddressMode.IMMEDIATE),
    LDA_ZP(0x5a, ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDA_ZP_X(0xB5, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES, AddressMode.ZERO_PAGE_X),
    LDA_ABSOLUTE(0xAD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES, AddressMode.ABSOLUTE),
    LDA_ABSOLUTE_X(0xBD, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_X),
    LDA_ABSOLUTE_Y(0xB9, ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.ABSOLUTE_Y),
    LDA_INDIRECT_X(0xA1, ZFIFZERO_NFIFNEG,6,REG_A_LOAD_CODES,AddressMode.INDIRECT_X),
    LDA_INDIRECT_Y(0xB1, ZFIFZERO_NFIFNEG,5,REG_A_LOAD_CODES,AddressMode.INDIRECT_Y),

    LDX_IM(0xA2,ZFIFZERO_NFIFNEG,2,REG_X_LOAD_CODES, AddressMode.IMMEDIATE),
    LDX_ZP(0xA6,ZFIFZERO_NFIFNEG,3,REG_X_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDX_ZP_Y(0xB6,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ZERO_PAGE_Y),
    LDX_ABSOLUTE(0xAE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ABSOLUTE),
    LDX_ABSOLUTE_Y(0xBE,ZFIFZERO_NFIFNEG,4,REG_X_LOAD_CODES,AddressMode.ABSOLUTE_Y),

    LDY_IM(0xA0,ZFIFZERO_NFIFNEG,2,REG_Y_LOAD_CODES, AddressMode.IMMEDIATE),
    LDY_ZP(0xA4,ZFIFZERO_NFIFNEG,3,REG_Y_LOAD_CODES, AddressMode.ZERO_PAGE),
    LDY_ZP_X(0xB4,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ZERO_PAGE_X),
    LDY_ABSOLUTE(0xAC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ABSOLUTE),
    LDY_ABSOLUTE_X(0xBC,ZFIFZERO_NFIFNEG,4,REG_Y_LOAD_CODES,AddressMode.ABSOLUTE_X),

    TAX(0xAA,ZFIFZERO_NFIFNEG, 2,REG_X_LOAD_CODES,AddressMode.NONE),
    TXA(0x8A,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES,AddressMode.NONE),
    TAY(0xA8,ZFIFZERO_NFIFNEG, 2,REG_Y_LOAD_CODES,AddressMode.NONE),
    TYA(0x98,ZFIFZERO_NFIFNEG, 2,REG_A_LOAD_CODES,AddressMode.NONE),


    STA_ZP(0x85,NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STA_ZP_X(0x95,NO_AFFECTS,4,AddressMode.ZERO_PAGE_X),
    STA_ABSOLUTE(0x8D,NO_AFFECTS,4,AddressMode.ABSOLUTE),
    STA_ABSOLUTE_X(0x9D,NO_AFFECTS,5,AddressMode.ABSOLUTE_X),
    STA_ABSOLUTE_Y(0x99,NO_AFFECTS,5,AddressMode.ABSOLUTE_Y),
    STA_INDIRECT_X(0x81,NO_AFFECTS,6,AddressMode.INDIRECT_X),
    STA_INDIRECT_Y(0x91,NO_AFFECTS,6,AddressMode.INDIRECT_Y),

    STX_ZP(0x86, NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STX_ZP_Y(0x96, NO_AFFECTS,4,AddressMode.ZERO_PAGE_Y),
    STX_ABSOLUTE(0x8e, NO_AFFECTS,4,AddressMode.ABSOLUTE),

    STY_ZP(0x84, NO_AFFECTS,3,AddressMode.ZERO_PAGE),
    STY_ZP_X(0x94, NO_AFFECTS,4,AddressMode.ZERO_PAGE_X),
    STY_ABSOLUTE(0x8c, NO_AFFECTS,4,AddressMode.ABSOLUTE),

    TSX(0xBA,ZFIFZERO_NFIFNEG,2, REG_X_LOAD_CODES,AddressMode.NONE),
    TXS(0x9A,NO_AFFECTS,2,AddressMode.NONE),
    PHA(0x48,NO_AFFECTS,3,AddressMode.NONE),
    PHP(0x08,NO_AFFECTS,3,AddressMode.NONE),
    PLA(0x64,ZFIFZERO_NFIFNEG,4,REG_A_LOAD_CODES,AddressMode.NONE),
    PLP(0x28,NO_AFFECTS, 4,AddressMode.NONE),


    AND_IM(0x29,ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    AND_ZP(0x25,ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),

    OR_IM(0x9,ZFIFZERO_NFIFNEG,2,REG_A_LOAD_CODES,AddressMode.IMMEDIATE),
    OR_ZP(0x5,ZFIFZERO_NFIFNEG,3,REG_A_LOAD_CODES,AddressMode.ZERO_PAGE),

    NOP(0xEA,NO_AFFECTS,2,AddressMode.NONE);

    private final int opcode;
    private final List<BiFunction<FlagRegister, Short, FlagRegister>> functions;
    private final int cycles;
    private final AddressMode addressMode;

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles, AddressMode addressMode) {
        this.opcode = opcode;
        this.functions = functions;
        this.cycles = cycles;
        this.addressMode = addressMode;
    }

    OpCodes(int opcode, List<BiFunction<FlagRegister, Short, FlagRegister>> functions, int cycles, Set<OpCodes> group, AddressMode addressMode) {
        this(opcode, functions, cycles,addressMode);
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
