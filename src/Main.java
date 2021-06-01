import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        new MyFrame();
    }
}

class MyFrame extends JFrame {
    MyFrame()
    {
        int left,top,width,height;
        left=200;
        top =100;
        width=1506;
        height=835;
        setTitle("Drawing Window");
        setBounds(left,top,width,height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(1,1));
        PaintTable paintTable=new PaintTable();

        getContentPane().add(paintTable);

        setJMenuBar(new WindowMenu(paintTable));

        setVisible(true);

        paintTable.activePainting();
    }
}

class WindowMenu extends JMenuBar {
    private final PaintTable paintTable;

    WindowMenu(PaintTable paintTable) {
        this.paintTable=paintTable;
        JMenu menu1 = new JMenu("  文件  ");
        JMenu menu2 = new JMenu("  颜色  ");
        setFileMenu(menu1);
        setColorMenu(menu2);
        add(menu1);
        add(menu2);
    }

    void setFileMenu(JMenu jMenu){
        JMenuItem item1 = new JMenuItem("  新建");
        item1.addActionListener(e -> {
            paintTable.colorInfo = new ColorInfo();
            paintTable.colorInfo.setBackgroundColor(new Color(238,238,238).getRGB());
            paintTable.resetPaintPen();
            paintTable.removeAll();
            paintTable.repaint();
            paintTable.updateUI();
        });
        JMenuItem item2 = new JMenuItem("  打开");
        item2.addActionListener(e -> paintTable.readLoad());
        JMenuItem item3 = new JMenuItem("  保存");
        item3.addActionListener(e -> paintTable.colorInfo.outPutColorInfo());
        jMenu.add(item1);
        jMenu.add(item2);
        jMenu.add(item3);
    }

    void setColorMenu(JMenu jMenu){
        JMenuItem item = new JMenuItem("  画笔颜色");
        item.addActionListener(e -> new ColorWindow() {
            void jButtonAction() {
                int red = redJSlider.getValue();
                int green = greenJSlider.getValue();
                int blue = blueJSlider.getValue();
                paintTable.graphics2D.setColor(new Color(red, green, blue));
            }
        });
        jMenu.add(item);
        JMenuItem item1 = new JMenuItem("  背景颜色");
        item1.addActionListener(e -> new ColorWindow(){
            void jButtonAction(){
                int red = redJSlider.getValue();
                int green = greenJSlider.getValue();
                int blue = blueJSlider.getValue();
                paintTable.setBackground(new Color(red,green,blue));
                paintTable.colorInfo.setBackgroundColor(new Color(red,green,blue).getRGB());
            }
        });
        jMenu.add(item1);
    }
}

class ColorWindow extends JFrame{
    final int first = 238;
    private final JButton jButton;
    JSlider redJSlider;
    JSlider greenJSlider;
    JSlider blueJSlider;

    ColorWindow(){
        int left,top,width,height;
        left=300;
        top =200;
        width=406;
        height=235;
        setTitle("Color Window");
        setBounds(left,top,width,height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());

        jButton = new JButton("OK");
        jButton.addActionListener(e -> {

        });
        jButton.setPreferredSize(new Dimension(60,40));

        settingJButton();

        JPanel south = new JPanel();

        south.add(jButton);

        add(new NorthPanel());
        add(south,BorderLayout.SOUTH);

        setVisible(true);
    }

    class NorthPanel extends JPanel{
        NorthPanel(){
            setLayout(new GridLayout(3,1));
            settingRed();
            settingGreen();
            settingBlue();
        }
        void settingRed(){
            JLabel redJLabel = new JLabel("红");
            redJSlider = new JSlider(0,255,first);
            redJSlider.setOpaque(false);
            redJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            eventAction(redJSlider);
            add(redJLabel);
            add(redJSlider);
        }

        void settingGreen()
        {
            JLabel greenJLabel = new JLabel("绿");
            greenJSlider = new JSlider(0,255,first);
            greenJSlider.setOpaque(false);
            greenJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            eventAction(greenJSlider);
            add(greenJLabel);
            add(greenJSlider);
        }

