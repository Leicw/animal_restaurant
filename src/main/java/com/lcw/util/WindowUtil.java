package com.lcw.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import lombok.SneakyThrows;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.Reference;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WindowUtil {
    public static final User32 user32 = User32.INSTANCE;

    public static final Kernel32 kernel32 = Kernel32.INSTANCE;

    public static final Robot robot;

    public static final String OPENCV_Dll_PATH = "lib" + File.separator + "/opencv_java460.dll";

    public static final String CLASS_PATH = URLUtil.decode(WindowUtil.class.getClassLoader().getResource("lib").getPath().replace("lib", ""));

    public static final String IMAGES_PATH = CLASS_PATH + File.separator + "images" + File.separator ;

    public static final File CURRENT_WINDOW_CAPTURE_FIlE = new File(WindowUtil.CLASS_PATH + File.separator + "temp.png");

    static {
        try {
            robot = new Robot();
            System.load(WindowUtil.CLASS_PATH + WindowUtil.OPENCV_Dll_PATH);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static Rectangle findProgramWindowRectangle(String programTitle) {
        return getRect(findProgramWindowRectangle2(programTitle));
    }

    public static WinDef.HWND findProgramWindowRectangle2(String programTitle) {
        // 找到窗口
        return user32.FindWindow(null, programTitle);
    }

    public static Rectangle getRect(WinDef.HWND programWindow) {
        if (Objects.isNull(programWindow)) {
            throw new RuntimeException("程序未找到");
        }

        // 返回窗口描述
        WinDef.RECT program_rect = new WinDef.RECT();
        user32.GetWindowRect(programWindow, program_rect);
        return program_rect.toRectangle();
    }

    public static void showW(WinDef.HWND programWindow) {
        User32 user32 = User32.INSTANCE;
        // 先6（最小化） 再9（激活最小化）一定激活
        user32.ShowWindow(programWindow, 6);
        user32.ShowWindow(programWindow, 9);
    }

    public static void addExitListener(Win32VK key) {
        addKeyboardListener(e -> System.exit(0), WinUser.WM_KEYDOWN, key);
        new Thread(() -> {
            while (true){
                System.out.printf("按下%s退出程序",key.name());
                System.out.println();
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    /**
     * 添加键盘监听
     *
     * @param callback 回调
     * @param callType 监听的触发类型 按键起来 {@link WinUser WM_KEYUP } 按下 {@linkplain WinUser WM_KEYDOWN }
     * @param key      监听的按键
     */
    public static void addKeyboardListener(Consumer<Win32VK> callback, int callType, Win32VK... key) {

        Runnable s = () -> {
            AtomicReference<WinUser.HHOOK> hhkRef = new AtomicReference<>();

            // 第一步 创建回调
            WinUser.LowLevelKeyboardProc lowLevelKeyboardProc = (nCode, wParam, lParam) -> {
                int vkCode = lParam.vkCode;
                System.out.printf("按下键盘 %s", Win32VK.fromValue(vkCode).name());
                System.out.println();
                // 没有指定按键 则所有都触发
                if (ArrayUtil.isEmpty(key)) {
                    callback.accept(Win32VK.fromValue(vkCode));

                }

                // 监听指定按键
                else {
                    for (Win32VK win32VK : key) {
                        if (win32VK.code == vkCode) {
                            callback.accept(win32VK);
                        }
                    }
                }

                // 继续责任链
                Pointer ptr = lParam.getPointer();
                long peer = Pointer.nativeValue(ptr);
                /// 只要返回任何一个不是 0 的数 我们的系统输入将接收不到键盘
                // return new WinDef.LRESULT(1);
                // 请勿修改参数（利用修改这里的变量来修改键盘映射）， 可能会导致错误发生，
                return user32.CallNextHookEx(hhkRef.get(), nCode, wParam, new WinDef.LPARAM(peer));
            };
            // 第二步添加回调
            hhkRef.set(user32.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, lowLevelKeyboardProc, kernel32.GetModuleHandle(null), 0));
            // 必须要 GetMessage阻塞住 上面的回调才能被触发

            // This bit never returns from GetMessage
            int result;
            WinUser.MSG msg = new WinUser.MSG();

            // 下面的代码只能这样触发 User32.INSTANCE.PostMessage(null, WinUser.WM_QUIT, null, null);

            while ((result = user32.GetMessage(msg, null, 0, 0)) != 0) {
                System.out.println(0);
                if (result == -1) {
                    // System.err.println("error in get message");
                    break;
                } else {
                    // System.err.println("got message");
                    user32.TranslateMessage(msg);
                    user32.DispatchMessage(msg);
                }
            }
            user32.UnhookWindowsHookEx(hhkRef.get());
        };
        new Thread(s).start();
    }

    /**
     * 退出键盘监听
     *
     * @param hhk
     * @return
     */
    public static boolean removeKeyBoardListener(WinUser.HHOOK hhk) {
        return user32.UnhookWindowsHookEx(hhk);
    }

    /**
     * 保存窗口截图
     *
     * @param savePath   保存到的路径
     * @param windowName 窗口名称
     */
    @SneakyThrows
    public static void captureWindowAndSave(File savePath, String windowName) {
        BufferedImage image = WindowUtil.robot.createScreenCapture(WindowUtil.findProgramWindowRectangle(windowName));
        ImageIO.write(image, "PNG", savePath);
    }

    /**
     * 保存窗口截图
     *
     * @param savePath  保存到的路径
     * @param rectangle 窗口定位
     */
    @SneakyThrows
    public static void captureWindowAndSave(File savePath, Rectangle rectangle) {
        BufferedImage image = WindowUtil.robot.createScreenCapture(rectangle);
        ImageIO.write(image, "PNG", savePath);
    }

    public static Mat captureWindowMat(Rectangle rectangle) {
        String path = CLASS_PATH + File.separator + System.currentTimeMillis() + ".png";
        return captureWindowMat(new File(path),rectangle);
    }

    public static Mat captureWindowMat(File savePath,Rectangle rectangle) {
        captureWindowAndSave(savePath, rectangle);
        Mat imread = Imgcodecs.imread(savePath.getPath());
//        savePath.delete();
        return imread;
    }

    public static Core.MinMaxLocResult matchTemplate(Mat template, Mat target) {
        Mat mat = new Mat();
        Imgproc.matchTemplate(template, target, mat, Imgproc.TM_CCOEFF_NORMED);
        return Core.minMaxLoc(mat);
    }

    public static Core.MinMaxLocResult matchTemplate(Mat template, File target) {
        return matchTemplate(template,Imgcodecs.imread(target.getPath()));
    }


    public static void main(String[] args) {
/*        addExitListener(Win32VK.VK_ESCAPE);
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println(scanner.nextLine());
        }*/

        Core.MinMaxLocResult re = WindowUtil.matchTemplate(
                WindowUtil.captureWindowMat(WindowUtil.findProgramWindowRectangle("动物餐厅")),
                new File(IMAGES_PATH + "check_cant_ad.png"));
        System.out.println(JSONUtil.parseObj(re));
    }
}
