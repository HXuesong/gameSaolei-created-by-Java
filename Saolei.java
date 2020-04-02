/*
created by HanXuesong
*/

//导入图形可视包、触发事件包
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
本程序中声音文件的播放主要通过java.applet.AudioClip 接口来实现
虽然AudioClip是一个接口，不能直接创建实例，但是java.applet.Applet对象
提供了一个静态的方法newAudioClip()，可以直接利用语句，得到一个AudioClip的实例引用
*/

//为播放音频导入依赖
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

//定义主类
public class MyFrame extends JFrame {

    //容器类
    JPanel minePanel = new JPanel();
    JPanel menuPanel = new JPanel();
    JPanel parentPanel;

    //雷区与下方菜单为Border布局
    BorderLayout borderLayout = new BorderLayout();
    //雷区布局为Grid网格布局
    GridLayout gridLayout = new GridLayout();

    /*
    底部栏基础组件
    */
    //信息提示区：设置地雷数、剩余地雷数
    JLabel setMine, leftMine;
    //地雷数通过文本框输入
    JTextField textField;
    //开始按钮
    JButton start = new JButton(" Start ");

    /*
    地雷属性定义
    */
    //二维矩阵雷区
    Mine[][] mineButton;
    // 当前雷数,当前方块数
    int mineNum, blockNum;
    // 找到的地雷数，剩余雷数，剩余方块数
    int rightMine, restMine, leftBlock;

    //构造方法，对主类进行初始化
    public MyFrame() {

        try {

            init();

            //用户单击窗口的关闭按钮时程序执行的操作，使用 System exit 方法退出应用程序
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        } catch (Exception ee) {

            System.out.println(ee);
        }
    }

    //主函数
    public static void main(String[] args) {

        MyFrame myFrame = new MyFrame();

        myFrame.setVisible(true);
    }

    //设置游戏界面
    private void init() throws Exception {

        //用getContentPane()方法获得JFrame的内容面板,再将其转型为JPanel
        parentPanel = (JPanel) getContentPane();

        setTitle("Mine Clearing");

        //更换窗口标题栏图标
        ImageIcon icon=new ImageIcon("images/mine.jpg");
        setIconImage(icon.getImage());

        //Java的一个类，封装了一个构件的高度和宽度，这个类与一个构件的许多属性具有相关，此处用于设置方格大小
        setSize(new Dimension(600, 600));

        //生成的窗体大小只由程序员决定，用户不可以自由改变该窗体的大小
        setResizable(false);

        //窗体居中显示
        setLocationRelativeTo(null);

        //设置底部栏背景色
        menuPanel.setBackground(new Color(0,255,255));

        //设置开始按钮图标
        start.setIcon(new ImageIcon("images/mine.png"));

        parentPanel.setLayout(borderLayout);

        //设置雷区为网格布局
        minePanel.setLayout(gridLayout);

        //初始化设置
        blockNum = 169;
        mineNum = 10;

        //设置雷区网格的行数与列数
        gridLayout.setColumns((int) Math.sqrt(mineNum));
        gridLayout.setRows((int) Math.sqrt(blockNum));

        //定义雷区按钮
        mineButton = new Mine[(int) Math.sqrt(blockNum)][(int) Math.sqrt(blockNum)];

        for (int i = 0; i < (int) Math.sqrt(blockNum); i++) {

            for (int j = 0; j < (int) Math.sqrt(blockNum); j++) {

                mineButton[i][j] = new Mine(i, j);

                //设置字体大小，PLAIN：普通样式；Bold：粗体样式；ITALIC：斜体样式
                mineButton[i][j].setFont(new Font("", Font.PLAIN, 14));

                //为雷区按钮绑定鼠标事件
                mineButton[i][j].addActionListener(new MineAction(this));
                mineButton[i][j].addMouseListener(new MineMouseAction(this));

                //添加按钮到雷区
                minePanel.add(mineButton[i][j]);
            }
        }

        parentPanel.add(minePanel, java.awt.BorderLayout.CENTER);

        setMine = new JLabel("Set the number of mines: ");

        //构造一个用指定文本和列初始化的新TextField，以显示所设置的地雷数
        textField = new JTextField("10", 3);

        leftMine = new JLabel("Remaining mines: " + mineNum);

        //为开始按钮绑定事件监听
        start.addActionListener(new StartActionListener(this));

        //在底部容器中添加组件
        menuPanel.add(setMine);
        menuPanel.add(textField);
        menuPanel.add(start);
        menuPanel.add(leftMine);

        parentPanel.add(menuPanel, BorderLayout.SOUTH);

        //开始布雷
        startMine();
    }

