package com.ztftrue.screen;

import javax.swing.*;

public class ResultWindow extends JFrame {
    JTextField tf;

    public void setMessage(String message) {
        this.setVisible(true);
        tf.setText(message);
    }

    public ResultWindow() {
        this.setSize(250, 220);
        this.setLocation(400, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tf = new JTextField();
        tf.setSize(250, 220);
        this.getContentPane().add(tf);
        setTitle("ScreenShot");
        this.setVisible(false);
        dispose();
    }
}
