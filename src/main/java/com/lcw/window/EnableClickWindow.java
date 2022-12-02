package com.lcw.window;

import com.lcw.ClickPoint;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

// 可以点击的窗口
public interface EnableClickWindow extends Window{
//    List<ClickPoint> loadClickPoint();
    List<ClickPoint> loadClickPoint();

    void init(Rectangle windowLeftTop);
}
