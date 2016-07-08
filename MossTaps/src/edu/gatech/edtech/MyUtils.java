package edu.gatech.edtech;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyUtils {

	public static String getDateString(){
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		return sdf.format(date);
	}
}
