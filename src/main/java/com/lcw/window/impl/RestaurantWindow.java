package com.lcw.window.impl;

import com.lcw.ClickPoint;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantWindow extends AnimalRestaurantWindow {

    @Override
    protected List<ClickPoint> loadRoutineClickPoint() {
        ArrayList<ClickPoint> clickPoints = new ArrayList<>();

        // 小鱼干颜色（146,146,146）

        //1 看广告 （536,1032） 50*50
        ClickPoint ad = new ClickPoint();
        clickPoints.add(ad);
        ad.setName("看广告宣传");
        ad.setX(430);
        ad.setY(1030);
        ad.setYFlow(40);
        ad.setXFlow(40);
        ad.setNext(ClickPoint.adClickPoint(300,720,100,20,10));

        //2 循环三次点餐 3
        for (int i = 0; i < 4; i++) {
            // 点餐坐标 （左上角第一个（229.0,438.0）  x间距 120 y间距 170 3* 2 矩阵  保守可变宽度 34 * 22
            for (int k = 0; k < 3; k++) {
                ClickPoint dc = new ClickPoint();
                dc.setX(190 + k * 135);
                dc.setY(510);
                dc.setYFlow(40);
                dc.setXFlow(20);
                dc.setSleepTime(1);
                dc.setName("点餐" + (k * 2 + 1));
                clickPoints.add(dc);

                ClickPoint clone = dc.clone();
                clickPoints.add(clone);
                clone.setName("点餐" + (k * 2 + 2));
                clone.setY(clone.getY() + 180);
                // 最后一个休息6秒
                if (k == 2){
                    clone.setSleepTime(3);
                }

            }
        }

        return clickPoints;
    }

    @Override
    protected List<ClickPoint> loadOneTimeClickPoint() {
        return new ArrayList<>();
    }

    @Override
    public TabEnum getNextTab(TabEnum last) {
//        return TabEnum.flower;
        return TabEnum.restaurant;
    }


    @Override
    public String getTabName() {
        return TabEnum.restaurant.getName();
    }
}
