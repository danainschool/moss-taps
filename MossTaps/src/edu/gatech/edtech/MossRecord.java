package edu.gatech.edtech;

import java.util.Comparator;

public class MossRecord implements Comparable<MossRecord> {
	private String studentA;
	private String studentB;
	private String mossCompareLink;
	private String projectA;
	private String projectB;
	private boolean isCurrentProjectA;
	private boolean isCurrentProjectB;
	private int linesMatched;
	private int percentA;
	private int percentB;

	public MossRecord() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(MossRecord other) {
//		return this.commonLines-other.commonLines; // ascending order
		return other.linesMatched-this.linesMatched; // descending order
	}

	public static Comparator<MossRecord> StudentComparator 
		= new Comparator<MossRecord>() {

			@Override
			public int compare(MossRecord arg0, MossRecord arg1) {
				// TODO Auto-generated method stub
				
				//ascending order return arg0.compareTo(arg1)
				//descending order return arg1.compareTo(arg0)
				return 0;
			}
		
	};

	public String getStudentA() {
		return studentA;
	}

	public String getStudentB() {
		return studentB;
	}

	public String getMossCompareLink() {
		return mossCompareLink;
	}

	public String getProjectA() {
		return projectA;
	}

	public String getProjectB() {
		return projectB;
	}

	public boolean isCurrentProjectA() {
		return isCurrentProjectA;
	}

	public boolean isCurrentProjectB() {
		return isCurrentProjectB;
	}

	public int getLinesMatched() {
		return linesMatched;
	}

	public int getPercentA() {
		return percentA;
	}

	public int getPercentB() {
		return percentB;
	}

	public static Comparator<MossRecord> getStudentComparator() {
		return StudentComparator;
	}
	
}
