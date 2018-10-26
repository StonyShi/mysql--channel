package com.stony.mysql.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 下午2:54
 * @see <a href="https://dev.mysql.com/doc/internals/en/character-set.html#packet-Protocol::CharacterSet">
 * CharacterSet
 * </a>
 * @since 2018/10/22
 */
public enum CharacterSet {

    BIG5("big5", "big5_chinese_ci", 1),
    DEC8("dec8", "dec8_swedish_ci", 3),
    CP850("cp850", "cp850_general_ci", 4),
    HP8("hp8", "hp8_english_ci", 6),
    KOI8R("koi8r", "koi8r_general_ci", 7),
    LATIN1("latin1", "latin1_swedish_ci", 8),
    LATIN2("latin2", "latin2_general_ci", 9),
    SWE7("swe7", "swe7_swedish_ci", 10),
    ASCII("ascii", "ascii_general_ci", 11),
    UJIS("ujis", "ujis_japanese_ci", 12),
    SJIS("sjis", "sjis_japanese_ci", 13),
    HEBREW("hebrew", "hebrew_general_ci", 16),
    TIS620("tis620", "tis620_thai_ci", 18),
    EUCKR("euckr", "euckr_korean_ci", 19),
    KOI8U("koi8u", "koi8u_general_ci", 22),
    GB2312("gb2312", "gb2312_chinese_ci", 24),
    GREEK("greek", "greek_general_ci", 25),
    CP1250("cp1250", "cp1250_general_ci", 26),
    GBK("gbk", "gbk_chinese_ci", 28),
    LATIN5("latin5", "latin5_turkish_ci", 30),
    ARMSCII8("armscii8", "armscii8_general_ci", 32),
    UTF8("utf8", "utf8_general_ci", 33),
    UCS2("ucs2", "ucs2_general_ci", 35),
    CP866("cp866", "cp866_general_ci", 36),
    KEYBCS2("keybcs2", "keybcs2_general_ci", 37),
    MACCE("macce", "macce_general_ci", 38),
    MACROMAN("macroman", "macroman_general_ci", 39),
    CP852("cp852", "cp852_general_ci", 40),
    LATIN7("latin7", "latin7_general_ci", 41),
    CP1251("cp1251", "cp1251_general_ci", 51),
    UTF16("utf16", "utf16_general_ci", 54),
    UTF16LE("utf16le", "utf16le_general_ci", 56),
    CP1256("cp1256", "cp1256_general_ci", 57),
    CP1257("cp1257", "cp1257_general_ci", 59),
    UTF32("utf32", "utf32_general_ci", 60),
    BINARY("binary", "binary", 63),
    GEOSTD8("geostd8", "geostd8_general_ci", 92),
    CP932("cp932", "cp932_japanese_ci", 95),
    EUCJPMS("eucjpms", "eucjpms_japanese_ci", 97),
    GB18030("gb18030", "gb18030_chinese_ci", 248),
    UTF8MB4("utf8mb4", "utf8mb4_0900_ai_ci", 255),;

    int id;
    String characterSetName;
    String collationName;
    CharacterSet(String characterSetName, String collationName, int id) {
        this.id = id;
        this.characterSetName = characterSetName;
        this.collationName = collationName;
    }

    private static final Map<Integer,CharacterSet> INDEX_BY_ID;
    static {
        INDEX_BY_ID = new HashMap<>(64);
        for (CharacterSet cs: CharacterSet.values()) {
            INDEX_BY_ID.put(cs.getId(), cs);
        }
    }
    public static CharacterSet byId(int id) {
        return INDEX_BY_ID.get(id);
    }

    public int getId() {
        return id;
    }

    public String getCharacterSetName() {
        return characterSetName;
    }

    public String getCollationName() {
        return collationName;
    }

    @Override
    public String toString() {
        return "(" +
                "id=" + id +
                ", characterSetName='" + characterSetName + '\'' +
                ", collationName='" + collationName + '\'' +
                ')';
    }
}