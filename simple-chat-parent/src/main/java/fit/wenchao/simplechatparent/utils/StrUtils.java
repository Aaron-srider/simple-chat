package fit.wenchao.simplechatparent.utils;

import java.io.File;

public class StrUtils {

    public static String ft(String format, Object... args) {
        return new StringFormatter().formatString(format, args);
    }

    /**
     * 如果参数只有一个，并且是数组，请使用asList将其转换为List，如果是多个数组，可以不用转换。
     *
     * @param format 格式化字符串，其中包含占位符<pre>"{}"</pre>，形式为：<pre>{@code "the result is:{}, expected is:{}"}</pre>
     * @param args   依次对应format模板中的占位符
     * @return 返回输出的字符串
     */
    public static String outf(String format, Object... args) {
        String resultString
                = new StringFormatter().formatString(format, args);
        System.out.println(resultString);
        return resultString;
    }

    public static class FilePathBuilder {
        StringBuilder sb;

        public FilePathBuilder() {
            this.sb = new StringBuilder();
        }

        public FilePathBuilder(String initPathStr) {
            String systemSpecificPath = convertSeparatorOfPath(initPathStr);
            this.sb = new StringBuilder(systemSpecificPath);
        }

        public FilePathBuilder ct(String rowPath) {

            String systemSpecificPath = convertSeparatorOfPath(rowPath);


            if (systemSpecificPath.startsWith(File.separator)) {
                for (int i = 0; i < systemSpecificPath.length(); i++) {
                    char c = systemSpecificPath.charAt(i);
                    if (String.valueOf(c).equals(File.separator)) {
                        systemSpecificPath = systemSpecificPath.substring(1);
                        continue;
                    }
                    break;
                }
            }

            if (sb.toString().endsWith(File.separator)) {
                for (int i = sb.toString().length() - 1; i >= 0; i--) {
                    char c = sb.toString().charAt(i);
                    if (String.valueOf(c).equals(File.separator)) {
                        sb = new StringBuilder(sb.substring(0, i));
                        continue;
                    }
                    break;
                }
            }

            sb.append(File.separator);

            sb.append(systemSpecificPath);
            return this;
        }

        private String convertSeparatorOfPath(String path) {
            String systemString = System.getProperty("os.name").toLowerCase();
            if (systemString.contains("windows")) {
                path = path.replace("/", File.separator);
            } else if (systemString.contains("linux") || systemString.contains("mac")) {
                path = path.replace("\\", File.separator);
            }
            return path;
        }

        public String build() {
            String path = sb.toString();
            if (path.endsWith(File.separator)) {
                while (path.endsWith(File.separator)) {
                    path = path.substring(0, path.length() - 1);
                }
            }
            return path;
        }
    }

    public static FilePathBuilder filePathBuilder(String initPathStr) {
        return new FilePathBuilder(initPathStr);
    }

    public static FilePathBuilder filePathBuilder() {
        return filePathBuilder("");
    }

    public static void main(String[] args)  {

    }


}