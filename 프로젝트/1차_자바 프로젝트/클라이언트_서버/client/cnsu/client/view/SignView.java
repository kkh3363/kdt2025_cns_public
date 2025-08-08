package com.cnsu.client.view;

import javax.swing.*;
import java.awt.*;

public class SignView extends JFrame {

    public SignView() {
        // 프레임 기본 설정
        setTitle("회원가입");
        setSize(350, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // 화면 중앙에 표시

        // 입력 필드를 담을 패널
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 컴포넌트 사이의 여백
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 아이디
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("아이디:"), gbc); //아이디 페널
        gbc.gridx = 1;
        panel.add(new JTextField(15), gbc); //아이디 텍스트 필드

        // 비밀번호
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("비밀번호:"), gbc);//비밀번호 페널
        gbc.gridx = 1;
        panel.add(new JPasswordField(15), gbc); // 비밀번호 텍스트 필드

        // 이메일
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("이메일:"), gbc); // 이메일 페널
        gbc.gridx = 1;
        panel.add(new JTextField(15), gbc); // 이메일 텍스트 필드

        // 닉네임
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("닉네임:"), gbc); // 닉네임 페널
        gbc.gridx = 1;
        panel.add(new JTextField(15), gbc); // 닉네임 텍스트 필드

        // 전화번호
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("전화번호:"), gbc); // 전화번호 페널
        gbc.gridx = 1;
        panel.add(new JTextField(15), gbc); // 전화번호 텍스트 필드

        // 가입하기 버튼
        JButton signUpButton = new JButton("가입하기");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // 버튼이 2칸을 차지하도록
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(signUpButton, gbc);

        // 프레임에 패널 추가
        add(panel, BorderLayout.CENTER);

        // 프레임 보이기
        setVisible(true);
    }
}
