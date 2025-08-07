```
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TopFrame extends JFrame {

    public TopFrame() {
        setTitle("Difference between Exit and Dispose First Frame");
        setSize(430, 110);
        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnDispose = new JButton("Dispose");
        btnDispose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        btnDispose.setBounds(10, 10, 100, 50);
        getContentPane().add(btnDispose);

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        btnExit.setBounds(120, 10, 100, 50);
        getContentPane().add(btnExit);

        JButton btnShow = new JButton("Show Another Frame");
        btnShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new SubFrame();
            }
        });
        btnShow.setBounds(230, 10, 170, 50);
        getContentPane().add(btnShow);

        setVisible(true);
    }
}
```
```
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SubFrame extends JFrame {
    public SubFrame() {
        setTitle("Difference between Exit and Dispose Second Frame");
        setSize(430, 110);
        setLocation(0, 120);
        getContentPane().setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton btnDispose = new JButton("Dispose");
        btnDispose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dispose();
            }
        });
        btnDispose.setBounds(10, 10, 100, 50);
        getContentPane().add(btnDispose);

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        btnExit.setBounds(120, 10, 100, 50);
        getContentPane().add(btnExit);

        setVisible(true);
    }
}
```
```
public class testMultiFrame {
    public static void main(String[] args) {
        TopFrame top = new TopFrame();
    }
}
```
