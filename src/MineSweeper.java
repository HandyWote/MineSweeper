import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import javax.swing.*;


/**
 * 扫雷游戏的 Swing 用户界面类。
 * 负责游戏的图形界面显示、用户交互以及游戏逻辑的协调。
 * 提供了多种难度级别和自定义游戏参数的功能。
 */
public class MineSweeper extends JFrame {
    /**
     * 游戏面板的行数。
     * 默认为初级难度的5行。
     */
    public static int ROWS = 5;
    
    /**
     * 游戏面板的列数。
     * 默认为初级难度的5列。
     */
    public static int COLS = 5;
    
    /**
     * 游戏中的地雷总数。
     * 默认为初级难度的6个地雷。
     */
    public static int MINES = 6;
    
    /**
     * 每个单元格的像素大小。
     * 固定为25像素。
     */
    private static final int CELL_SIZE = 25;
    
    /**
     * 主面板，包含顶部面板和游戏面板
     */
    private JPanel mainPanel;
    
    /**
     * 游戏面板，包含所有单元格按钮
     */
    private JPanel gamePanel;
    
    /**
     * 游戏单元格按钮二维数组
     */
    private JButton[][] buttons;
    
    /**
     * 地雷位置布尔二维数组，true表示有地雷
     */
    private boolean[][] mines;
    
    /**
     * 单元格是否已揭示的布尔二维数组，true表示已揭示
     */
    private boolean[][] revealed;
    
    /**
     * 单元格是否已标记的布尔二维数组，true表示已标记为地雷
     */
    private boolean[][] flagged;
    
    /**
     * 每个单元格周围地雷数量的二维数组
     */
    private int[][] adjacentMines;
    
    /**
     * 显示剩余地雷数量的标签
     */
    private JLabel mineCountLabel;
    
    /**
     * 显示游戏时间的标签
     */
    private JLabel timerLabel;
    
    /**
     * 重置游戏的按钮（笑脸按钮）
     */
    private JButton resetButton;
    
    /**
     * 游戏计时器
     */
    private Timer gameTimer;
    
    /**
     * 游戏已进行的时间（秒）
     */
    private int timeElapsed;
    
    /**
     * 游戏是否结束的标志
     */
    private boolean gameOver;
    
    /**
     * 游戏是否胜利的标志
     */
    private boolean gameWon;
    
    /**
     * 剩余未标记的地雷数量
     */
    private int remainingMines;
    
    /**
     * 构造方法，初始化游戏数据并设置 MineSweeper。
     * 创建游戏界面并准备开始新游戏。
     */
    public MineSweeper() {
        initializeGame();
        setupUI();
        placeMines();
        calculateAdjacentMines();
        addKeyListener();
    }

