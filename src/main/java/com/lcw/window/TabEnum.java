package com.lcw.window;

import com.lcw.ClickPoint;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TabEnum {
    restaurant("餐厅",new ClickPoint("去餐厅", 300, 565, 50, 50,5)),
    flower("花园",new ClickPoint("去花园",185,650,35,35,5)),
    pool("池塘",new ClickPoint("去池塘",145,545,40,40,5)),
    yard("前院",new ClickPoint("去前院",270,445,45,65,5)),
    balcony("露台",new ClickPoint("去露台",420,450,50,50,5));

    TabEnum(String name, ClickPoint relativePoint) {
        this.name = name;
        this.relativePoint = relativePoint.setSleepTime(5);
    }

    private final String name;
    private final ClickPoint relativePoint;
    public static TabEnum of(String name){
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElseThrow(() -> new RuntimeException("对应名称场景不存在"));
    }
}
