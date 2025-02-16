package org.qmor;

import java.util.HashSet;
import java.util.Set;

public abstract class OpGroups {
   private OpGroups() {}
   protected static final Set<OpCodes> REG_A_LOAD_CODES = new HashSet<>();
   protected static final Set<OpCodes> REG_X_LOAD_CODES = new HashSet<>();
   protected static final Set<OpCodes> REG_Y_LOAD_CODES = new HashSet<>();
}