    /**
     * 添加键盘快捷键监听器
     * 设置Ctrl+B为测试模式快捷键，直接获得胜利
     */
    private void addKeyListener() {
        // 设置键盘快捷键监听
        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getActionMap();

        // 定义Ctrl+B快捷键
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK);
        inputMap.put(ctrlB, "instantWin");
        actionMap.put("instantWin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instantWin();
            }
        });
    }

    /**
     * 测试后门：直接胜利
     * 揭示所有非地雷格子并标记所有地雷，使游戏直接进入胜利状态
     */
    private void instantWin() {
        if (gameOver) return;

        // 停止计时器
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }

        // 揭示所有非地雷格子
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!mines[i][j]) {
                    revealCell(i, j);
                } else {
                    // 标记所有地雷
                    flagged[i][j] = true;
                    buttons[i][j].setIcon(IconManager.getFlagIcon());
                    buttons[i][j].setBackground(Color.YELLOW);
                }
            }
        }

        // 更新游戏状态
        gameWon = true;
        gameOver = true;
        remainingMines = 0;
        mineCountLabel.setText("000");
        resetButton.setIcon(IconManager.getWinIcon());

        // 显示胜利消息
        JOptionPane.showMessageDialog(this,
                "测试模式激活！游戏直接胜利！\n用时: " + timeElapsed + " 秒",
                "测试胜利", JOptionPane.INFORMATION_MESSAGE);

        // 询问是否记录成绩
        askAndRecordScore();
    }

    /**
     * 初始化游戏数据结构，包括按钮数组、地雷位置、揭示状态、标记状态和相邻地雷数。
     * 重置游戏状态变量。
     */
    private void initializeGame() {
        buttons = new JButton[ROWS][COLS];
        mines = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        flagged = new boolean[ROWS][COLS];
        adjacentMines = new int[ROWS][COLS];
        timeElapsed = 0;
        gameOver = false;
        gameWon = false;
        remainingMines = MINES;
    }

    /**
     * 根据地雷数最大值动态调整滑块刻度
     * 根据不同范围的地雷数设置不同的刻度间隔
     * 
     * @param slider 地雷数滑块控件
     * @param maxMines 最大地雷数
     */
    private void updateMinesSliderTicks(JSlider slider, int maxMines) {
        // 根据地雷数范围智能设置刻度
        if (maxMines <= 20) {
            // 小范围: 主刻度5，次刻度1
            slider.setMajorTickSpacing(5);
            slider.setMinorTickSpacing(1);
        } else if (maxMines <= 50) {
            // 中等范围: 主刻度10，次刻度2
            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(2);
        } else if (maxMines <= 100) {
            // 较大范围: 主刻度20，次刻度5
            slider.setMajorTickSpacing(20);
            slider.setMinorTickSpacing(5);
        } else {
            // 大范围: 主刻度50，次刻度10
            slider.setMajorTickSpacing(50);
            slider.setMinorTickSpacing(10);
        }

        // 确保当前值不超过最大值
        if (slider.getValue() > maxMines) {
            slider.setValue(maxMines);
        }

        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
    }

    /**
     * 显示难度自定义对话框
     * 允许用户通过滑块调整行数、列数和地雷数
     */
    private void showDifficultyDialog() {
        // 创建对话框面板
        JPanel panel = new JPanel(new GridLayout(4, 2));

        // 创建滑块和标签
        JSlider rowsSlider = new JSlider(5, 30, ROWS);
        JSlider colsSlider = new JSlider(5, 30, COLS);
        JSlider minesSlider = new JSlider(1, (ROWS * COLS) / 2, MINES);
        int maxMines = (ROWS * COLS) / 2;
        rowsSlider.setMajorTickSpacing(5);
        rowsSlider.setMinorTickSpacing(1);
        rowsSlider.setPaintTicks(true);
        rowsSlider.setPaintLabels(true);
        colsSlider.setMajorTickSpacing(5);
        colsSlider.setMinorTickSpacing(1);
        colsSlider.setPaintTicks(true);
        colsSlider.setPaintLabels(true);
        // 动态设置地雷数滑块刻度
        updateMinesSliderTicks(minesSlider, maxMines);

        JLabel rowsLabel = new JLabel("行数: " + ROWS);
        JLabel colsLabel = new JLabel("列数: " + COLS);
        JLabel minesLabel = new JLabel("地雷数: " + MINES);

        // 添加组件到面板
        panel.add(new JLabel("行数:"));
        panel.add(rowsSlider);
        panel.add(rowsLabel);
        panel.add(new JLabel("列数:"));
        panel.add(colsSlider);
        panel.add(colsLabel);
        panel.add(new JLabel("地雷数:"));
        panel.add(minesSlider);
        panel.add(minesLabel);

        // 滑块变化监听器
        rowsSlider.addChangeListener(e -> {
            ROWS = rowsSlider.getValue();
            rowsLabel.setText("行数: " + ROWS);
            minesSlider.setMaximum((ROWS * COLS) / 2); // 更新地雷数最大值
        });

        colsSlider.addChangeListener(e -> {
            COLS = colsSlider.getValue();
            colsLabel.setText("列数: " + COLS);
            minesSlider.setMaximum((ROWS * COLS) / 2); // 更新地雷数最大值
        });

        minesSlider.addChangeListener(e -> {
            MINES = minesSlider.getValue();
            minesLabel.setText("地雷数: " + MINES);
        });

        // 显示对话框
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "自定义难度",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // 只有当用户点击确定时才重置游戏
        if (result == JOptionPane.OK_OPTION) {
            ROWS = rowsSlider.getValue();
            COLS = colsSlider.getValue();
            MINES = Math.min(minesSlider.getValue(), (ROWS * COLS) - 1); // 至少留一个安全格子
            resetGame(true); // 强制完全重建
        }
    }


    /**
     * 询问玩家是否记录游戏成绩并处理记录过程
     * <p>
     * 当游戏胜利且游戏时间大于0秒时，弹出对话框询问玩家是否记录成绩。
     * 如果玩家同意，则进一步询问玩家姓名，并将姓名和游戏时间记录到对应难度的排行榜文件中。
     * 如果是通过测试模式（Ctrl+B快捷键）获胜，会在玩家名前添加[TEST]标记。
     * </p>
     */
    private void askAndRecordScore() {
        if (timeElapsed == 0 || !gameWon) {
            return; // 不记录未完成或0秒的游戏
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "是否记录此次游戏成绩？\n",
                "记录成绩",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            String playerName = JOptionPane.showInputDialog(
                    this,
                    "来将可留姓名？：\n",
                    "记录成绩",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (playerName != null && !playerName.trim().isEmpty()) {
                // 为测试模式成绩添加标记
                String nameToSave = playerName.trim();
                if (gameWon) {
                    nameToSave = "[TEST]" + nameToSave;
                }
                LegendList.addRecord(nameToSave, timeElapsed);
            }
        }
    }

    /**
     * 显示当前难度的游戏排行榜
     * <p>
     * 从对应难度的排行榜文件中读取所有记录，并以表格形式展示。
     * 表格包含排名、玩家名和完成时间三列。
     * 如果当前难度没有任何记录，则显示提示信息。
     * </p>
     */
    private void showLeaderboard() {
        List<LegendList.Record> records = LegendList.getRecords();

        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "当前难度暂无记录！",
                    "英雄榜",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // 创建表格模型
        String[] columnNames = {"排名", "玩家", "时间(秒)"};
        Object[][] data = new Object[records.size()][3];

        for (int i = 0; i < records.size(); i++) {
            LegendList.Record record = records.get(i);
            data[i][0] = i + 1;
            data[i][1] = record.getPlayerName();
            data[i][2] = record.getTime();
        }

        // 创建表格
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false); // 禁止编辑
        table.setFillsViewportHeight(true);

        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 400));

        // 显示对话框
        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "英雄榜 - " + ROWS + "x" + COLS + " 地雷数:" + MINES,
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * 设置游戏窗口和面板的布局及组件
     * <p>
     * 初始化并配置游戏界面的所有UI组件，包括：
     * - 设置窗口标题、关闭行为和大小调整策略
     * - 创建主面板和顶部面板
     * - 配置菜单栏和菜单项（难度选择、排行榜、关于）
     * - 设置地雷计数器、重置按钮和计时器
     * - 初始化游戏面板和计时器
     * </p>
     */
    private void setupUI() {
        setTitle("扫雷");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 创建主面板
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        topPanel.setPreferredSize(new Dimension(40, 50));
        topPanel.setBackground(Color.LIGHT_GRAY);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建设置菜单
        JMenu setting = new JMenu("设置");
        JMenu difficulty = new JMenu("难度");
        JMenuItem junior = new JMenuItem("初级");
        JMenuItem middle = new JMenuItem("中级");
        JMenuItem senior = new JMenuItem("高级");
        JMenuItem customize = new JMenuItem("自定义");
        JMenuItem legendList = new JMenuItem("排行");
        JMenuItem about = new JMenuItem("关于");

        // 创建英雄榜窗口
        legendList.addActionListener(e -> showLeaderboard());

        // 创建关于事件
        about.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel,
                "我是黄应辉，我是一个负责的男人。\n" +
                        "没事可以来我的网站：www.handywote.site",
                "关于",
                JOptionPane.INFORMATION_MESSAGE));

        // 创建难度选择事件
        junior.addActionListener(e -> {
            ROWS = 5;
            COLS = 5;
            MINES = 6;
            resetGame(true);
        });
        middle.addActionListener(e -> {
            ROWS = 9;
            COLS = 9;
            MINES = 25;
            resetGame(true);
        });
        senior.addActionListener(e -> {
            ROWS = 15;
            COLS = 15;
            MINES = 50;
            resetGame(true);
        });
        // 创建难度自定义窗口
        customize.addActionListener(e -> showDifficultyDialog());

        // 设置菜单项的边距
        junior.setMargin(new Insets(5, 10, 5, 10));
        middle.setMargin(new Insets(5, 10, 5, 10));
        senior.setMargin(new Insets(5, 10, 5, 10));
        customize.setMargin(new Insets(5, 10, 5, 10));
        difficulty.setMargin(new Insets(5, -10, 5, -2));
        legendList.setMargin(new Insets(5, -10, 5, -2));

        // 添加菜单项到设置菜单
        setting.add(difficulty);
        setting.add(legendList);
        difficulty.add(junior);
        difficulty.add(middle);
        difficulty.add(senior);
        difficulty.addSeparator();
        difficulty.add(customize);

        // 添加菜单到菜单栏
        menuBar.add(setting);

        // 添加关于菜单项到菜单栏
        menuBar.add(about);

        // 设置菜单项的大小
        about.setPreferredSize(new Dimension(40, 20));
        difficulty.setPreferredSize(new Dimension(40, 20));
        legendList.setPreferredSize(new Dimension(40, 20));




        // 地雷计数器
        mineCountLabel = new JLabel(String.format("%03d", remainingMines));
        mineCountLabel.setFont(new Font("Digital-7", Font.BOLD, 24));
        mineCountLabel.setForeground(Color.RED);
        mineCountLabel.setBackground(Color.BLACK);
        mineCountLabel.setOpaque(true);
        mineCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mineCountLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    
        // 重置按钮（笑脸）
        resetButton = new JButton();
        resetButton.setIcon(IconManager.getSmileIcon());
        resetButton.setPreferredSize(new Dimension(40, 40));
        resetButton.addActionListener(e -> resetGame(false));
        resetButton.setFocusPainted(false);
    
        // 计时器
        timerLabel = new JLabel("000");
        timerLabel.setFont(new Font("Digital-7", Font.BOLD, 24));
        timerLabel.setForeground(Color.RED);
        timerLabel.setBackground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    
        topPanel.add(mineCountLabel, BorderLayout.WEST);
        topPanel.add(resetButton, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);
        topPanel.add(menuBar, BorderLayout.NORTH);

        // 创建游戏面板
        this.gamePanel = initGamePanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // 初始化计时器
        gameTimer = new Timer(1000, e -> {
            timeElapsed++;
            timerLabel.setText(String.format("%03d", Math.min(timeElapsed, 999)));
        });

        mainPanel.setFocusable(true);
        mainPanel.requestFocusInWindow();
    }

    /**
     * 在游戏面板上随机放置地雷
     * <p>
     * 使用随机数生成器在游戏网格中放置指定数量的地雷。
     * 确保放置的地雷数量等于MINES常量，且不会在同一位置重复放置地雷。
     * </p>
     */
    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;
    
        while (minesPlaced < MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);
    
            if (!mines[row][col]) {
                mines[row][col] = true;
                minesPlaced++;
            }
        }
    }
    
    /**
     * 计算每个非地雷单元格周围相邻的地雷数量
     * <p>
     * 遍历游戏网格中的每个非地雷单元格，计算其周围8个相邻单元格中的地雷数量。
     * 计算结果存储在adjacentMines数组中，用于显示数字提示。
     * </p>
     */
    private void calculateAdjacentMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (!mines[i][j]) {
                    int count = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            if (ni >= 0 && ni < ROWS && nj >= 0 && nj < COLS && mines[ni][nj]) {
                                count++;
                            }
                        }
                    }
                    adjacentMines[i][j] = count;
                }
            }
        }
    }
    
    /**
     * 处理鼠标左键点击事件
     * <p>
     * 当玩家左键点击游戏网格中的单元格时：
     * - 如果游戏已结束或单元格已被标记或揭示，则不执行任何操作
     * - 第一次点击时启动游戏计时器
     * - 如果点击到地雷，游戏结束并显示所有地雷位置
     * - 如果点击到安全区域，揭示该单元格并检查是否达成胜利条件
     * </p>
     *  @param row 点击的行索引
     * @param row 点击的行索引
     * @param col 点击的列索引
    */
    private void leftClick(int row, int col) {
        if (gameOver || flagged[row][col] || revealed[row][col]) {
            return;
        }
        
        // 第一次点击启动计时器
        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
        
        if (mines[row][col]) {
            // 踩到地雷
            gameOver = true;
            gameTimer.stop();
            resetButton.setIcon(IconManager.getDeadIcon());
            revealAllMines();
            buttons[row][col].setBackground(Color.RED);
        } else {
            // 安全区域
            revealCell(row, col);
            checkWinCondition();
        }
    }
    
    /**
     * 处理鼠标右键点击事件
     * <p>
     * 当玩家右键点击游戏网格中的单元格时：
     * - 如果游戏已结束或单元格已被揭示，则不执行任何操作
     * - 如果单元格已被标记为地雷，则取消标记并更新剩余地雷计数
     * - 如果单元格未被标记，则标记为地雷并更新剩余地雷计数
     * </p>
     * @param row 点击的行索引
     * @param col 点击的列索引
     */

    private void rightClick(int row, int col) {
        if (gameOver || revealed[row][col]) {
            return;
        }
        
        if (flagged[row][col]) {
            // 取消标记
            flagged[row][col] = false;
            buttons[row][col].setIcon(null);
            buttons[row][col].setBackground(null);
            remainingMines++;
        } else {
            // 标记为地雷
            flagged[row][col] = true;
            buttons[row][col].setIcon(IconManager.getFlagIcon());
            buttons[row][col].setBackground(Color.YELLOW);
            remainingMines--;
        }
        
        mineCountLabel.setText(String.format("%03d", Math.max(0, remainingMines)));
    }

    /**
     * 揭示指定位置的单元格
     * <p>
     * 当单元格被揭示时，会根据相邻地雷数量显示不同颜色的数字。
     * 如果单元格周围没有地雷（相邻地雷数为0），则会递归揭示周围的单元格。
     * 已经被揭示或标记为旗帜的单元格不会被再次揭示。
     * </p>
     * 
     * @param row 要揭示的单元格行索引
     * @param col 要揭示的单元格列索引
     */
    private void revealCell(int row, int col) {
        if (revealed[row][col] || flagged[row][col]) return;

        revealed[row][col] = true;
        buttons[row][col].setEnabled(false);
        buttons[row][col].setBackground(Color.LIGHT_GRAY);

        if (adjacentMines[row][col] > 0) {
            Color[] colors = {
                    Color.BLUE, Color.GREEN, Color.RED,
                    Color.MAGENTA, Color.ORANGE, Color.CYAN,
                    Color.BLACK, Color.GRAY
            };
            buttons[row][col].setForeground(colors[adjacentMines[row][col] - 1]);
            buttons[row][col].setText(String.valueOf(adjacentMines[row][col]));
        }
        if (adjacentMines[row][col] == 0) {
            buttons[row][col].setText("");
            // Recursively reveal adjacent cells
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    int ni = row + di;
                    int nj = col + dj;
                    if (ni >= 0 && ni < ROWS && nj >= 0 && nj < COLS) {
                        revealCell(ni, nj);
                    }
                }
            }
        }
    }
    
    /**
     * 揭示游戏中所有地雷的位置
     * <p>
     * 在游戏结束时调用此方法，显示所有地雷的位置。
     * 对于正确标记的地雷，保持旗帜图标；对于未标记的地雷，显示地雷图标；
     * 对于错误标记的位置（没有地雷但标记了旗帜），显示错误标记（❌）。
     * </p>
     */
    private void revealAllMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (mines[i][j] && !flagged[i][j]) {
                    buttons[i][j].setIcon(IconManager.getMineIcon());
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                } else if (!mines[i][j] && flagged[i][j]) {
                    buttons[i][j].setText("❌");
                    buttons[i][j].setBackground(Color.LIGHT_GRAY);
                }
            }
        }
    }
    
    /**
     * 检查游戏是否达成胜利条件
     * <p>
     * 胜利条件：除地雷外的所有单元格都已被揭示。
     * 当玩家获胜时，会自动标记所有未标记的地雷，停止计时器，
     * 更新剩余地雷计数为0，显示胜利消息，并记录玩家成绩。
     * </p>
     */
    private void checkWinCondition() {
        int revealedCount = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (revealed[i][j]) {
                    revealedCount++;
                }
            }
        }
        
        if (revealedCount == ROWS * COLS - MINES) {
            gameWon = true;
            gameOver = true;
            gameTimer.stop();
            resetButton.setIcon(IconManager.getWinIcon());
            
            // 自动标记所有剩余的地雷
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (mines[i][j] && !flagged[i][j]) {
                        flagged[i][j] = true;
                        buttons[i][j].setIcon(IconManager.getFlagIcon());
                        buttons[i][j].setBackground(Color.YELLOW);
                    }
                }
            }
            
            remainingMines = 0;
            mineCountLabel.setText("000");
            
            JOptionPane.showMessageDialog(this, "恭喜你获胜了！\n用时: " + timeElapsed + " 秒", 
                                        "游戏胜利", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(this::askAndRecordScore);
        }
    }

    /**
     * 重置游戏状态
     * <p>
     * 重置游戏计时器、游戏状态标志、剩余地雷计数和重置按钮图标。
     * 根据是否改变难度，执行不同的重置逻辑：
     * - 改变难度时：完全重建游戏界面和数据
     * - 普通重置：仅重置现有游戏数据和按钮样式
     * 重置后重新放置地雷并计算相邻地雷数量
     * </p>
     * 
     * @param changeDifficulty 是否改变难度设置
     */
    private void resetGame(boolean changeDifficulty) {
        // 停止计时器
        gameTimer.stop();
        timeElapsed = 0;
        timerLabel.setText("000");

        // 重置游戏状态
        gameOver = false;
        gameWon = false;
        remainingMines = MINES;
        mineCountLabel.setText(String.format("%03d", remainingMines));
        resetButton.setIcon(IconManager.getSmileIcon());

        if (changeDifficulty) {
            // 完全重建游戏界面
            mainPanel.remove(gamePanel);

            // 重新初始化游戏数据
            initializeGame();
            gamePanel = initGamePanel();
            mainPanel.add(gamePanel, BorderLayout.CENTER);

            // 必须调用这些方法来确保布局更新
            mainPanel.revalidate();
            mainPanel.repaint();

            // 调整窗口大小并居中
            pack();
            setLocationRelativeTo(null);
        } else {
            // 普通重置逻辑
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    mines[i][j] = false;
                    revealed[i][j] = false;
                    flagged[i][j] = false;
                    adjacentMines[i][j] = 0;

                    // 使用统一的样式重置
                    initButtonStyle(buttons[i][j]);
                }
            }
        }

        // 重新放置地雷
        placeMines();
        calculateAdjacentMines();
    }

    private JPanel initGamePanel() {
        JPanel panel = new JPanel(new GridLayout(ROWS, COLS, 1, 1));
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.setBackground(Color.GRAY);

        // 创建按钮
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                buttons[i][j] = createCellButton(i, j);
                panel.add(buttons[i][j]);
            }
        }
        return panel;
    }

    private JButton createCellButton(int row, int col) {
        JButton button = new JButton();
        initButtonStyle(button);  // 使用统一的样式初始化
        // 添加事件监听器
        button.addActionListener(e -> leftClick(row, col));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    rightClick(row, col);
                }
            }
        });
        return button;
    }

    private void initButtonStyle(JButton button) {
        button.setEnabled(true);
        button.setText("");
        button.setIcon(null);
        button.setBackground(null);
        button.setForeground(Color.BLACK);  // 设置默认前景色
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusPainted(false);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(() -> new MineSweeper().setVisible(true));

    }
}