package com.lcw.window;

import cn.hutool.core.collection.CollUtil;
import com.lcw.App;
import com.lcw.ClickPoint;
import com.lcw.util.WindowUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Data
@Accessors(chain = true)
public abstract class AnimalRestaurantWindow implements EnableChangeTabWindow{
    /**
     * **************************************
     *
     * *****************************前院****露台***********
     *  *************                I      I
     * ****************池塘<=>花园<=>餐厅<=>外卖*************
     *  *************                I
     * ****************************自助餐******************
     * 餐厅（路过） -》  前院 （有限广告）-》
     * 餐厅（路过） -》 外卖（路过）-》 露台（有限广告） -》
     * 外卖（路过） -》花园（种植） -》
     * 池塘（有限广告） -》
     * 花园（路过） -》 餐厅（无限广告） -》 花园 -》 餐厅
     *
     */
    public static final ThreadLocal<AnimalRestaurantWindow> currentTab = new ThreadLocal<>();
    public static Rectangle originWindow = new Rectangle(603,1116);


    private static final ClickPoint changeTab;

    static {
        changeTab = new ClickPoint("准备切换场景",470,150,30,20);
    }

    protected Rectangle currentWindow;

    private List<ClickPoint> routineClickPoint;
    private List<ClickPoint> oneTimeClickPoint;

    @SneakyThrows
    public final void run(Rectangle rectangle){
        currentTab.set(this);
        this.init(rectangle);
        routineClickPoint.forEach(ClickPoint::click);
        currentTab.remove();
    }

    @Override
    public final List<ClickPoint> loadClickPoint() {
        this.oneTimeClickPoint  = this.loadOneTimeClickPoint().stream().map(e -> e.toAbsolute(currentWindow,originWindow)).collect(Collectors.toList());
        this.routineClickPoint = this.loadRoutineClickPoint().stream().map(e -> e.toAbsolute(currentWindow,originWindow)).collect(Collectors.toList());

        return  (ArrayList<ClickPoint>)CollUtil.addAll(CollUtil.addAll(new ArrayList<>(),oneTimeClickPoint),routineClickPoint);
    }
    public final AnimalRestaurantWindow changeTab2This(){
        changeTab(TabEnum.of(getTabName()));
        return this;
    }
    @Override
    public final void init(Rectangle windowLeftTop) {
        this.currentWindow = windowLeftTop;
        this.changeTab2This();
        if (App.tabContext.containsKey(TabEnum.of(getTabName()))){
            return;
        }
        App.tabContext.putIfAbsent(TabEnum.of(getTabName()),this);

        this.loadClickPoint();
        oneTimeClickPoint.forEach(ClickPoint::click);
    }
    protected abstract List<ClickPoint> loadRoutineClickPoint();

    protected abstract List<ClickPoint> loadOneTimeClickPoint();
    public abstract TabEnum getNextTab(TabEnum last);


    @Override
    public AnimalRestaurantWindow changeTab(String last){
        return this.changeTab(this.getNextTab(TabEnum.of(last)));
    }


    protected final AnimalRestaurantWindow changeTab(TabEnum tab){
        changeTab.clone()
                .setNext(
                        tab.getRelativePoint()
                                .setExecNext(e -> this.check(new File(WindowUtil.IMAGES_PATH + "in_change_tab.png"),0.9))
                        .setNext(new ClickPoint("当前已经在想要去的窗口，点击一下蒙版",300,180,100,100)) // 防止当前已经在这个窗口
                )
                .toAbsolute(currentWindow,originWindow).click();
        return App.tabContext.get(tab);
    }



    public boolean check(File target,double minSimilarRate){
        Mat mat = WindowUtil.captureWindowMat(WindowUtil.CURRENT_WINDOW_CAPTURE_FIlE,this.currentWindow);
        Core.MinMaxLocResult minMaxLocResult = WindowUtil.matchTemplate(mat, target);
        return minMaxLocResult.maxVal > minSimilarRate;
    }
}
