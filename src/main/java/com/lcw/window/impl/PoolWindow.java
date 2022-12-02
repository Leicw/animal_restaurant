package com.lcw.window.impl;

import cn.hutool.core.collection.CollUtil;
import com.lcw.ClickPoint;
import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PoolWindow extends AnimalRestaurantWindow {
    @Override
    protected List<ClickPoint> loadRoutineClickPoint() {
        return Collections.emptyList();
    }

    @Override
    protected List<ClickPoint> loadOneTimeClickPoint() {
        List<ClickPoint> clickPoints = new ArrayList<>();

        // 点出来弹窗
        ClickPoint open = new ClickPoint("点开池塘钓鱼", 150, 510, 50, 50);
        clickPoints.add(open);
        // 邀请卡
        ClickPoint openInvite = new ClickPoint("点开获取鱼饵", 310, 140, 20, 20);
        clickPoints.add(openInvite);
        for (int i = 0; i < 20; i++) {
            ClickPoint adClickPoint = ClickPoint.adClickPoint(400, 570, 100, 100);
            adClickPoint.setExecThis(current -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_ad_yuer.png"), 0.9));
            clickPoints.add(adClickPoint);
        }
        // 关闭
        ClickPoint close = new ClickPoint("关闭获取鱼饵", 300, 250, 100, 100);
        close.setNext(new ClickPoint("关闭池塘钓鱼", 55, 1055, 20, 20));
        clickPoints.add(close);
        return clickPoints;
    }

    @Override
    public TabEnum getNextTab(TabEnum last) {
        return TabEnum.balcony;
    }

    @Override
    public String getTabName() {
        return TabEnum.pool.getName();
    }
}
