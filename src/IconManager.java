import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * 图标管理器工具类，负责使用 Java Graphics2D 绘制和提供游戏所需的图标。
 * 图标包括笑脸、死亡、胜利（王冠）、旗帜和地雷。
 */
public class IconManager {
    /**
     * 游戏单元格图标的标准大小。
     */
    private static final int ICON_SIZE = 20;
    /**
     * 重置按钮图标的大小。
     */
    private static final int RESET_ICON_SIZE = 24;

    /**
     * 缓存的笑脸图标实例。
     */
    private static final ImageIcon smileIcon;
    /**
     * 缓存的死亡图标实例。
     */
    private static final ImageIcon deadIcon;
    /**
     * 缓存的胜利（王冠）图标实例。
     */
    private static final ImageIcon winIcon;
    /**
     * 缓存的旗帜图标实例。
     */
    private static final ImageIcon flagIcon;
    /**
     * 缓存的地雷图标实例。
     */
    private static final ImageIcon mineIcon;

    static {
        // 初始化所有图标，在类加载时创建并缓存。
        smileIcon = createSmileIcon();
        deadIcon = createDeadIcon();
        winIcon = createCrownIcon(); // 王冠图标
        flagIcon = createFlagIcon();
        mineIcon = createMineIcon();
    }

    /**
     * 创建笑脸图标。
     * @return 笑脸图标的 ImageIcon 实例。
     */
    private static ImageIcon createSmileIcon() {
        BufferedImage image = new BufferedImage(RESET_ICON_SIZE, RESET_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        // 绘制圆脸
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(2, 2, RESET_ICON_SIZE-4, RESET_ICON_SIZE-4);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(2, 2, RESET_ICON_SIZE-4, RESET_ICON_SIZE-4);

        // 绘制眼睛
        g2d.fillOval(7, 8, 3, 3);
        g2d.fillOval(14, 8, 3, 3);

        // 绘制笑容
        g2d.drawArc(7, 8, 10, 10, 0, -180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * 创建死亡图标。
     * @return 死亡图标的 ImageIcon 实例。
     */
    private static ImageIcon createDeadIcon() {
        BufferedImage image = new BufferedImage(RESET_ICON_SIZE, RESET_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        // 绘制圆脸
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(2, 2, RESET_ICON_SIZE-4, RESET_ICON_SIZE-4);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(2, 2, RESET_ICON_SIZE-4, RESET_ICON_SIZE-4);

        // 绘制X形眼睛
        g2d.drawLine(6, 7, 9, 10);
        g2d.drawLine(9, 7, 6, 10);
        g2d.drawLine(15, 7, 18, 10);
        g2d.drawLine(18, 7, 15, 10);

        // 绘制悲伤的嘴巴
        g2d.drawArc(7, 14, 10, 6, 0, 180);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * 创建王冠图标（用于表示胜利）。
     * @return 王冠图标的 ImageIcon 实例。
     */
    private static ImageIcon createCrownIcon() {
        BufferedImage image = new BufferedImage(RESET_ICON_SIZE, RESET_ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        // 绘制王冠
        int[] xPoints = {12, 4, 8, 12, 16, 20};
        int[] yPoints = {4, 12, 12, 8, 12, 12};
        g2d.setColor(Color.YELLOW);
        g2d.fillPolygon(xPoints, yPoints, 6);

        // 绘制王冠底部
        g2d.fillRect(4, 12, 16, 6);
        g2d.fillRect(6, 18, 12, 2);

        // 绘制边框
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 6);
        g2d.drawRect(4, 12, 16, 6);
        g2d.drawRect(6, 18, 12, 2);

        // 绘制宝石
        g2d.setColor(Color.RED);
        g2d.fillOval(7, 13, 2, 2);
        g2d.setColor(Color.BLUE);
        g2d.fillOval(12, 13, 2, 2);
        g2d.setColor(Color.GREEN);
        g2d.fillOval(17, 13, 2, 2);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * 创建旗帜图标。
     * @return 旗帜图标的 ImageIcon 实例。
     */
    private static ImageIcon createFlagIcon() {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        // 绘制旗杆
        g2d.setColor(Color.BLACK);
        g2d.fillRect(5, 3, 2, 14);

        // 绘制旗帜
        g2d.setColor(Color.RED);
        int[] xPoints = {7, 15, 7};
        int[] yPoints = {3, 6, 9};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // 绘制底座
        g2d.setColor(Color.BLACK);
        g2d.fillRect(3, 17, 6, 2);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * 创建地雷图标。
     * @return 地雷图标的 ImageIcon 实例。
     */
    private static ImageIcon createMineIcon() {
        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        // 绘制地雷主体
        g2d.setColor(Color.BLACK);
        g2d.fillOval(4, 4, 12, 12);

        // 绘制地雷尖刺
        g2d.drawLine(10, 0, 10, 20); // 垂直线
        g2d.drawLine(0, 10, 20, 10); // 水平线
        g2d.drawLine(3, 3, 17, 17); // 对角线
        g2d.drawLine(3, 17, 17, 3); // 对角线

        // 绘制高光
        g2d.setColor(Color.WHITE);
        g2d.fillOval(7, 7, 3, 3);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * 设置 Graphics2D 对象的渲染属性，以提高绘图质量。
     * @param g2d 要设置的 Graphics2D 对象。
     */
    private static void setupGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setStroke(new BasicStroke(1.0f));
    }

    /**
     * 获取笑脸图标。
     * @return 笑脸图标的 ImageIcon 实例。
     */
    public static ImageIcon getSmileIcon() { return smileIcon; }
    /**
     * 获取死亡图标。
     * @return 死亡图标的 ImageIcon 实例。
     */
    public static ImageIcon getDeadIcon() { return deadIcon; }
    /**
     * 获取胜利（王冠）图标。
     * @return 胜利（王冠）图标的 ImageIcon 实例。
     */
    public static ImageIcon getWinIcon() { return winIcon; }
    /**
     * 获取旗帜图标。
     * @return 旗帜图标的 ImageIcon 实例。
     */
    public static ImageIcon getFlagIcon() { return flagIcon; }
    /**
     * 获取地雷图标。
     * @return 地雷图标的 ImageIcon 实例。
     */
    public static ImageIcon getMineIcon() { return mineIcon; }
}