        void settingBlue()
        {
            JLabel blueJLabel = new JLabel("蓝");
            blueJSlider = new JSlider(0,255,first);
            blueJSlider.setOpaque(false);
            blueJLabel.setHorizontalAlignment(SwingConstants.CENTER);
            eventAction(blueJSlider);
            add(blueJLabel);
            add(blueJSlider);
        }

        void eventAction(JSlider jSlider){
            jSlider.addChangeListener(e -> {
                int red = redJSlider.getValue();
                int green = greenJSlider.getValue();
                int blue = blueJSlider.getValue();
                setBackground(new Color(red,green,blue));
            } );
        }
    }

    void settingJButton(){
        jButton.addActionListener(e -> {
            jButtonAction();
            setVisible(false);
        });
    }

    void jButtonAction(){}
}

class PaintTable extends JPanel implements java.io.Serializable{
    Graphics2D graphics2D;
    ColorInfo colorInfo;
    private boolean first = true;
    private int x = 0;
    private int y = 0;

    PaintTable(){
        super();
        colorInfo = new ColorInfo();
    }

    void activePainting(){
        addMouseListener(new PaintPen());
        graphics2D = (Graphics2D) getGraphics();
        colorInfo.setBackgroundColor(getBackground().getRGB());
    }

    void readLoad(){
        try{
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("text.dat"));
            colorInfo = (ColorInfo) (objectInputStream.readObject());
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        repaint();
        first=true;

    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(4f));
        setBackground(new Color(colorInfo.getBackgroundRGB()));
        while(colorInfo.setNext())
        {
            g.setColor(new Color(colorInfo.getRGB()));
            g2.drawLine(colorInfo.getFirstX(),colorInfo.getFirstY(),colorInfo.getNextX(),colorInfo.getNextY());
        }
        colorInfo.resetCurrent();
    }

    void resetPaintPen(){first=true;}

    class PaintPen extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount()>1)
            {
                first = true;
            }
            else if (!first){
                graphics2D.drawLine(x,y,e.getX(),e.getY());
                colorInfo.setLineColor(x,y,e.getX(),e.getY(),graphics2D.getColor().getRGB());
                x=e.getX();
                y=e.getY();
            }
            else {
                graphics2D.setStroke(new BasicStroke(4f));
                first = false;
                x=e.getX();
                y=e.getY();
            }
        }
    }


}

class ColorInfo implements Serializable {
    private final ColorNode root;
    private ColorNode current;
    private ColorNode end;

    static class ColorNode implements Serializable{
        private int firstX;
        private int firstY;
        private int nextX;
        private int nextY;
        private int rgb;
        private ColorNode next;

        ColorNode(){
            firstX=firstY=nextX=nextY=rgb=-1;
            next=null;
        }
    }

    ColorInfo(){
        root = new ColorNode();
        root.next= new ColorNode();
        current = root;
        end = root;
    }

    int getBackgroundRGB(){
        return root.rgb;
    }

    void setBackgroundColor(int rgb){
        root.rgb=rgb;
    }

    void setLineColor(int firstX,int firstY,int nextX,int nextY,int rgb){
        end.next= new ColorNode();
        end = end.next;
        end.firstX=firstX;
        end.firstY=firstY;
        end.nextX=nextX;
        end.nextY=nextY;
        end.rgb=rgb;
    }

    void outPutColorInfo(){
        try{
            ObjectOutputStream objectOutputStream =new ObjectOutputStream(new FileOutputStream("text.dat"));
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    int getFirstX(){
        return current.firstX;
    }

    int getFirstY(){
        return current.firstY;
    }

    int getNextX(){
        return current.nextX;
    }

    int getNextY(){
        return current.nextY;
    }

    int getRGB(){
        return current.rgb;
    }

    boolean setNext(){
        if(current!=end)
        {
            current=current.next;
            return true;
        }else return false;
    }

    void resetCurrent(){
        current=root;
    }
}