package com.ztftrue.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class MainApplication extends JFrame {
    String configPath = "ScreenShot.json";
    BufferedImage image;
    BufferedImage tempImage;
    EventUtils eventUtils;

    @Override
    public void paint(Graphics g) {
        RescaleOp ro = new RescaleOp(0.7f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }

    Robot robot;

    public MainApplication() throws AWTException {
        eventUtils = new EventUtils(this);
        robot = new Robot();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(false);
        eventUtils.addEventListener(this);
    }

    /**
     * 先全屏获取图片
     */
    public void init() {

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (image != null) {
            image.getGraphics().dispose();
        }
        setUndecorated(true);
        setVisible(true);
        image = robot.createScreenCapture(new Rectangle(d));

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(this);
    }


    public static void main(String[] args) throws AWTException {

        System.setProperty("sun.java2d.uiScale", "1");
        MainApplication rd = new MainApplication();
        if (args.length > 0) {
            rd.configPath = args[0];
        }
        rd.setTitle("ScreenShot");
        rd.dispose();
        UIUtils uiUtils = new UIUtils();
        uiUtils.addTray(rd);
    }


}
