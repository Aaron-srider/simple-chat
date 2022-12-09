package fit.wenchao.simplechatparent.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.MessageFormat;
import java.util.*;

public class StringFormatter {

    public static <T> ArrayWrapper<T> arr(T[] arr) {
        return new ArrayWrapper<T>(arr);
    }

    private static class ArrayWrapper<T> {

        T[] arr;

        public ArrayWrapper(T[] arr) {
            this.arr = arr;
        }

        public T[] getArr() {
            return arr;
        }
    }

    public String formatString(String format, Object... args) {

        //将format字符串转换成messageFormat能处理的格式
        String formatStrCanBeProcessedByMessageFormat = transRowFormatStrToTheOneCanBeProcessedByMsgFormat(format);

        unpackArrayWrapper(args);

        //将所有参数替换成String，包括数组
        String[] stringDescForArgs = transEachElemToString(args);

        return doFormatStringWithPlaceholder(formatStrCanBeProcessedByMessageFormat, stringDescForArgs);
    }

    private void unpackArrayWrapper(Object[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                Object e = args[i];
                if (e instanceof ArrayWrapper) {
                    Object[] arr = ((ArrayWrapper<?>) e).getArr();
                    args[i] = arr;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String doFormatStringWithPlaceholder(String formatCanBeProcessedByMsgFormat, Object... args) {
        MessageFormat messageFormat = new MessageFormat(formatCanBeProcessedByMsgFormat);
        String rst = messageFormat.format(args);
        return rst;
    }

    @Data
    @AllArgsConstructor
    static class Entry {
        Integer k;
        Integer v;
    }

    public String transRowFormatStrToTheOneCanBeProcessedByMsgFormat(String rowFormat) {
        if (rowFormat == null) {
            return "null";
        }
        StringBuilder formatCanBeProcessedByMsgFormat = new StringBuilder("");

        List<Entry> singleQuotesEntryList = new ArrayList<>();

        Map<Integer, Integer> singleQuotesIndexMap = new HashMap<>();

        int countOfBraces = 0;

        for (int i = 0; i < rowFormat.length(); i++) {
            int idx = i;
            char curChar = rowFormat.charAt(i);

            //如果当前的字符是单引号，则找到与之匹配的下一个，并将这对单引号存储起来
            findAndStorePairQuotes(idx, rowFormat, singleQuotesIndexMap, singleQuotesEntryList);
            //如果找到{}，则在中间填写序号count
            if ('{' == curChar && '}' == rowFormat.charAt(idx + 1)) {
                if (!ifCurCharBetweenQuotes(idx, singleQuotesEntryList)) {
                    formatCanBeProcessedByMsgFormat.append("{");
                    formatCanBeProcessedByMsgFormat.append(countOfBraces++);
                } else {
                    formatCanBeProcessedByMsgFormat.append("{");
                }
            } else {
                formatCanBeProcessedByMsgFormat.append(curChar);
            }
        }
        return formatCanBeProcessedByMsgFormat.toString();
    }

    static final int SINGLE_QUOTE = 39;
    private static void findAndStorePairQuotes(int currentIdx, String format, Map<Integer, Integer> singleQuotesIndexMap, List<Entry> singleQuotesEntryList) {
        if (SINGLE_QUOTE == format.charAt(currentIdx)) {
            if (!singleQuotesIndexMap.containsKey(currentIdx) && !singleQuotesIndexMap.containsValue(currentIdx)) {
                boolean ifFoundAnotherSingleQuoteAfterExistsOne = false;

                for (int anotherSingleQuoteIndex = currentIdx + 1; anotherSingleQuoteIndex < format.length(); anotherSingleQuoteIndex++) {
                    if (format.charAt(anotherSingleQuoteIndex) == SINGLE_QUOTE) {
                        ifFoundAnotherSingleQuoteAfterExistsOne = true;
                        singleQuotesIndexMap.put(currentIdx, anotherSingleQuoteIndex);
                        singleQuotesEntryList.add(new Entry(currentIdx, anotherSingleQuoteIndex));
                        break;
                    }

                }

                if (!ifFoundAnotherSingleQuoteAfterExistsOne) {
                    //没有匹配的单引号，用null表示
                    singleQuotesEntryList.add(new Entry(currentIdx, null));
                    singleQuotesIndexMap.put(currentIdx, null);
                }

            }
        }
    }


    private static boolean ifCurCharBetweenQuotes(int idx, List<Entry> quotes) {

        if (quotes.isEmpty()) {
            return false;
        }

        Entry lastEntry = quotes.get(quotes.size() - 1);

        return idx > lastEntry.getK() &&
                (lastEntry.getV() == null || idx + 1 < lastEntry.getV());
    }

    public static String[] transEachElemToString(Object[] args) {
        String[] stringDescForArgs = new String[args.length];
        try {
            for (int i = 0; i < args.length; i++) {
                Object itm = args[i];
                String s = null;
                if (itm == null) {
                    s = "null";
                } else if (itm.getClass().isArray()) {
                    s = convertArrayToString(itm);
                } else {
                    s = itm.toString();
                }
                //args[idx] = s;
                stringDescForArgs[i] = s;
            }
            return stringDescForArgs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String convertArrayToString(Object arg) {
        String s = null;
        if (arg instanceof long[]) {
            s = Arrays.toString((long[]) arg);
        } else if (arg instanceof int[]) {
            s = Arrays.toString((int[]) arg);
        } else if (arg instanceof short[]) {
            s = Arrays.toString((short[]) arg);
        } else if (arg instanceof char[]) {
            s = Arrays.toString((char[]) arg);
        } else if (arg instanceof byte[]) {
            s = Arrays.toString((byte[]) arg);
        } else if (arg instanceof boolean[]) {
            s = Arrays.toString((boolean[]) arg);
        } else if (arg instanceof float[]) {
            s = Arrays.toString((float[]) arg);
        } else if (arg instanceof double[]) {
            s = Arrays.toString((double[]) arg);
        } else if (arg instanceof Object[]) {
            s = Arrays.toString((Object[]) arg);
        }
        return s;
    }
}
