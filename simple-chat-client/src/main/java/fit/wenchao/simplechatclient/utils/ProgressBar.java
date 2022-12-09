package fit.wenchao.simplechatclient.utils;

public class ProgressBar {

    // 进度条长度，一定不能超过100，最好大于20
    private final int PROGRESS_SIZE = 20;
    private int BITE = 100 / PROGRESS_SIZE;

    private String getNChar(int num, char ch) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(ch);
        }
        return builder.toString();
    }

    public String getProgressBar(String prompt, int progress) {
        String finish;
        String unFinish;
        finish = getNChar(progress / BITE, '█');
        unFinish = getNChar(PROGRESS_SIZE - progress / BITE, ' ');
        String target = String.format("%d%%[%s%s]", progress, finish, unFinish);
        return target;
    }


    public static void main(String[] args) {
    }
}
