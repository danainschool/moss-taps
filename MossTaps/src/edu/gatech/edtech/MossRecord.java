package edu.gatech.edtech;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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
	}
	public MossRecord(String studentA, String studentB, String mossCompareLink, String projectA, String projectB,
			boolean isCurrentProjectA, boolean isCurrentProjectB, int linesMatched, int percentA, int percentB) {
		super();
		this.studentA = studentA;
		this.studentB = studentB;
		this.mossCompareLink = mossCompareLink;
		this.projectA = projectA;
		this.projectB = projectB;
		this.isCurrentProjectA = isCurrentProjectA;
		this.isCurrentProjectB = isCurrentProjectB;
		this.linesMatched = linesMatched;
		this.percentA = percentA;
		this.percentB = percentB;
	}

	public MossRecord(String studentA, String studentB, String mossCompareLink, int linesMatched ) {
		super();
		this.studentA = studentA;
		this.studentB = studentB;
		this.mossCompareLink = mossCompareLink;
		this.linesMatched = linesMatched;
	}

	public void setStudentA(String studentA) {
		this.studentA = studentA;
	}
	public void setStudentB(String studentB) {
		this.studentB = studentB;
	}
	public void setMossCompareLink(String mossCompareLink) {
		this.mossCompareLink = mossCompareLink;
	}
	public void setProjectA(String projectA) {
		this.projectA = projectA;
	}
	public void setProjectB(String projectB) {
		this.projectB = projectB;
	}
	public void setCurrentProjectA(boolean isCurrentProjectA) {
		this.isCurrentProjectA = isCurrentProjectA;
	}
	public void setCurrentProjectB(boolean isCurrentProjectB) {
		this.isCurrentProjectB = isCurrentProjectB;
	}
	public void setLinesMatched(int linesMatched) {
		this.linesMatched = linesMatched;
	}
	public void setPercentA(int percentA) {
		this.percentA = percentA;
	}
	public void setPercentB(int percentB) {
		this.percentB = percentB;
	}
	public static void setStudentComparator(Comparator<MossRecord> studentComparator) {
		StudentComparator = studentComparator;
	}
	@Override
	public int compareTo(MossRecord other) {
//		return this.linesMatched-other.linesMatched; // ascending order
		return other.linesMatched-this.linesMatched; // descending order
	}

	public static Comparator<MossRecord> StudentComparator 
		= new Comparator<MossRecord>() {

			@Override
			public int compare(MossRecord arg0, MossRecord arg1) {
				// TODO Implement StudentComparator
				
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
	public void setProjectStudentPercentA(String text) {
		Scanner scLinkText = new Scanner(text);
		if (scLinkText.hasNext()){
			List<String> fileWalk = splitFolderName(scLinkText);
			if (fileWalk.size()>1){
				setStudentA(fileWalk.get(fileWalk.size()-1));
				setProjectA(fileWalk.get(fileWalk.size()-2));
			}
			else {
				setStudentA("");
				setProjectA("");
			}
		}
		if (scLinkText.hasNext()){
			try {
				setPercentA(Integer.valueOf(scLinkText.next().replace("(", "").replace("%)","")));
			} catch (NumberFormatException e) {
				System.out.println("MossTaps did not capture percentage properly.");
				setPercentA(0);
			}
		}
		scLinkText.close();
	}
	
	public void setProjectStudentPercentB(String text) {
		Scanner scLinkText = new Scanner(text);
		if (scLinkText.hasNext()){
			List<String> fileWalk = splitFolderName(scLinkText);
			if (fileWalk.size()>1){
				setStudentB(fileWalk.get(fileWalk.size()-1));
				setProjectB(fileWalk.get(fileWalk.size()-2));
			}
			else {
				setStudentB("");
				setProjectB("");
			}
		}
		if (scLinkText.hasNext()){
			try {
				setPercentB(Integer.valueOf(scLinkText.next().replace("(", "").replace("%)","")));
			} catch (NumberFormatException e) {
				System.out.println("MossTaps did not capture percentage properly.");
				setPercentB(0);
			}
		}
		scLinkText.close();
	}
	private List<String> splitFolderName(Scanner scLinkText) {
		Scanner scFolderName = new Scanner(scLinkText.next());
		scFolderName.useDelimiter("/");
		List<String> fileWalk = new ArrayList<String>();
		while (scFolderName.hasNext()){
			fileWalk.add(scFolderName.next());
		}
		scFolderName.close();
		return fileWalk;
	}
	public boolean hasCurrentProject() {
		StringBuilder sbA = new StringBuilder(getProjectA());
		StringBuilder sbB = new StringBuilder(getProjectB());
		try {
			if (sbA.substring(0,2).equals("C_") || sbB.substring(0,2).equals("C_"))
				return true;
		} catch (Exception e) {
		}
		return false;
	}
	
}
