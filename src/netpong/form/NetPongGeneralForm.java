package netpong.form;


import netpong.logics.LogicService;
import netpong.logics.Player;
import netpong.network.PacketBuilder;
import netpong.network.PacketSender;
import netpong.network.Validator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/* 27.02.2016.*/
public class NetPongGeneralForm {


    public NetPongGeneralForm() { init(); }

    private JPanel rootPanel;
    private JLabel timeLabel;
    private JLabel secondPlayerPoints;
    private JLabel firstPlayerPoints;
    private JPanel gamePanel;

    private JPanel connectionPanel;
    private JLabel roleLabel;


    public JPanel getConnectionPanel() {
        return connectionPanel;
    }

    public JLabel getRoleLabel() {
        return roleLabel;
    }
    public JPanel getRootPanel() { return rootPanel; }
    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public JLabel getSecondPlayerPoints() {
        return secondPlayerPoints;
    }

    public JLabel getFirstPlayerPoints() {
        return firstPlayerPoints;
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }


    public JFrame getFrame() {
        return frame;
    }

    private JFrame frame;

    private void init() {
        frame = new JFrame("NETpong");

        Font font = new Font("Verdana", Font.PLAIN, 12);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Menu");
        fileMenu.setFont(font);

        JMenu gameMenu = new JMenu("Game");
        gameMenu.setFont(font);
        fileMenu.add(gameMenu);

        JMenuItem newGameItem = new JMenuItem("New Local Game");
        newGameItem.addActionListener((e) -> LogicService.getInstance().startLocal());
        newGameItem.setFont(font);
        gameMenu.add(newGameItem);

        JMenuItem newNetworkGameItem = new JMenuItem("New Network Game");
        newNetworkGameItem.addActionListener((e) -> LogicService.getInstance().startNetwork());
        newNetworkGameItem.setFont(font);
        gameMenu.add(newNetworkGameItem);

        JMenuItem pauseItem = new JMenuItem("Pause");
        pauseItem.addActionListener((e) -> {
            if (LogicService.getInstance().isActive()) {
                LogicService.getInstance().pause();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong PAUSED");
            } else {
                LogicService.getInstance().resume();
                LogicService.getInstance().getForm().getFrame().setTitle("NETpong");
            }

        });
        pauseItem.setFont(font);
        gameMenu.add(pauseItem);


        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        fileMenu.add(exitItem);

        exitItem.addActionListener((e) -> System.exit(0));

        menuBar.add(fileMenu);

        JMenu networkMenu = new JMenu("Network");
        networkMenu.setFont(font);
        JMenuItem connect = new JMenuItem("Connect");
        connect.addActionListener((e) -> {
            String address;
            do {
                Object inputValue = JOptionPane.showInputDialog(
                        null,
                        "Enter Server IP",
                        "Set Server IP",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "127.0.0.1");

                if (  inputValue == null ) {
                    break;
                } else {
                    address = (String) inputValue;
                }
                if (!Validator.validationIP(address)) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid IP address.",
                            " Warning ",
                            JOptionPane.WARNING_MESSAGE);
                } else

                {
                    LogicService.getInstance().startNetworkAsClient();
                    LogicService
                            .getInstance()
                            .getPlayers()
                            .stream()
                            .filter(Player::isServer)
                            .findFirst()
                            .get()
                            .setIP(address);

                    PacketBuilder builder
                            = new PacketBuilder()
                            .setClient()
                            .setOk();
                    PacketSender.sendToServer(builder.toString());
                }
            } while (!Validator.validationIP(address));
        });

        connect.setFont(font);
        networkMenu.add(connect);

        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.setFont(font);
        networkMenu.add(disconnect);

        menuBar.add(networkMenu);
        JMenu help = new JMenu("Help");
        help.setFont(font);

        JMenuItem showConfig = new JMenuItem("Show configuration");
        showConfig.addActionListener(e -> JOptionPane.showMessageDialog(null, "1) create new game Menu->Game->New" +
                "\n2) left player up=w down=s" +
                "\n   right player up=o down=l" +
                "\n   for startLocal BOTH playes need to be ready (press UP or DOWN to be ready)" +
                "\n3) space - pause"));
        showConfig.setFont(font);
        help.add(showConfig);

        menuBar.add(help);

        frame.setJMenuBar(menuBar);
        frame.setPreferredSize(new Dimension(640, 480));

        timeLabel = new JLabel();
        secondPlayerPoints = new JLabel();
        firstPlayerPoints = new JLabel();
        gamePanel = new JPanel();
        rootPanel = new JPanel();
        roleLabel = new JLabel();
        connectionPanel = new JPanel();

        JPanel panel3 = new JPanel();
        JPanel panel5 = new JPanel();
        JLabel label2 = new JLabel();
        JPanel hSpacer1 = new JPanel(null);
        JLabel label4 = new JLabel();
        JPanel panel6 = new JPanel();
        JPanel panel7 = new JPanel();
        JPanel panel8 = new JPanel();

        rootPanel.setLayout(new BorderLayout());

        //======== panel3 ========
        {
            panel3.setMaximumSize(new Dimension(640, 150));
            panel3.setMinimumSize(new Dimension(640, 150));
            panel3.setPreferredSize(new Dimension(640, 150));
            panel3.setLayout(new BorderLayout());

            //======== panel5 ========
            {
                panel5.setMaximumSize(new Dimension(640, 40));
                panel5.setMinimumSize(new Dimension(640, 40));
                panel5.setPreferredSize(new Dimension(640, 40));
                panel5.setLayout(new FlowLayout());

                //---- label2 ----
                label2.setText("time:");
                panel5.add(label2);

                //---- timeLabel ----
                timeLabel.setText("--:--:--");
                panel5.add(timeLabel);
                panel5.add(hSpacer1);

                //---- label4 ----
                label4.setText("role:");
                panel5.add(label4);

                //---- roleLabel ----
                roleLabel.setText("---");
                panel5.add(roleLabel);

                panel5.add(hSpacer1);

                //panel5.add(new Rectangle(10, 10));
                //======== connectionPanel ========
                {
                    connectionPanel.setMaximumSize(new Dimension(10, 10));
                    connectionPanel.setMinimumSize(new Dimension(10, 10));
                    connectionPanel.setPreferredSize(new Dimension(10, 10));
                    connectionPanel.setBackground(Color.RED);
                    connectionPanel.setLayout(new BorderLayout());
                    connectionPanel.setVisible(false);
                }
                panel5.add(connectionPanel);
            }
            panel3.add(panel5, BorderLayout.SOUTH);

            //======== panel6 ========
            {
                panel6.setLayout(new BorderLayout());

                //======== panel7 ========
                {
                    panel7.setMaximumSize(new Dimension(310, 100));
                    panel7.setMinimumSize(new Dimension(310, 100));
                    panel7.setPreferredSize(new Dimension(310, 100));
                    panel7.setLayout(new BorderLayout());

                    //---- secondPlayerPoints ----
                    secondPlayerPoints.setText("0");
                    secondPlayerPoints.setFont(new Font("Tahoma", Font.PLAIN, 28));
                    secondPlayerPoints.setHorizontalAlignment(SwingConstants.CENTER);
                    panel7.add(secondPlayerPoints, BorderLayout.CENTER);
                }
                panel6.add(panel7, BorderLayout.EAST);

                //======== panel8 ========
                {
                    panel8.setMaximumSize(new Dimension(310, 100));
                    panel8.setMinimumSize(new Dimension(310, 100));
                    panel8.setPreferredSize(new Dimension(310, 100));
                    panel8.setLayout(new BorderLayout());

                    //---- firstPlayerPoints ----
                    firstPlayerPoints.setText("0");
                    firstPlayerPoints.setFont(new Font("Tahoma", Font.PLAIN, 28));
                    firstPlayerPoints.setHorizontalAlignment(SwingConstants.CENTER);
                    panel8.add(firstPlayerPoints, BorderLayout.CENTER);
                }
                panel6.add(panel8, BorderLayout.WEST);
            }
            panel3.add(panel6, BorderLayout.CENTER);
        }
        rootPanel.add(panel3, BorderLayout.NORTH);

        //======== gamePanel ========
        {
            gamePanel.setBorder(LineBorder.createBlackLineBorder());
            gamePanel.setLayout(new BorderLayout());
        }
        rootPanel.add(gamePanel, BorderLayout.CENTER);

        frame.add(rootPanel);

        //frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }
}