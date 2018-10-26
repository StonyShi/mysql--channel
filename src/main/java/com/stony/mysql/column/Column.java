package com.stony.mysql.column;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * <p>mysql-x
 * <p>com.mysql
 *
 * @author stony
 * @version 上午11:29
 * @since 2018/10/22
 */
public interface Column<V> {
    V getValue();

    public static void main(String[] args) throws IOException {
        String[] columns = {"DECIMAL", "TINY", "SHORT", "LONG", "FLOAT", "DOUBLE", "NULL", "TIMESTAMP", "LONGLONG", "INT24", "DATE", "TIME", "DATETIME", "YEAR", "NEWDATE", "VARCHAR", "BIT", "TIMESTAMP2", "DATETIME2", "TIME2", "NEWDECIMAL", "ENUM", "SET", "TINY_BLOB", "MEDIUM_BLOB", "LONG_BLOB", "BLOB", "VAR_STRING", "STRING", "GEOMETRY" };

        File dir = new File("/Users/stony/IdeaProjects/mysql-x/src/main/java/com/stony/mysql/column");

//        System.out.println(dir.exists());
//
        for (String f : columns) {
            String className = toCamel(f) + "Column";
            File java = new File(dir, className + ".java");
            System.out.println(java.getAbsolutePath());
            if (!java.exists()) {
                FileWriter writer = new FileWriter(java);
//
//
                writer.write("package com.stony.mysql.column;\n");
                writer.write("\n");
                writer.write("/**\n");
                writer.write("* @author stony\n");
                writer.write("* @since 2018/10/22\n");
                writer.write("*/\n");
                writer.write("public class " + className + " implements Column{\n");
                writer.write("}");
                writer.close();
//            }
            }

        }

    }

    public static String toCamel(String str) {
        char[] array = str.toLowerCase().toCharArray();
        char[] arr2 = new char[array.length];
        int index = 1;
        arr2[0] = array[0];
        boolean next = false;
        for (int i = 1; i < array.length; i++) {
            char c = array[i];
            if(c == '_') {
                next = true;
            } else {
                if(next){
                    arr2[index++] = (char)(c-32);
                }else {
                    arr2[index++] = c;
                }
                next = false;

            }
        }
        if (arr2[0] >= 'a' && arr2[0] <= 'z') {
            arr2[0] -= 32;
        }
        return String.valueOf(Arrays.copyOf(arr2, index));
    }
}