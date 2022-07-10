package com.cpen321.f5;

import java.util.HashMap;
import java.util.Map;

public class Item {
    private String id, title;
    private String description;
    private String sellerID;
    private String location;
    private int startPrc, deposit, stepPrc;
    private String postTime, postDelTime, timeLast, timeRemain;

    Map<Integer, String> bid = new HashMap<Integer, String>(); //bid price : price holder

    public Item(String _id, String ttl, String dsrp, String slID, String lct, String sttPrc, String dpst, String stpPrc, String pstTm, String tmLst, String tmRm){
        id = _id;
        title = ttl;
        description = dsrp;
        sellerID = slID;
        location = lct;
        startPrc = Integer.parseInt(sttPrc);
        deposit = Integer.parseInt(dpst);
        stepPrc = Integer.parseInt(stpPrc);
        postTime = pstTm;
        timeLast = tmLst;
        timeRemain = tmRm;
    }

    public void addBid(int price, String buyerID){
        bid.put(price, buyerID);
    }

    public int getHighestBid(){
        int max = startPrc;
        for (int ea : bid.keySet()){
            if (ea > max){ max = ea; }
        }
        return max;
    }

    public String getID(){
        return id;
    }
}