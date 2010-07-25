package cit010_Eliza;

public class ReplacementUnit {
	private int beginPosition = -1;
	private int endPosition = -1;
	private boolean substitutionMode;
	private String replacement;
	
	ReplacementUnit(){
		
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public boolean getSubstitutionMode() {
		return substitutionMode;
	}

	public void setSubstitutionMode(boolean substitutionMode) {
		this.substitutionMode = substitutionMode;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
}
