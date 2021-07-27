package com.ztftrue.screen;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventUtils {
    int startX, startY, endX, endY;

    BufferedImage saveImage;
    ResultWindow resultWindow;
    BaiduOCR baiduOCR;

    public EventUtils(MainApplication mainApplication) {
        resultWindow = new ResultWindow();
        baiduOCR = new BaiduOCR(mainApplication);
    }

    Graphics g;
    int borderWidth = 1;

    public void addEventListener(MainApplication mainApplication) {
        mainApplication.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
        });

        //鼠标运动监听器
        mainApplication.addMouseMotionListener(new MouseMotionAdapter() {
            //鼠标拖拽事件
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                g = mainApplication.getGraphics();
                g.drawImage(mainApplication.tempImage, 0, 0, mainApplication);
                int x = Math.min(startX, endX);
                int y = Math.min(startY, endY);
                //加上1，防止width,height为0
                int width = Math.abs(endX - startX) + borderWidth;
                int height = Math.abs(endY - startY) + borderWidth;
                g.setColor(Color.BLUE);
                g.drawRect(x, y, width, height);
                //减1，加1都是为了防止图片将矩形框覆盖掉
                if (saveImage != null) {
                    saveImage.getGraphics().dispose();
                }
                saveImage = mainApplication.image.getSubimage(x, y, width, height);
                g.drawImage(saveImage, x, y, mainApplication);
            }

        });

        mainApplication.addKeyListener(new KeyAdapter() {
            @Override
            //按键释放
            public void keyReleased(KeyEvent e) {
                // KeyEvent.VK_ENTER don't work
                if (e.getKeyCode() == 10 || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (saveImage != null) {
                        saveToFile(mainApplication);

                    }
                } else if (e.getKeyCode() == 27 || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    mainApplication.dispose();
                    releaseImage(mainApplication);
                }
            }
        });
    }

    private void saveToFile(MainApplication mainApplication) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String name = sdf.format(new Date());
        File path = FileSystemView.getFileSystemView().getHomeDirectory();
        String format = "jpg";
        File f = new File(path + File.separator + name + "." + format);
        try {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice();
            gd.setFullScreenWindow(null);
            mainApplication.dispose();
            ImageIO.write(saveImage, format, f);


            String result = baiduOCR.accurateBasic(BaiduOCR.imageType, f.getPath(), null);
            System.out.println(result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("words_result");
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                message.append(jsonObject1.getString("words")).append("\n");
            }
            resultWindow.setMessage(message.toString());
            releaseImage(mainApplication);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainApplication.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

            }
        });
    }

    public void releaseImage(MainApplication mainApplication) {
        if (saveImage != null)
            saveImage.getGraphics().dispose();
        if (g != null)
            g.dispose();
        if (mainApplication.image != null)
            mainApplication.image.getGraphics().dispose();
        if (mainApplication.tempImage != null)
            mainApplication.tempImage.getGraphics().dispose();
        System.gc();
    }
}