    //开始布雷
    /*
    startMine()方法可根据参数提供的数据设置雷区的雷数，其中方块数固定
    */
    public void startMine() {

        leftMine.setText("Remaining mines: " + mineNum);

        //布雷初始化
        for (int i = 0; i < (int) Math.sqrt(blockNum); i++) {

            for (int j = 0; j < (int) Math.sqrt(blockNum); j++) {

                //初始化
                mineButton[i][j].mineRoundCount = 9;
                mineButton[i][j].isMine = false;
                mineButton[i][j].isClicked = false;
                mineButton[i][j].isRightClicked = false;
                mineButton[i][j].mineFlag = 0;

                mineButton[i][j].setEnabled(true);
                mineButton[i][j].setText("");

                //设置字体大小
                mineButton[i][j].setFont(new Font("", Font.PLAIN, 14));
                mineButton[i][j].setForeground(new Color(111,96,170));

                rightMine = 0;
                restMine = mineNum;
                leftBlock = blockNum - mineNum;
            }
        }

        //随机布雷，根据参数提供的数据设置雷区的雷数，其中方块数固定
        for (int i = 0; i < mineNum; ) {

            int x = (int) (Math.random() * (int) (Math.sqrt(blockNum) - 1));
            int y = (int) (Math.random() * (int) (Math.sqrt(blockNum) - 1));

            if (mineButton[x][y].isMine != true) {

                mineButton[x][y].isMine = true;
                i++;
            }
        }

        //方法调用
        CountRoundMine();
    }

    //开始按钮
    /*
    startAction(ActionEvent e)是实现的 ActionListener 接口中的方法
    当用户单击开始时，startAction(ActionEvent e)方法负责执行有关算法
    例如，当用鼠标左键单击方块上的按钮后，若用户定义雷数少于 10，或大于50
    将弹出错误提示，反之，则执行游戏
    */
    public void startAction(ActionEvent e) throws MalformedURLException,  FileNotFoundException, InterruptedException{

        //设置消息提示为英文
        UIManager.put("OptionPane.okButtonText","OK");

        //获取所设置的地雷数量
        int num = Integer.parseInt(textField.getText().trim());

        if (num >= 10 && num <= 50) {

            mineNum = num;

            startMine();
        } else if (num < 10) {

            //选择播放文件
            File file = new File("audio/warning.wav");
            //创建audioclip对象
            AudioClip audioClip = null;
            //将file转换为url
            audioClip = Applet.newAudioClip(file.toURL());
            //单次播放,播放一次可以使用audioClip.loop
            audioClip.play();

            //利用JOptionPane类弹出错误提示消息框
            JOptionPane.showMessageDialog(null, "The number of mines you set is too small, please reset!", "ERROR!", JOptionPane.ERROR_MESSAGE);

            //重置
            num = 10;
            mineNum = num;
        } else {

            File file = new File("audio/warning.wav");
            AudioClip audioClip = null;
            audioClip = Applet.newAudioClip(file.toURL());
            audioClip.play();

            JOptionPane.showMessageDialog(null, "The number of mines you set is too many, please reset!", "ERROR!", JOptionPane.ERROR_MESSAGE);

            num = 10;
            mineNum = num;
        }
    }

    //计算方块周围雷数
    /*
    CountRoundMine()方法是一个计算周围雷数算法，当需要检测的单元格本身无地雷的情况下
    统计周围的地雷个数，记录到 mineRoundCount 中以数字形式显示在单元格
    */
    public void CountRoundMine() {

        for (int i = 0; i < (int) Math.sqrt(blockNum); i++) {

            for (int j = 0; j < (int) Math.sqrt(blockNum); j++) {

                int count = 0;

                // 在需检测的雷块本身无雷的情况下,统计周围地雷个数
                if (mineButton[i][j].isMine != true) {

                    for (int x = i - 1; x <= i + 1; x++) {

                        for (int y = j - 1; y <= j + 1; y++) {

                            //保证坐标点在雷区内
                            if ((x >= 0) && (y >= 0) && (x < ((int) Math.sqrt(blockNum))) && (y < ((int) Math.sqrt(blockNum)))) {

                                if (mineButton[x][y].isMine == true) {

                                    count++;
                                }
                            }
                        }
                    }

                    //统计周围的地雷个数，记录到mineRoundCount中
                    mineButton[i][j].mineRoundCount = count;
                }
            }
        }
    }

