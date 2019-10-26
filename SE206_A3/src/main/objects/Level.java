package main.objects;

/**
 * This enum stores the details about the various levels the user can achieve.
 * @author Hazel Williams
 *
 */
public enum Level {
	L1(1,new Boolean[]{true,false,false,false},0,100), L2(2,new Boolean[]{true,true,false,false},100,200), L3(3,new Boolean[]{true,true,false,false},200,450), L4(4,new Boolean[]{true,true,false,false},450,9999);
	
	private Boolean[] featureUnlocks;
	private int maxXP;
	private int minXP;
	private int levelID;
	
	private Level(int levelID, Boolean[] featureUnlocks, int minXP, int maxXP) {
		this.levelID = levelID;
		this.featureUnlocks = featureUnlocks;
		this.maxXP = maxXP;
		this.minXP = minXP;
	}
	
	/*
	 * return information about whether a feature is unlocked.
	 */
	public boolean isUnlocked(int index) {
		if(index < 0 || index > featureUnlocks.length) {
			//if its out of bounds, then assume its locked. 
			//(the might have a list thats longer than this level has, but those features wont be available to a previous level)
			return false;
		}
		return featureUnlocks[index];
	}
	
	/*
	 * If the xp is within the levels bounds, then the user is currently at this level.
	 */
	public boolean isThisLevel(int xp) {
		if(xp >= maxXP || xp < minXP) {
			return false;
		} else {
			//its 
			return true;
		}
	}
	
	/*
	 * There isn't a lot of abstraction to this. Potentially this could return a string value instead.
	 */
	public String getLevelNumber() {
		return ""+ levelID;
	}
	
	/*
	 * Returns the text that will be displayed under the progress bar. Provides some abstraction by not directly
	 * giving out the maxXP
	 */
	public String getXPText(int XP) {
		return (XP-minXP) + " / " + (maxXP-minXP) + " XP";
	}
	
	/*
	 * Returns the progress of the XP to the next level, so that it can be used in updating the progress bar
	 */
	public double getProgress(int XP) {
		//TODO: implement this function
		return ((double)(XP-minXP))/((double)(maxXP-minXP));
	}

}
