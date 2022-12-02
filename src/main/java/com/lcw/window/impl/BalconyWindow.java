package com.lcw.window.impl;

import com.lcw.ClickPoint;
import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BalconyWindow extends AnimalRestaurantWindow {
    @Override
    protected List<ClickPoint> loadRoutineClickPoint() {
        return Collections.emptyList();
    }

    @Override
    protected List<ClickPoint> loadOneTimeClickPoint() {
        List<ClickPoint> clickPoints = new ArrayList<>();

        // 点出来弹窗
        ClickPoint open = new ClickPoint("点开转瓶子", 190, 340, 50, 50);
        clickPoints.add(open);
        // 邀请卡
        ClickPoint openBottle = new ClickPoint("点开获取瓶子", 310, 140, 20, 20);
        clickPoints.add(openBottle);
        for (int i = 0; i < 6; i++) {
            ClickPoint adClickPoint = ClickPoint.adClickPoint(400, 570, 100, 100);
            adClickPoint.setExecThis(current -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_ad_bottle.png"), 0.9));
            clickPoints.add(adClickPoint);
        }
        // 关闭
        ClickPoint close = new ClickPoint("关闭获取瓶子", 300, 250, 100, 100);
        close.setNext(new ClickPoint("关闭转瓶子", 55, 1055, 20, 20));
        clickPoints.add(close);
        return clickPoints;
    }

    @Override
    public TabEnum getNextTab(TabEnum last) {
        return TabEnum.restaurant;
    }

    @Override
    public String getTabName() {
        return TabEnum.balcony.getName();
    }
}
