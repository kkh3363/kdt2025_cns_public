package com.cnsu.server.view;

import com.cnsu.server.controller.ServerManager;
import com.cnsu.server.handler.ServerActionHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ServerMainView extends JFrame {
    JPanel contentPane;
    JTextArea taclientlist;
    public JButton btnchangeport;
    public JButton btnstartServer;
    public JButton btnstop;
    ServerManager svrControl;
    public JButton btnClientList;

    public ServerMainView( ServerManager svrControl){
        this.svrControl = svrControl;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 673, 513);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        taclientlist = new JTextArea();
        taclientlist.setFont(new Font("Monospaced", Font.BOLD, 14));
        //taclientlist.setBounds(12, 50, 472, 415);
        JScrollPane scrollPane = new JScrollPane(taclientlist
                , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
                , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(12, 50, 472, 415);
        contentPane.add(scrollPane);
        //scrollPane.setVisible(true);

        JLabel label = new JLabel("\uC811\uC18D\uC790:");
        label.setFont(new Font("HY견고딕", Font.BOLD, 14));
        label.setBounds(12, 10, 120, 35);
        contentPane.add(label);

        btnchangeport = new JButton("\uD3EC\uD2B8\uBCC0\uACBD");
        btnchangeport.setFont(new Font("HY견고딕", Font.BOLD, 14));
        btnchangeport.setBounds(516, 50, 129, 35);
        contentPane.add(btnchangeport);

        btnstartServer = new JButton("\uC11C\uBC84\uC2DC\uC791");
        btnstartServer.setFont(new Font("HY견고딕", Font.BOLD, 14));
        btnstartServer.setBounds(516, 95, 129, 35);
        contentPane.add(btnstartServer);

        btnstop = new JButton("\uC11C\uBC84\uC911\uC9C0");
        btnstop.setFont(new Font("HY견고딕", Font.BOLD, 14));
        btnstop.setBounds(516, 140, 129, 35);
        contentPane.add(btnstop);
        btnstartServer.addActionListener(new ServerActionHandler(this, svrControl));
        btnstop.addActionListener(new ServerActionHandler(this,svrControl));

        btnClientList = new JButton("Queue");
        btnClientList.setFont(new Font("HY견고딕", Font.BOLD, 14));
        btnClientList.setBounds(516, 190, 129, 35);
        btnClientList.addActionListener(new ServerActionHandler(this,svrControl));
        contentPane.add(btnClientList);

        setVisible(true);
    }
    public void appendTextLog(String strLog){
        taclientlist.append(strLog+"\n");
    }
    public ServerManager getSeverManager(){
        return svrControl;
    }
}
