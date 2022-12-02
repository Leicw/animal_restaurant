package com.lcw;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.lcw.util.WindowUtil;
import com.sun.imageio.plugins.common.ImageUtil;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.SneakyThrows;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void findMousePosition() {
        Rectangle programWindowRectangle = WindowUtil.findProgramWindowRectangle(App.PROGRAM_TITLE);
        System.out.println(programWindowRectangle);
        Point point = MouseInfo.getPointerInfo().getLocation();
        System.out.println("x=" + point.getX() + ",y=" + point.getY());

    }

    @Test
    public void findRestaurantRelativePosition() {
        Rectangle programWindowRectangle = WindowUtil.findProgramWindowRectangle(App.PROGRAM_TITLE);

        Point firstPoint = MouseInfo.getPointerInfo().getLocation();
        System.out.println("(" + (firstPoint.getX() - programWindowRectangle.getX()) + "," + (firstPoint.getY() - programWindowRectangle.getY()) + ")");

        // 所有可变 均为 y * x

        // 小鱼干颜色（146,146,146）

        // 点餐坐标 （左上角第一个（229.0,438.0）  x间距 120 y间距 170 3* 2 矩阵  保守可变宽度 34 * 22

        // 向左坐标 (31.0,575.0) 可变： 33 * 22
        // 向右坐标 (571.0,576.0) 可变： 33 * 22
        // 向上坐标 (374.0,263.0) 可变： 40 * 80
        // 向下坐标 (227.0,1058.0) 可变： 40 * 80

        // 种花坐标 （左上角第一个（(179.0,524.0)  x间距 240 y间距 170 2* 2 矩阵  保守可变宽度 40 * 25
        // 如果相对种花位置 （40，-65） 颜色为  （249，245,232）这个才点击 然后点击下面那个
        //  播种 (305.0,713.0) 保守可变宽度 105 * 25


    }

    @Test
    public void showW() {
        User32 user32 = User32.INSTANCE;
        WinDef.HWND programWindow = user32.FindWindow(null, "动物餐厅");
        // 先6（最小化） 再9（激活最小化）一定激活
        user32.ShowWindow(programWindow, 6);
        user32.ShowWindow(programWindow, 9);


    }

    private static WinUser.HHOOK hhk;
    private static WinUser.LowLevelKeyboardProc keyboardHook;
    static List<Character> singleInput = new ArrayList<>();

    private static String caseCode() {
        StringBuffer buffer = new StringBuffer();
        for (Character i : singleInput) {
            buffer.append(i);
        }
        return buffer.toString();
    }

    @Test
    public void testKeyListener() {
        final User32 lib = User32.INSTANCE;
        WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = new WinUser.LowLevelKeyboardProc() {
            boolean isShiftUp = false;

            @Override
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT info) {


                if (nCode >= 0) {

                    switch (wParam.intValue()) {
                        case WinUser.WM_KEYDOWN:// 只监听键盘按下
                // vkCode枚举 Win32VK
                            // 按下回车键，生成完整的字符串，并清空list
                            if (info.vkCode == 13) {

                                String text = caseCode();
                                System.out.println(text);
                                singleInput.clear();
                                break;
                            }

                            // 按下的是shift键时，标记一下
                            if (info.vkCode == 160) {
                                isShiftUp = true;
                            }
                            if (!isShiftUp) {
                                if (info.vkCode >= 65 && info.vkCode <= 90) {// 字母键
                                    singleInput.add((char) (info.vkCode + 32));
                                } else if (info.vkCode >= 219 && info.vkCode <= 221) {// [\]
                                    singleInput.add((char) (info.vkCode - 128));
                                } else if (info.vkCode >= 188 && info.vkCode <= 191) {// ,-./
                                    singleInput.add((char) (info.vkCode - 144));
                                } else if (info.vkCode >= 48 && info.vkCode <= 57) {// 数字键
                                    singleInput.add((char) info.vkCode);
                                }
                                if (info.vkCode == 186) {
                                    singleInput.add(';');
                                }
                                if (info.vkCode == 187) {
                                    singleInput.add('=');
                                }
                                if (info.vkCode == 192) {
                                    singleInput.add('`');
                                }
                                if (info.vkCode == 222) {
                                    singleInput.add('\'');
                                }
                            } else {
                                // 大写字母
                                if (info.vkCode >= 65 && info.vkCode <= 90) {
                                    singleInput.add((char) info.vkCode);
                                }

                                switch (info.vkCode) {
                                    case 186:
                                        singleInput.add(':');
                                        break;
                                    case 187:
                                        singleInput.add('+');
                                        break;
                                    case 188:
                                        singleInput.add('<');
                                        break;
                                    case 189:
                                        singleInput.add('_');
                                        break;
                                    case 190:
                                        singleInput.add('>');
                                        break;
                                    case 191:
                                        singleInput.add('?');
                                        break;
                                    case 192:
                                        singleInput.add('~');
                                        break;
                                    case 219:
                                        singleInput.add('{');
                                        break;
                                    case 220:
                                        singleInput.add('|');
                                        break;
                                    case 221:
                                        singleInput.add('}');
                                        break;
                                    case 222:
                                        singleInput.add('\"');
                                        break;
                                    case 48:
                                        singleInput.add('!');
                                        break;
                                    case 50:
                                        singleInput.add('@');
                                        break;
                                    case 51:
                                        singleInput.add('#');
                                        break;
                                    case 52:
                                        singleInput.add('$');
                                        break;
                                    case 53:
                                        singleInput.add('%');
                                        break;
                                    case 54:
                                        singleInput.add('^');
                                        break;
                                    case 55:
                                        singleInput.add('&');
                                        break;
                                    case 56:
                                        singleInput.add('*');
                                        break;
                                    case 57:
                                        singleInput.add('(');
                                        break;
                                    case 58:
                                        singleInput.add(')');
                                        break;
                                }
                            }
                            break;
                        case WinUser.WM_KEYUP:// 按键起来
                            if (info.vkCode == 160) {
                                isShiftUp = false;
                            }
                            break;
                    }
                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                // return lib.CallNextHookEx(hhk, nCode, wParam, new WinDef.LPARAM(peer));
                return new WinDef.LRESULT(0x18);
                // return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
            }
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

        // This bit never returns from GetMessage
        int result;
        WinUser.MSG msg = new WinUser.MSG();

        // 下面的代码只能这样触发 User32.INSTANCE.PostMessage(null, WinUser.WM_QUIT, null, null);

        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            System.out.println(0);
            if (result == -1) {
                // System.err.println("error in get message");
                break;
            } else {
                // System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhk);
    }

    @Test
    public void pathTest(){
        // System.out.println(System.getProperty("java.library.path"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(this.getClass().getClassLoader().getResource("").getPath());
        System.out.println(this.getClass().getResource("").getPath());
        System.out.println(this.getClass().getResource("/").getPath());
        // System.out.println(this.getClass().getResource("//").getPath());
        // System.out.println(this.getClass().getClassLoader().getResource("/").getPath());
        System.out.println(this.getClass().getClassLoader().getResource("//").getPath());
    }

    @Test
    public void openCvTest(){
        System.load(this.getClass().getClassLoader().getResource("lib" +File.separator + Core.NATIVE_LIBRARY_NAME + ".dll").getPath());

        Mat mat = Mat.eye( 3, 3, CvType.CV_8UC1  );
        System.out.println( "mat = " + mat.dump() );
    }

    @SneakyThrows
    @Test
    public void screenCaptureTest(){
        BufferedImage image = WindowUtil.robot.createScreenCapture(WindowUtil.findProgramWindowRectangle("动物餐厅"));
        ImageIO.write(image,"PNG",WindowUtil.CURRENT_WINDOW_CAPTURE_FIlE);
    }

    @Test
    public void imageHintSimilarTest(){
        Robot robot = WindowUtil.robot;
        Mat imread1 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m1.jpg");
        Mat imread11 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m1.jpg",Imgcodecs.IMREAD_GRAYSCALE);
        Mat imread2 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m2.png");
        Mat imread3 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m3.jpg");
        Mat imread4 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m4.jpg");

        // 色
        Mat hvs1 = new Mat();
        Mat hvs2 = new Mat();
        Mat hvs3 = new Mat();
        Mat hvs4 = new Mat();
        Imgproc.cvtColor(imread1,hvs1,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(imread2,hvs2,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(imread3,hvs3,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(imread4,hvs4,Imgproc.COLOR_BGR2GRAY);

       // 直方图计算
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        Mat hist3 = new Mat();
        Mat hist4 = new Mat();

        Imgproc.calcHist(Collections.singletonList(hvs1),new MatOfInt(0),new Mat(),hist1,new MatOfInt(255) ,new MatOfFloat(0,256));
        Imgproc.calcHist(Collections.singletonList(hvs2),new MatOfInt(0),new Mat(),hist2,new MatOfInt(255) ,new MatOfFloat(0,256));
        Imgproc.calcHist(Collections.singletonList(hvs3),new MatOfInt(0),new Mat(),hist3,new MatOfInt(255) ,new MatOfFloat(0,256));
        Imgproc.calcHist(Collections.singletonList(hvs4),new MatOfInt(0),new Mat(),hist4,new MatOfInt(255) ,new MatOfFloat(0,256));

        // 归一化 可不用
//        Core.normalize(hist1, hist1, 1, hist1.rows() , Core.NORM_MINMAX, -1, new Mat() );
//        Core.normalize(hist2, hist2, 1, hist2.rows() , Core.NORM_MINMAX, -1, new Mat() );
//        Core.normalize(hist2, hist2, 1, hist2.rows() , Core.NORM_MINMAX, -1, new Mat() );
//        Core.normalize(hist2, hist2, 1, hist2.rows() , Core.NORM_MINMAX, -1, new Mat() );


        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_CORREL));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_CORREL));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_CORREL));

        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_CHISQR));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_CHISQR));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_CHISQR));

        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_INTERSECT));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_INTERSECT));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_INTERSECT));

        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_HELLINGER));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_HELLINGER));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_HELLINGER));

        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_CHISQR_ALT));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_CHISQR_ALT));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_CHISQR_ALT));

        System.out.println(Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_KL_DIV));
        System.out.println(Imgproc.compareHist(hist1,hist3,Imgproc.CV_COMP_KL_DIV));
        System.out.println(Imgproc.compareHist(hist1,hist4,Imgproc.CV_COMP_KL_DIV));
    }

    @Test
    public void imageTemplateMathSimilarTest(){
        // 1 不能有中文  2template 宽高要比 图片都大或者都小 不能一大一小

        Robot robot = WindowUtil.robot;
        Mat imread1 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m1.jpg");
        Mat imread2 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m2.png");
        Mat imread3 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m3.jpg");
        Mat imread4 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m4.jpg");

        Mat mat1 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat1,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult minMaxLocResult1 = Core.minMaxLoc(mat1);
        System.out.println(JSONUtil.parseObj(minMaxLocResult1));

        Mat mat2 = new Mat();
        Imgproc.matchTemplate(imread1,imread3,mat2,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult minMaxLocResult2 = Core.minMaxLoc(mat2);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));