    //是否挖完了所有的雷
    /*
    win()方法用来判断用户是否扫雷成功，如果成功该方法负责让一个
    文本框弹出提示游戏胜利,所谓扫雷成功是指找到了全部的雷
    */
    public void win() throws MalformedURLException,  FileNotFoundException, InterruptedException{

        leftBlock = blockNum - mineNum;

        for (int i = 0; i < (int) Math.sqrt(blockNum); i++) {

            for (int j = 0; j < (int) Math.sqrt(blockNum); j++) {

                //判定放个是否被点击
                if (mineButton[i][j].isClicked == true) {

                    leftBlock--;
                }
            }
        }

        if (rightMine == mineNum || leftBlock == 0) {

            //设置消息提示为英文
            UIManager.put("OptionPane.okButtonText","OK");

            JOptionPane.showMessageDialog(this, "You have dug up all the mines，you have won!", "Victory!", JOptionPane.INFORMATION_MESSAGE);

            //选择播放文件
            File file = new File("audio/vic.wav");
            //创建audioclip对象
            AudioClip audioClip = null;
            //将file转换为url
            audioClip = Applet.newAudioClip(file.toURL());
            //单次播放,播放一次可以使用audioClip.loop
            audioClip.play();

            //线程休眠2秒，在此期间程序暂停执行
            Thread.sleep(2000);

            //重新开始
            startMine();
        }
    }

    //当选中的位置为空,则翻开周围的地图
    /*
    isNull(mine ClickedButton)方法是用来判断周围雷数是否为 0 的算法
    若为空，则调用 open(mine ClickedButton)方法以翻开周围所有雷数为 0 的单元格
    */
    public void isNull(Mine ClickedButton) {

        int i, j;

        i = ClickedButton.num_x;
        j = ClickedButton.num_y;

        for (int x = i - 1; x <= i + 1; x++) {

            for (int y = j - 1; y <= j + 1; y++) {

                //锁定范围
                if (((x != i) || (y != j)) && (x >= 0) && (y >= 0) && (x < ((int) Math.sqrt(blockNum))) && (y < ((int) Math.sqrt(blockNum)))) {

                    //任何事件皆为触发
                    if (mineButton[x][y].isMine == false && mineButton[x][y].isClicked == false && mineButton[x][y].isRightClicked == false) {

                        open(mineButton[x][y]);
                    }
                }
            }
        }
    }

    //翻开
    /*
    open(mine ClickedButton)方法是进行翻开单元格的动作，还有
    */
    public void open(Mine ClickedButton) {

        //将组件设置为未启用，不再响应用户事件触发
        ClickedButton.setEnabled(false);
        ClickedButton.isClicked = true;

        if (ClickedButton.mineRoundCount > 0) {

            //小技巧：强转为字符串
            ClickedButton.setText(ClickedButton.mineRoundCount + "");
        } else {

            //继续回调，拓展范围
            isNull(ClickedButton);
        }
    }

