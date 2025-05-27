
# 扫雷游戏 (Mine Sweeper)

## 项目简介

这是一个使用Java Swing开发的经典扫雷游戏，具有友好的图形用户界面和多种难度级别。游戏实现了扫雷的所有基本功能，并添加了排行榜系统来记录玩家的最佳成绩。

## 游戏特点

- **多种难度级别**：初级、中级、高级和自定义难度
- **计时系统**：记录游戏完成时间
- **排行榜**：为每个难度级别保存最佳成绩
- **自定义图标**：使用Java Graphics2D绘制的精美图标
- **键盘快捷键**：包括测试模式（Ctrl+B直接获胜）

## 游戏操作

- **左键点击**：揭示单元格
- **右键点击**：标记/取消标记地雷
- **点击笑脸按钮**：重置游戏
- **Ctrl+B**：测试模式，直接获胜（开发者功能）

## 项目结构

项目包含三个主要类：

1. **MineSweeper.java**：游戏的主类，包含UI界面和游戏逻辑
2. **LegendList.java**：负责管理排行榜记录
3. **IconManager.java**：负责创建和管理游戏图标

## 系统要求

- Java Runtime Environment (JRE) 8或更高版本
- 支持Windows、macOS和Linux等操作系统

## 如何运行

### 方法一：直接运行JAR文件

1. 确保已安装Java
2. 双击`MineSweeper.jar`文件运行游戏

### 方法二：从源代码编译运行

```bash
javac -d bin src/*.java
java -cp bin MineSweeper
```

## 将项目打包成单个EXE文件

要将Java项目打包成单个EXE文件，可以使用Launch4j工具。以下是详细步骤：

### 步骤1：创建JAR文件

首先，需要将项目打包成JAR文件：

1. 打开命令提示符，进入项目目录
2. 编译所有Java文件：
   ```bash
   mkdir bin
   javac -d bin src/*.java
   ```
3. 创建MANIFEST.MF文件，内容如下：
   ```
   Manifest-Version: 1.0
   Main-Class: MineSweeper
   ```
4. 创建JAR文件：
   ```bash
   jar cvfm MineSweeper.jar MANIFEST.MF -C bin .
   ```

### 步骤2：使用Launch4j创建EXE文件

1. 下载并安装[Launch4j](http://launch4j.sourceforge.net/)
2. 打开Launch4j配置工具
3. 基本设置：
   - 输出文件：设置为`MineSweeper.exe`
   - Jar：选择刚创建的`MineSweeper.jar`文件
   - 勾选「不要使用独立的JRE」选项
4. JRE设置：
   - 最小JRE版本：1.8.0
   - 最大JRE版本：（可留空）
   - JVM选项：-Xms64m -Xmx512m
5. 点击构建按钮生成EXE文件

### 步骤3：使用jpackage（Java 14+）

如果使用Java 14或更高版本，可以使用jpackage工具：

```bash
jpackage --input . --main-jar MineSweeper.jar --main-class MineSweeper --type exe --name "Mine Sweeper" --app-version 1.0 --win-menu --win-shortcut
```

### 步骤4：使用GraalVM Native Image（高级）

对于更小的可执行文件，可以考虑使用GraalVM：

1. 安装GraalVM和Native Image工具
2. 运行以下命令：
   ```bash
   native-image -jar MineSweeper.jar MineSweeper
   ```

## 注意事项

- 打包成EXE后，确保目标计算机上安装了兼容版本的Java（除非使用GraalVM方法）
- 排行榜记录存储在`LegendLists`目录中，确保程序有写入权限
- 自定义难度时，建议地雷数不要超过单元格总数的1/3，以保持游戏的可玩性

## 开发者信息

这个扫雷游戏是使用Java Swing开发的，展示了Java图形界面编程、事件处理、文件I/O和面向对象设计的应用。代码结构清晰，注释完善，适合Java初学者学习参考。

---

祝您游戏愉快！