//        Mat mat3 = new Mat();
//        Imgproc.matchTemplate(imread1,imread4,mat3,Imgproc.TM_CCOEFF_NORMED);
//        Core.MinMaxLocResult minMaxLocResult3 = Core.minMaxLoc(mat3);
//        System.out.println(JSONUtil.parseObj(minMaxLocResult3));

    }

    @Test
    public void imageTemplateMathMentodSimilarTest(){
        // 1 不能有中文  2template 宽高要比 图片都大或者都小 不能一大一小

        Robot robot = WindowUtil.robot;
        Mat imread1 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m1.jpg");
        Mat imread2 = Imgcodecs.imread("C:\\Users\\Administrator\\Desktop\\m2.png");

        Mat mat1 = new Mat();
        // param1 原图 2：特征 3：结果 4：算法
        Imgproc.matchTemplate(imread1,imread2,mat1,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult minMaxLocResult1 = Core.minMaxLoc(mat1);
        System.out.println(JSONUtil.parseObj(minMaxLocResult1));

        Mat mat2 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat2,Imgproc.TM_SQDIFF);
        Core.MinMaxLocResult minMaxLocResult2 = Core.minMaxLoc(mat2);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));

        Mat mat3 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat2,Imgproc.TM_SQDIFF_NORMED);
        Core.MinMaxLocResult minMaxLocResult3 = Core.minMaxLoc(mat3);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));

        Mat mat4 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat2,Imgproc.TM_CCOEFF);
        Core.MinMaxLocResult minMaxLocResult4 = Core.minMaxLoc(mat4);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));

        Mat mat5 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat2,Imgproc.TM_CCORR);
        Core.MinMaxLocResult minMaxLocResult5 = Core.minMaxLoc(mat5);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));

        Mat mat6 = new Mat();
        Imgproc.matchTemplate(imread1,imread2,mat2,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult minMaxLocResult6 = Core.minMaxLoc(mat6);
        System.out.println(JSONUtil.parseObj(minMaxLocResult2));


    }
}
