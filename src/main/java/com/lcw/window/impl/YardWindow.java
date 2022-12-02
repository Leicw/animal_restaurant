package com.lcw.window.impl;

import com.lcw.ClickPoint;
import com.lcw.util.WindowUtil;
import com.lcw.window.AnimalRestaurantWindow;
import com.lcw.window.TabEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YardWindow extends AnimalRestaurantWindow {


    @Override
    protected List<ClickPoint> loadRoutineClickPoint() {
        return new ArrayList<>();
    }

    @Override
    protected List<ClickPoint> loadOneTimeClickPoint() {
        List<ClickPoint> clickPoints = new ArrayList<>();

        // 点出来弹窗
        ClickPoint open = new ClickPoint("点开前院杆子", 372, 820, 20, 75);
        clickPoints.add(open);
        // 邀请卡
        ClickPoint openInvite = new ClickPoint("点开邀请卡", 220, 890, 20, 20);
        clickPoints.add(openInvite);
        for (int i = 0; i < 10; i++) {
            ClickPoint adClickPoint = ClickPoint.adClickPoint(400, 570, 100, 100);
            adClickPoint.setExecThis(current -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_ad_invite.png"), 0.9));
            clickPoints.add(adClickPoint);
        }
        // 关闭
        ClickPoint closeInvite = new ClickPoint("关闭邀请卡", 300, 250, 100, 100);
        clickPoints.add(closeInvite);

        // 切换召唤
        ClickPoint switchZH = new ClickPoint("点开邀请卡", 420, 230, 100, 25);
        clickPoints.add(switchZH);
        // 召唤卡
        ClickPoint openZH = new ClickPoint("点开邀请卡", 220, 890, 20, 20);
        clickPoints.add(openZH);
        for (int i = 0; i < 10; i++) {
            ClickPoint adClickPoint = ClickPoint.adClickPoint(400, 570, 100, 100);
            adClickPoint.setExecThis(current -> super.check(new File(WindowUtil.IMAGES_PATH + "check_can_ad_zh.png"), 0.9));
            clickPoints.add(adClickPoint);
        }
        // 关闭
        ClickPoint close = new ClickPoint("关闭召唤卡", 300, 250, 100, 100);
        close.setNext(new ClickPoint("关闭杆子卡", 300, 980, 20, 20));
        clickPoints.add(close);
        return clickPoints;
    }

    @Override
    public TabEnum getNextTab(TabEnum last) {
        return TabEnum.pool;
    }

    @Override
    public String getTabName() {
        return TabEnum.yard.getName();
    }
}
