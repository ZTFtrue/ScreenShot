package com.ztftrue.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class UIUtils {


    public void addTray(MainApplication mainApplication) {
        // 判断是否支持系统托盘
        if (SystemTray.isSupported()) {
            Image image = Toolkit.getDefaultToolkit().getImage(
                    getClass().getClassLoader().getResource("icon.jpg"));
            // 创建托盘图标
            TrayIcon trayIcon = new TrayIcon(image);
            // 为托盘添加鼠标适配器
            // 添加工具提示文本
            trayIcon.setToolTip("ScreenShot");
            // 创建弹出菜单
            PopupMenu popupMenu = new PopupMenu();
            popupMenu.add(new MenuItem("Screen Shot al Screen"));
            popupMenu.addSeparator();
            popupMenu.add(new MenuItem("Screen Shot Area"));
            popupMenu.addSeparator();
            MenuItem menuItemExit = new MenuItem("Exit");
            menuItemExit.addActionListener(e -> System.exit(EXIT_ON_CLOSE));
            popupMenu.add(menuItemExit);
            trayIcon.setPopupMenu(popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1) {
                        Graphics g = mainApplication.getGraphics();
                        if (g != null) {
                            g.dispose();
                        }
                        mainApplication.dispose();
                        mainApplication.init();
                    }
                }
            });

            // 获得系统托盘对象
            SystemTray systemTray = SystemTray.getSystemTray();
            try {
                // 为系统托盘加托盘图标
                systemTray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "not support");
        }
    }


}
