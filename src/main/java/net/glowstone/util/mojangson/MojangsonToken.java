package net.glowstone.util.mojangson;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MojangsonToken {

    COMPOUND_START(0, "Compound_Start", '{'),
    COMPOUND_END(1, "Compound_End", '}'),
    ELEMENT_SEPERATOR(2, "Element_Seperator", ','),
    ARRAY_START(3, "Array_Start", '['),
    ARRAY_END(4, "Array_End", ']'),
    ELEMENT_PAIR_SEPERATOR(5, "Pair_Seperator", ':'),

    STRING_QUOTES(6, "String_Quotes", '\"'),
    DOUBLE_SUFFIX(8, "Double_Suffix", 'd'),
    BYTE_SUFFIX(9, "Byte_Suffix", 'b'),
    FLOAT_SUFFIX(10, "Float_Suffix", 'f'),
    SHORT_SUFFIX(11, "Short_Suffix", 's'),
    LONG_SUFFIX(12, "Long_Suffix", 'l'),

    WHITE_SPACE(13, "WhiteSpace", ' ');

    private final int id;
    private final String name;
    private final char symbol;

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
