package com.stony.mysql.event;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午2:59
 * @since 2018/10/18
 */
public class WrE {

    static String toLine(String str) {
        char[] array = str.toCharArray();
        char[] arr2 = new char[array.length*2];
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if ('A' <= c && c <= 'Z') {
                array[i] = '_';
                if(index > 0) {
                    arr2[index++] = '_';
                }
                arr2[index++] = c;
            } else {
                arr2[index++] = c;
            }
        }
        return String.valueOf(Arrays.copyOf(arr2, index));
    }


    public static void main(String[] args) throws IOException {
        String[] fs = {"StartEventV3", "QueryEvent", "StopEvent", "RotateEvent", "IntvarEvent", "LoadEvent", "SlaveEvent", "CreateFileEvent", "AppendBlockEvent", "ExecLoadEvent", "DeleteFileEvent", "NewLoadEvent", "RandEvent", "UserVarEvent", "FormatDescriptionEvent", "XidEvent", "BeginLoadQueryEvent", "ExecuteLoadQueryEvent", "TableMapEvent", "DeleteRowsEventv0", "UpdateRowsEventv0", "WriteRowsEventv0", "DeleteRowsEventv1", "UpdateRowsEventv1", "WriteRowsEventv1", "IncidentEvent", "HeartbeatEvent", "DeleteRowsEventv2", "UpdateRowsEventv2", "WriteRowsEventv2" };

        File dir = new File("/Users/stony/IdeaProjects/mysql-x/src/main/java/com/stony/mysql/event");

        System.out.println(dir.exists());

        for (String f: fs){
            System.out.println("} else if ("+toLine(f).toUpperCase()+" == header.eventType) {");
            System.out.println("\tBinlogEvent binlogEvent = new BinlogEvent(header, process"+f+"(byteBuffer));");
            System.out.println("\tbinlogEvent.code = head;");
            System.out.println("\tbinlogEvent.checksum = checksum;");
            System.out.println("\treturn binlogEvent;");
        }
//        for (String f: fs){
//            String m = "process" + f;
//            System.out.println("private "+f+" process"+f+"(LittleByteBuffer byteBuffer) {");
//            System.out.println("\t"+f+" event = new "+f+"();");
//            System.out.println("\treturn event;");
//            System.out.println("}");
//        }
//       for (String f: fs){
//           File java = new File(dir, f+".java");
////           System.out.println(java.getAbsolutePath());
//           if(!java.exists()){
//               FileWriter writer = new FileWriter(java);
//
//
//               writer.write("package com.stony.mysql.event;\n");
//               writer.write("\n");
//               writer.write("/**\n");
//               writer.write("* @author stony\n");
//               writer.write("* @since 2018/10/18\n");
//               writer.write("*/\n");
//               writer.write("public class "+f+" implements BinlogEvent.Event{\n");
//               writer.write("}");
//               writer.close();
//           }
//       }



    }
}
