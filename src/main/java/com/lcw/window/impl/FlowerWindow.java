package com.lcw.window.impl;

import com.lcw.ClickPoint;
import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;
import com.sun.imageio.plugins.common.ImageUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlowerWindow extends AnimalRestaurantWindow {


    @Override
    protected List<ClickPoint> loadRoutineClickPoint() {
        ArrayList<ClickPoint> list = new ArrayList<>();
        // 种花坐标 （左上角第一个（(180.0,530.0)  x间距 235 y间距 155 2* 2 矩阵  保守可变宽度 35 * 15
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {

                // 先尝试收花
                ClickPoint plant = new ClickPoint(String.format("尝试种花第%d个",i * 2 + 1),180 + 235 * j,530 + 155 * i,35,15,null,5);
                plant.setExecNext(e -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_plant.png"),0.8));
                // todo 如果相对种花位置 （245，465） 颜色为  （249，245,232）这个才点击 然后点击下面那个
                //  播种 (300,715.0) 保守可变宽度 100 * 25
                ClickPoint doPlant = new ClickPoint(String.format("种花第%d个",i * 2 + j + 1),300,715,35,15);
                plant.setNext(doPlant);

                // 来两次 防止花需要收获
                list.add(plant.clone());
                list.add(plant);
            }
        }

        return list;
    }

    @Override
    protected List<ClickPoint> loadOneTimeClickPoint() {

        ArrayList<ClickPoint> list = new ArrayList<>();

        // 看广告许愿
        for (int i = 0; i < 20; i++) {
            ClickPoint wish = new ClickPoint("看广告许愿",300,845,35,15);
            wish.setNext(ClickPoint.adClickPoint(200,725,90,20,30));
            wish.setExecNext(current -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_wish.png"),0.9));
            list.add(wish);
        }

        return list;

    }

    @Override
    public TabEnum getNextTab(TabEnum last) {
        return TabEnum.restaurant;
    }



    @Override
    public String getTabName() {
        return TabEnum.flower.getName();
    }
}