    //鼠标左键点击
    public void actionPerformed(ActionEvent e) throws MalformedURLException,  FileNotFoundException, InterruptedException{

        //若为左键点击且之前尚未触发
        if (((Mine) e.getSource()).isClicked == false && ((Mine) e.getSource()).isRightClicked == false) {

            //若此单元格不为雷
            if (((Mine) e.getSource()).isMine == false) {

                open(((Mine) e.getSource()));

                try {
                    win();
                }
                catch (Exception ee) {
                    System.out.println(ee);
                }

            } else {

                for (int i = 0; i < (int) Math.sqrt(blockNum); i++) {

                    for (int j = 0; j < (int) Math.sqrt(blockNum); j++) {

                        //显示存在地雷的单元格
                        if (mineButton[i][j].isMine == true) {

                            mineButton[i][j].setFont(new Font("", Font.BOLD, 12));
                            mineButton[i][j].setText("B");
                        }
                    }
                }

                ((Mine) e.getSource()).setFont(new Font("", Font.BOLD, 16));
                ((Mine) e.getSource()).setForeground(Color.RED);
                ((Mine) e.getSource()).setText("X");

                //设置消息提示为英文
                UIManager.put("OptionPane.okButtonText","OK");

                JOptionPane.showMessageDialog(this, "You step on the mine, press OK to come back!", "Defeated!", 2);

                //选择播放文件
                File file = new File("audio/def.wav");
                //创建audioclip对象
                AudioClip audioClip = null;
                //将file转换为url
                audioClip = Applet.newAudioClip(file.toURL());
                //单次播放,播放一次可以使用audioClip.loop
                audioClip.play();

                //线程休眠2秒
                Thread.sleep(2000);

                startMine();
            }
        }
    }

    //右键点击
    public void mouseClicked(MouseEvent e) {

        //获取事件源
        Mine mineSource = (Mine) e.getSource();

        //判定是否为右键点击
        boolean right = SwingUtilities.isRightMouseButton(e);

        //若为右键点击且之前尚未触发
        if ((right == true) && (mineSource.isClicked == false)) {

            //两种状态切换
            mineSource.mineFlag = (mineSource.mineFlag + 1) % 2;

            if (mineSource.mineFlag == 1) {

                //若剩余雷数大于0
                if (restMine > 0) {

                    mineSource.setFont(new Font("", Font.BOLD, 16));
                    mineSource.setForeground(Color.RED);
                    mineSource.setText("!");

                    mineSource.isRightClicked = true;
                    restMine--;
                } else {

                    //若剩余雷数为0，则将探雷标记重新设置为0，即触发无效
                    mineSource.mineFlag = 0;
                }

            } else {

                //回退
                mineSource.setText("");
                mineSource.isRightClicked = false;
                mineSource.setFont(new Font("", Font.PLAIN, 14));

                restMine++;
            }

            if (mineSource.isMine == true) {

                if (mineSource.mineFlag == 1) {

                    rightMine++;
                }

                try {
                    win();
                }
                catch (Exception ee) {
                    System.out.println(ee);
                }
            }

            //更新当前雷数
            leftMine.setText("Remaining mines: " + restMine);
        }

    }
}

//定义Mine类，改写雷区按钮
class Mine extends JButton {

    //第几号方块
    int num_x, num_y;
    //周围雷数
    int mineRoundCount;

    //是否为雷
    boolean isMine;
    //插旗
    int mineFlag;

    //是否被点击
    boolean isClicked;
    //是否存在右键触发
    boolean isRightClicked;

    //构造方法，作初始设定
    public Mine(int x, int y) {

        num_x = x;
        num_y = y;
        mineRoundCount = 9;

        isMine = false;
        mineFlag = 0;

        isClicked = false;
        isRightClicked = false;

    }
}

//注册监听器以监听Start按钮产生的事件
class StartActionListener implements ActionListener {

    private MyFrame transfer;

    StartActionListener(MyFrame transfer) {

        this.transfer = transfer;
    }

    //定义处理事件的方法
    public void actionPerformed(ActionEvent e) {

        try {
            transfer.startAction(e);
        }
        catch (Exception ee) {
            System.out.println(ee);
        }
    }
}

//注册监听器以监听鼠标产生的事件
class MineAction implements ActionListener {

    private MyFrame transfer;

    MineAction(MyFrame transfer) {

        this.transfer = transfer;
    }

    //定义处理事件的方法
    public void actionPerformed(ActionEvent e) {

        try {
            transfer.actionPerformed(e);
        }
        catch (Exception ee) {
            System.out.println(ee);
        }

    }
}

//注册监听器以监听鼠标右键产生的事件
class MineMouseAction extends MouseAdapter {

    private MyFrame transfer;

    MineMouseAction(MyFrame transfer) {

        this.transfer = transfer;
    }

    //定义处理事件的方法
    public void mouseClicked(MouseEvent e) {

        transfer.mouseClicked(e);
    }
}
