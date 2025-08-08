package com.cnsu.client.view;

import com.cnsu.client.controller.ClientManager;
import com.cnsu.client.handler.LoginAction;
import com.cnsu.common.dto.LoginMessageDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {
    private ClientManager clientManager;
    public JButton loginButton;
    private JTextField idField;
    private JPasswordField passwordField;
    public LoginView(ClientManager clientManager)  {
        // --- 프레임, 메인 패널, 아이디/비밀번호/로그인 버튼 부분은 이전과 동일 ---
        this.clientManager = clientManager;

        setTitle("로그인");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("아이디:");
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(idLabel, gbc);

        idField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(idField, gbc);

        JLabel passwordLabel = new JLabel("비밀번호:");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        loginButton = new JButton("로그인");
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(loginButton, gbc);
        // --- 여기까지는 이전과 동일 ---

        // 하단 패널 (아이디/비밀번호 찾기, 회원가입)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JLabel findIdPwLabel = new JLabel("아이디/비밀번호 찾기");
        JLabel signUpLabel = new JLabel("회원가입");

        findIdPwLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // "회원가입" 라벨 클릭 이벤트 (기존 코드)
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SignView();
            }
        });
        // "아이디/비밀번호 찾기" 라벨에 마우스 클릭 이벤트 리스너 추가
        findIdPwLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // FindIdPwFrame의 새 인스턴스를 생성하여 창을 엽니다.
                //new FindIdPwFrame();
            }
        });


        bottomPanel.add(findIdPwLabel);
        bottomPanel.add(new JLabel("|"));
        bottomPanel.add(signUpLabel);

        // 프레임에 패널 추가
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 보이기
        setVisible(true);

        loginButton.addActionListener(new LoginAction(this ));
    }
    public ClientManager getClientManager(){
        return clientManager;
    }
    public String getIdField(){
        return idField.getText();
    }
    public String getPassword(){
        char[] strPassword = passwordField.getPassword() ;
        return new String(strPassword);
    }
    public LoginMessageDto getLoginMessageDto(){

        LoginMessageDto dto = new LoginMessageDto( getIdField(), getPassword());
        return dto;
    }
}
