package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import data.GoMove;
import data.GoPosAbstract;
import data.GoStateAbstract;
import math.GoMatrix;
import math.GoVector;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GoCanvasAbstract extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
    public static final double SCROLL_FAC = 0.05;
    public static final double DRAG_FAC = 0.005;
    public static final double MAX_MOUSEMOVE = 5;
    public static final int CHAT_MESSAGE_DELAY = 25000;
    public static final float CHAT_MESSAGE_FS = 18f;

    private final GoViewer viewer;

    public GoStateAbstract state;

    protected double scale_state = 1;
    protected GoPosAbstract hover_pos;
    
    protected double[][] wlist = {{}, {}};
    
    protected GoMatrix Rot_old  = GoMatrix.unit();
    protected GoMatrix Rot_drag = GoMatrix.unit();
    protected double[][] drag_pos = null;

    protected GoMatrix Rot;
    protected GoMatrix Scl;
    protected GoMatrix Tsl;
    protected GoMatrix Trm;

    private final JTextField chatField;
    private final ArrayList<String> chatMessages = new ArrayList<>();

    protected HashMap<String, GoCanvasStoneAbstract> stones = new HashMap<>();
    public void putStone(GoCanvasStoneAbstract stone) {
        stones.put(stone.getPos().posString(), stone);
    }

    public GoCanvasAbstract(GoViewer viewer) {
        super();
        this.viewer = viewer;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        setLayout(new BorderLayout());
        JPanel chatPanel = new JPanel();
        chatField = new JTextField(30);
        chatField.setBorder(new EmptyBorder(new Insets(8, 8, 8, 8)));
        chatField.setFont(getFont().deriveFont(15f));
        chatField.setMargin(new Insets(0, 0, 10, 0));
        chatField.setVisible(false);
        chatField.addKeyListener(this);
        chatPanel.add(chatField);
        add(chatPanel, BorderLayout.SOUTH);
    }

    public abstract void setState(GoStateAbstract state);
    public abstract void paintMe(Graphics2D g2);

    public synchronized void showChatField() {
        chatField.setVisible(true);
        chatField.setText("Write your message here (press <ENTER> to send)... ");
        chatField.select(0, chatField.getText().length());
        chatField.requestFocus();
        viewer.validate();
    }

    private void sendChatMessage(boolean send) {
        if (send && !chatField.getText().equals("")) viewer.chatMessage(chatField.getText());
        chatField.setVisible(false);
        viewer.requestFocus();
    }

    public void chatMessage(String text) {
        chatMessages.add(text);
        repaint();
        new Thread(new Runnable() {
            public void run() {
                try { Thread.sleep(CHAT_MESSAGE_DELAY); }
                catch (InterruptedException ex) {}
                chatMessages.remove(text);
                repaint();
            }
        }).start();
    }

    private void paintChat(Graphics2D g2) {
        g2.setColor(Color.gray);
        g2.setFont(g2.getFont().deriveFont(CHAT_MESSAGE_FS));
        for (int i = 0; i < chatMessages.size(); i ++) {
            g2.drawString(chatMessages.get(i), 30, 30+CHAT_MESSAGE_FS*1.5f*i);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateMe();
        Graphics2D g2 = (Graphics2D) g;
        paintChat(g2);
        paintMe(g2);
    }

    // fitting object's size and position to window geometry
    protected synchronized void updateMe() {
        double[] size = { (double) getSize().width, (double) getSize().height };
        double scale = Math.min(size[0], size[1]) * 0.4 * scale_state;

        Rot = Rot_drag.connect(Rot_old);
        Scl = GoMatrix.scale(scale, -scale, scale);
        Tsl = GoMatrix.translate(new GoVector(size[0]/2, size[1]/2, 0d));

        Trm = Tsl.connect(Scl);

        for (GoCanvasStoneAbstract p : stones.values()) { p.update(Trm, Rot); }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (drag_pos != null) {
            drag_pos[1][0] = e.getX();
            drag_pos[1][1] = e.getY();

            Rot_drag = GoMatrix.rotate(
                new GoVector(
                    (drag_pos[1][1] - drag_pos[0][1]) * GoCanvasSphere.DRAG_FAC,
                    (drag_pos[1][0] - drag_pos[0][0]) * GoCanvasSphere.DRAG_FAC,
                    0
                )
            );

            repaint(); 
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hover_pos = null;
        boolean running = (state.status == GoStateAbstract.RUNNING_STATUS && state.me == state.turn);
        boolean delete = state.status == GoStateAbstract.DELETE_STATUS;
        if (running || delete) {
            for (GoCanvasStoneAbstract point : stones.values()) {
                boolean running_ok = state.status == GoStateAbstract.RUNNING_STATUS && point.getPos().s == 0;
                boolean delete_ok  = state.status == GoStateAbstract.DELETE_STATUS  && point.getPos().s != 0;
                if ((running_ok || delete_ok) && point.isHovered(e.getX(), e.getY())) {
                    hover_pos = point.getPos().changeStone(state.me+1);
                    break;
                }
            }
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        drag_pos = new double[2][2];
        drag_pos[0][0] = e.getX();
        drag_pos[0][1] = e.getY();
        drag_pos[1][0] = e.getX();
        drag_pos[1][1] = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        Rot_old  = Rot_drag.connect(Rot_old);
        Rot_drag = GoMatrix.unit();
        if (hover_pos != null) {
            GoVector delta = new GoVector(
                drag_pos[1][0] - drag_pos[0][0],
                drag_pos[1][1] - drag_pos[0][1],
                0
            );
            if (delta.length() <= MAX_MOUSEMOVE) {
                viewer.doMove(new GoMove(hover_pos, state.me));
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {}

    public void mouseWheelMoved(MouseWheelEvent e) {
        scale_state += e.getWheelRotation() * GoCanvasSphere.SCROLL_FAC * scale_state;
        repaint();
    }

    public void keyTyped(KeyEvent var1) {}
    public void keyPressed(KeyEvent var1) {
        if (var1.getKeyCode() == KeyEvent.VK_ESCAPE) sendChatMessage(false);
        if (var1.getKeyCode() == KeyEvent.VK_ENTER) sendChatMessage(true);
    };

    public void keyReleased(KeyEvent var1) {}
}
