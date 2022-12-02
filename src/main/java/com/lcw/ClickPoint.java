package com.lcw;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import lombok.*;
import lombok.experimental.Accessors;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class ClickPoint {
    private static final double flowMultiple = 0.5;
    @NonNull
    private String name;
    @NonNull
    private double x;
    @NonNull
    private double y;
    private double xFlow;
    private double yFlow;
    private ClickPoint next;
    // 是否执行下一个点击
    private Predicate<ClickPoint> execNext;
    private Predicate<ClickPoint> execThis;
    private int sleepTime = 2;



    public ClickPoint(@NonNull String name, @NonNull double x, @NonNull double y, double xFlow, double yFlow) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.xFlow = xFlow;
        this.yFlow = yFlow;
    }

    public ClickPoint(@NonNull String name, @NonNull double x, @NonNull double y, double xFlow, double yFlow, ClickPoint next, int sleepTime) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.xFlow = xFlow;
        this.yFlow = yFlow;
        this.next = next;
        this.sleepTime = sleepTime;
    }

    public ClickPoint(@NonNull String name, @NonNull double x, @NonNull double y, double xFlow, double yFlow, int sleepTime) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.xFlow = xFlow;
        this.yFlow = yFlow;
        this.sleepTime = sleepTime;
    }

    @SneakyThrows
    public void click() {
        if (!Optional.ofNullable(execThis).map(e -> e.test(this)).orElse(true)){
            return;
        }

        Robot robot = new Robot();
        int randX = this.getRandX();
        int randY = this.getRandY();
        System.out.printf("点击 %s（%s,%s） 然后休息 %s秒", name, randX, randY,sleepTime);
        System.out.println();

        robot.mouseMove(randX, randY);
        //模拟鼠标按下左键
        robot.mousePress(InputEvent.BUTTON1_MASK);
        //模拟鼠标松开左键
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        if (sleepTime != 0){
            try {
                TimeUnit.SECONDS.sleep(sleepTime);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        // 如果有下一个  没有判断条件 或者 条件成立都点击下一个
        if (Objects.isNull(next) || !Optional.ofNullable(execNext).map(e -> e.test(this)).orElse(true)) {
            return;
        }
        next.click();
    }

    public int getRandX() {
        return new Double(x + RandomUtil.randomDouble(-xFlow, xFlow) * flowMultiple).intValue();
    }

    public int getRandY() {
        return new Double(y + RandomUtil.randomDouble(-yFlow, yFlow) * flowMultiple).intValue();
    }

    public ClickPoint toAbsolute(Rectangle currentRect, Rectangle originRect) {
        double x1 = currentRect.getWidth();
        double x2 = originRect.getWidth();
        double y1 = originRect.getHeight();
        double y2 = originRect.getHeight();
        // x方向变形
        double xMultiple = x1 / x2;
        // y方向变形
        double yMultiple = y1 / y2;
        ClickPoint clickPoint = this.clone();
        clickPoint.setY(currentRect.getY() + this.y * yMultiple);
        clickPoint.setX(currentRect.getX() + this.x * xMultiple);
        clickPoint.setXFlow(this.xFlow * xMultiple);
        clickPoint.setYFlow(this.yFlow * yMultiple);
        clickPoint.setNext(Optional.ofNullable(this.next).map(e -> e.toAbsolute(currentRect, originRect)).orElse(null));
        return clickPoint;
    }

    @Override
    public ClickPoint clone() {
        return BeanUtil.copyProperties(this, ClickPoint.class);
    }


    public static ClickPoint adClickPoint(@NonNull double x, @NonNull double y, double xFlow, double yFlow){
        return adClickPoint(x,y,xFlow,yFlow,null);
    }

    public static ClickPoint adClickPoint(@NonNull double x, @NonNull double y, double xFlow, double yFlow,Integer closeSleepTime){
        // 确认广告
        ClickPoint propAd = new ClickPoint("确认看广告", x, y, xFlow, yFlow);
        // 静音
        ClickPoint mute = new ClickPoint("静音广告",525,77,20,10, 0);
        propAd.setNext(mute);

        // 关闭广告
        ClickPoint close = new ClickPoint("关闭广告",565,77,20,10,Optional.ofNullable(closeSleepTime).orElse(5));

        close.setExecThis(e -> {
            long record = System.currentTimeMillis();
            while (true){
                // 这个广告不会超过32s  ( 32 多 2 为了防止 响应慢  时间没到
                if (System.currentTimeMillis() - record > 32* 1000){
                    return true;
                }

                // 为了 提前能关闭广告
                boolean check = AnimalRestaurantWindow.currentTab.get().check(new File(WindowUtil.IMAGES_PATH + "check_can_close_ad.png"), 0.8);
                if (check){
                    return true;
                }

                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        mute.setNext(close);

        return propAd;
    }
}
