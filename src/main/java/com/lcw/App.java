package com.lcw;

import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;
import com.lcw.window.impl.*;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;
import lombok.SneakyThrows;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {

    public static final String PROGRAM_TITLE = "动物餐厅";
    public static final Map<TabEnum,AnimalRestaurantWindow> tabContext = new HashMap<>();
    // 臭鼬 150 475
    static {
        System.load(WindowUtil.CLASS_PATH + WindowUtil.OPENCV_Dll_PATH);
    }


    @SneakyThrows
    public static void main(String[] args) {
        // 找到小程序坐标
        WinDef.HWND programWindowRectangle2 = WindowUtil.findProgramWindowRectangle2(PROGRAM_TITLE);
        Rectangle rect = WindowUtil.getRect(programWindowRectangle2);
        // 弹出小程序
        WindowUtil.showW(programWindowRectangle2);

        // 添加退出程序监听
        WindowUtil.addExitListener(Win32VK.VK_ESCAPE);

        // 延迟启动 保证弹窗跳出
        TimeUnit.SECONDS.sleep(3);

        // todo 餐厅特殊事件检测  蟑螂  小偷 臭鼬 富二代 歌手 老鼠

        AnimalRestaurantWindow restaurantWindow = new RestaurantWindow();
        AnimalRestaurantWindow flowerWindow = new FlowerWindow();
        AnimalRestaurantWindow yardWindow = new YardWindow();
        AnimalRestaurantWindow poolWindow = new PoolWindow();
        AnimalRestaurantWindow balconyWindow = new BalconyWindow();

        restaurantWindow.run(rect);
        flowerWindow.run(rect);
        yardWindow.run(rect);
        poolWindow.run(rect);
        balconyWindow.run(rect);

        while (true){
            for (int i = 0; i < 10; i++) {
                restaurantWindow.run(rect);
            }
            flowerWindow.run(rect);
        }
    }

    public static void init(){

    }


}
