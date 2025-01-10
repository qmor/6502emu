package org.qmor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.BiFunction;

import static org.qmor.OpFunctions.*;

@RequiredArgsConstructor
@Getter
public enum OpCodes {
    LDA_IM(0xa9, ZFIFZERO_NFIFNEG,2),
    LDA_ZP(0x5a, ZFIFZERO_NFIFNEG,3),
    LDA_ZP_X(0xB5, ZFIFZERO_NFIFNEG,4),

    LDX_IM(0xA2,ZFIFZERO_NFIFNEG,2);
    private final int opcode;
    private final List<BiFunction<FlagRegister, Short, FlagRegister>> functions;
    private final int cycles;

    public static OpCodes fromByte(int opcode) {
        for (var b: OpCodes.values()) {
            if (opcode == b.opcode) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown opcode %s" + "%02X".formatted(opcode));
    }
}
