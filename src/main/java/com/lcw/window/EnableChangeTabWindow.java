package com.lcw.window;

// 可以点击点位切换tab 的 窗口
public interface EnableChangeTabWindow extends EnableClickWindow{

    EnableClickWindow changeTab(String lastTab);

    String getTabName();
}
