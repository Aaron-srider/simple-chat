package fit.wenchao.simplechatclient.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecvTaskListItem {
    private String uuid;
    private String tempPath;
    private int id;
    private int progress;
    private static String space = "";

    static {
        for (int i = 0; i < 4; i++) {
            space += " ";
        }
    }


    private void printSpace() {
        System.out.print(space);
    }



    public static void main(String[] args) {
        RecvTaskListItem recvTaskListItem = new RecvTaskListItem();

        String uuid = UUID.randomUUID().toString();
        String tempPath = "/path/test/testfile" + uuid;

        recvTaskListItem
                .setId(1)
                .setUuid(uuid)
                .setTempPath(tempPath);
    }
}