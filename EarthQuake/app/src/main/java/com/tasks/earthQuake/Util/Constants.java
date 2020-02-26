package com.tasks.earthQuake.Util;

import java.util.Random;

public class Constants {
    public static final int LIMIT=30;
    public static final String URL="https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";

    public static int randomInt(int max,int min){
//        creating a random object ,input is the range of the generated number
        return new Random().nextInt(max-min)+min;
    }
}
