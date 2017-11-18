package com.example.krasn.agent08.Utils;

public class OneThing {
    private String mAllCount;
    private String mCount;
    private Integer mId;
    private String mName;
    private String mNum = "0";
    private String mPrice;

    public OneThing(Integer id, String name, String allCount, String price, String num) {
        this.mId = id;
        this.mName = name;
        this.mAllCount = allCount;
        this.mPrice = price;
        this.mNum = num;
    }

    public void setCount(String count) {
        this.mCount = count;
    }

    public Integer getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public String getAllCount() {
        return this.mAllCount;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public String getCount() {
        return this.mCount;
    }

    public String getNum() {
        return this.mNum;
    }
}
