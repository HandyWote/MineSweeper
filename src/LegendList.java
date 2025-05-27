import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 英雄榜记录管理类
 * 负责管理和存储不同难度级别的游戏记录，包括玩家名称和完成时间。
 * 记录按照完成时间升序排序，存储在对应难度的文本文件中。
 */
public class LegendList {
    /**
     * 记录文件名前缀，用于构建不同难度的记录文件名
     */
    private static final String FILE_PREFIX = "LegendList_";
    
    /**
     * 存储记录文件的目录名
     */
    private static final String LEADERBOARD_DIR = "LegendLists";

    /**
     * 确保记录文件存储目录存在
     * 如果目录不存在，则创建该目录
     */
    private static void ensureDirectoryExists() {
        File dir = new File(LEADERBOARD_DIR);
        if (!dir.exists()) {
            dir.mkdir(); // 创建目录
        }
    }

    /**
     * 获取当前难度级别对应的记录文件路径
     * 文件名格式为：LegendList_行数x列数_地雷数.txt
     * 
     * @return 当前难度的记录文件完整路径
     */
    private static String getFilePath() {
        ensureDirectoryExists();
        return LEADERBOARD_DIR + File.separator + FILE_PREFIX + MineSweeper.ROWS + "x" + MineSweeper.COLS + "_" + MineSweeper.MINES + ".txt";
    }

    /**
     * 添加新的游戏记录到对应难度的记录文件中
     * 
     * @param playerName 玩家名称
     * @param time 完成游戏所用的时间（秒）
     */
    public static void addRecord(String playerName, int time) {
        ensureDirectoryExists();
        try (PrintWriter out = new PrintWriter(new FileWriter(getFilePath(), true))) {
            out.println(playerName + "," + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前难度级别的所有记录并按时间升序排序
     * 
     * @return 排序后的记录列表，如果没有记录则返回空列表
     */
    public static List<Record> getRecords() {
        ensureDirectoryExists();
        List<Record> records = new ArrayList<>();
        File file = new File(getFilePath());

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        records.add(new Record(parts[0], Integer.parseInt(parts[1])));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 按时间升序排序
        Collections.sort(records, Comparator.comparingInt(Record::getTime));
        return records;
    }

    /**
     * 记录类，表示一条游戏记录
     * 包含玩家名称和完成时间两个属性
     */
    public static class Record {
        /**
         * 玩家名称
         */
        private final String playerName;
        
        /**
         * 完成游戏所用的时间（秒）
         */
        private final int time;

        /**
         * 创建一条游戏记录
         * 
         * @param playerName 玩家名称
         * @param time 完成时间（秒）
         */
        public Record(String playerName, int time) {
            this.playerName = playerName;
            this.time = time;
        }

        /**
         * 获取玩家名称
         * 
         * @return 玩家名称
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * 获取完成时间
         * 
         * @return 完成时间（秒）
         */
        public int getTime() {
            return time;
        }
    }
}