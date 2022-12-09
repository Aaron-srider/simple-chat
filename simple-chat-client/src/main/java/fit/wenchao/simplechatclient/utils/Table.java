package fit.wenchao.simplechatclient.utils;

import fit.wenchao.simplechatparent.utils.SyncPrinterHelper;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class Table<T> {

    public static final String WHITE = " ";

    List<T> list = new ArrayList<>();

    String[] headList = new String[0];


    int[] gapList = new int[0];

    int[] widthList = new int[0];

    List<CustomPrintCell<T>> customPrintCells = new ArrayList<>();

    public void setCustomColPrintPolicy(List<CustomPrintCell<T>> customPrintCells) {
        this.customPrintCells = customPrintCells;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setHeadList(String[] headList) {
        this.headList = headList;
    }

    public void setGapList(int[] gapList) {
        this.gapList = gapList;
    }

    public void setWidthList(int[] widthList) {
        this.widthList = widthList;
    }

    public static <T> String getAttrFromEntity(T entity, String attrName) {
        Class<?> aClass = entity.getClass();

        Field declaredField = null;
        try {
            declaredField = aClass.getDeclaredField(attrName);
            declaredField.setAccessible(true);
            Object result = declaredField.get(entity);
            return result.toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getNChar(int num, char ch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public void print(SyncPrinterHelper.Printer printer) {
        printHead(headList,
                widthList,
                gapList, printer);
        printer.println();

        for (int i = 0; i < this.list.size(); i++) {
            printRow(i,
                    headList,
                    widthList,
                    gapList,
                    customPrintCells, printer);
            printer.println();
        }
    }

    @Data
    public static class CustomPrintCell<T> {
        Function<T, String> custom;
        String colName;
    }

    public void printRow(int idx, String[] titles, int[] cellWidths, int[] gaps,
                         List<CustomPrintCell<T>> customPrintCellList, SyncPrinterHelper.Printer printer) {

        String[] values = new String[titles.length];
        for (int i = 0; i < titles.length; i++) {

            String title = titles[i];
            String value;
            T tableItem = this.list.get(idx);
            if (ifCustom(title, customPrintCellList)) {
                CustomPrintCell<T> customPrintCell = getCustom(title, customPrintCellList);
                value = customPrintCell.getCustom().apply(tableItem);
            }
            else {
                value = getAttrFromEntity(tableItem, title);
            }
            values[i] = value;
        }


        Queue<Integer> widthQueue = new LinkedList<>();
        for (int width : cellWidths) {
            widthQueue.add(width);
        }

        Queue<String> valueQueue = new LinkedList<>();
        for (String value : values) {
            valueQueue.add(value);
        }

        Queue<Integer> gapQueue = new LinkedList<>();
        for (int gap : gaps) {
            gapQueue.add(gap);
        }

        while (!valueQueue.isEmpty()) {
            String title = valueQueue.poll();
            int gap;

            if (!gapQueue.isEmpty()) {
                gap = gapQueue.poll();

            }
            else {
                gap = 3;
            }

            if (widthQueue.isEmpty()) {
                printer.print(title);
                printer.print(getNChar(3, WHITE.charAt(0)));
            }
            else {
                Integer width = widthQueue.poll();

                if (title.length() > width) {
                    title = title.substring(0, Math.max(width - 3, 0));
                    if (width < 3) {
                        for (Integer i = 0; i < width; i++) {
                            title = title + ".";
                        }
                    }
                    else {
                        title = title + "...";
                    }
                }
                else {
                    int delt = width - title.length();
                    for (int i = 0; i < delt; i++) {
                        title += WHITE;
                    }
                }
                printer.print(title);
                printer.print(getNChar(gap, WHITE.charAt(0)));

            }
        }


        //System.out.print("id      uuid    process     path");
    }

    private CustomPrintCell<T> getCustom(String title, List<CustomPrintCell<T>> customPrintCellList) {
        return customPrintCellList.stream().filter((customPrintCell -> {
            return customPrintCell.getColName().equals(title);
        })).findFirst().orElse(null);
    }

    private boolean ifCustom(String title, List<CustomPrintCell<T>> customPrintCellList) {
        for (CustomPrintCell customPrintCell : customPrintCellList) {
            String colName = customPrintCell.getColName();
            if (colName.equals(title)) {
                return true;
            }
        }
        return false;
    }

    public void printHead(String[] titles, int[] cellWidths, int[] gaps, SyncPrinterHelper.Printer printer) {

        Queue<Integer> widthQueue = new LinkedList<>();
        for (int width : cellWidths) {
            widthQueue.add(width);
        }

        Queue<String> titleQueue = new LinkedList<>();
        for (String title : titles) {
            titleQueue.add(title);
        }

        Queue<Integer> gapQueue = new LinkedList<>();
        for (int gap : gaps) {
            gapQueue.add(gap);
        }

        while (!titleQueue.isEmpty()) {
            String title = titleQueue.poll();
            int gap;

            if (!gapQueue.isEmpty()) {
                gap = gapQueue.poll();

            }
            else {
                gap = 3;
            }

            if (widthQueue.isEmpty()) {
                printer.print(title);
                printer.print(getNChar(3, WHITE.charAt(0)));
            }
            else {
                Integer width = widthQueue.poll();

                if (title.length() > width) {
                    title = title.substring(0, Math.max(width - 3, 0));
                    if (width < 3) {
                        for (Integer i = 0; i < width; i++) {
                            title = title + ".";
                        }
                    }
                    else {
                        title = title + "...";
                    }
                }
                else {
                    int delt = width - title.length();
                    for (int i = 0; i < delt; i++) {
                        title += WHITE;
                    }
                }
                printer.print(title);
                printer.print(getNChar(gap, WHITE.charAt(0)));

            }
        }


    }

    public static void main(String[] args) {

        RecvTaskListItem recvTaskListItem = new RecvTaskListItem();

        String uuid = UUID.randomUUID().toString();
        String tempPath = "/path/test/testfile" + uuid;

        recvTaskListItem
                .setId(111)
                .setUuid(uuid)
                .setTempPath(tempPath)
                .setProgress(79)
        ;
        //recvTaskListItem.print();
        List<RecvTaskListItem> list = new ArrayList<>();
        list.add(recvTaskListItem);
        list.add(new RecvTaskListItem()
                .setId(172).setUuid(UUID.randomUUID().toString())
                .setProgress(33).setTempPath("/path/test/testfile/ghsdfkjlhaljshdfj"));


        CustomPrintCell<RecvTaskListItem> customPrintCell = new CustomPrintCell<>();
        customPrintCell.setCustom((recv) -> {
            String progress = getAttrFromEntity(recv, "progress");
            return new ProgressBar().getProgressBar("", Integer.parseInt(progress));
        });
        customPrintCell.setColName("progress");
        List<CustomPrintCell<RecvTaskListItem>> customPrintCells = new ArrayList<>();
        customPrintCells.add(customPrintCell);

        Table<RecvTaskListItem> table = new Table<>();
        table.setGapList(new int[]{4, 4, 4});
        table.setWidthList(new int[]{3, 36, 26});
        table.setHeadList(new String[]{"id", "uuid", "progress", "tempPath"});
        table.setList(list);
        table.setCustomColPrintPolicy(customPrintCells);

        SyncPrinterHelper.Printer printer = SyncPrinterHelper.getSingleton().lock();
        table.print(printer);
        SyncPrinterHelper.getSingleton().unlock();

    }


}
