//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.okworx.ilcd.validation.common;

public enum ExchangeDirection {
    INPUT("Input"),
    OUTPUT("Output");

    private final String value;

    private ExchangeDirection(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ExchangeDirection fromValue(String v) {
        ExchangeDirection[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ExchangeDirection c = var1[var3];
            if (c.value.equals(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }

    public String getValue() {
        return this.value;
    }
}
