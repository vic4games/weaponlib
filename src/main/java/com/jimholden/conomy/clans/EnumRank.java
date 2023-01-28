package com.jimholden.conomy.clans;

public enum EnumRank {
	LEADER,
	FIRSTOFFICER,
	SECONDOFFICER,
	PRIVATE,
	RECRUIT,
	NOCLAN,
	ANY;
	
	/*
	 * public static final List<EnumRank> rankOrder = Arrays.asList(ANY, NOCLAN, RECRUIT, PRIVATE, SECONDOFFICER, FIRSTOFFICER, LEADER);
	
	public boolean isGreater(EnumRank rank) {
		int thisPos = rankOrder.indexOf(this);
		int rankPos = rankOrder.indexOf(rank);
		//System.out.print
		if(thisPos > rankPos || this == LEADER) {
			return true;
		}
		else return false;
	}
	 */
	
	public boolean isGreater(EnumRank rank) {
		switch(this) {
			case ANY:
			default:
				return true;
			case NOCLAN:
				return rank.equals(NOCLAN);
			case RECRUIT:
				return false;
			case PRIVATE:
				return rank.equals(RECRUIT);
			case SECONDOFFICER:
				return rank.equals(PRIVATE) || rank.equals(RECRUIT);
			case FIRSTOFFICER:
				return rank.equals(SECONDOFFICER) || rank.equals(PRIVATE) || rank.equals(RECRUIT);
			case LEADER:
				return true;
		}	
	}
	
	public EnumRank getRankAbove() {
		switch(this) {
			case ANY:
			default:
				return ANY;
			case NOCLAN:
				return NOCLAN;
			case RECRUIT:
				return PRIVATE;
			case PRIVATE:
				return SECONDOFFICER;
			case SECONDOFFICER:
				return FIRSTOFFICER;
			case FIRSTOFFICER:
				return LEADER;
						
		}
	}
	public EnumRank getRankBelow() {
		switch(this) {
			case ANY:
			default:
				return ANY;
			case NOCLAN:
				return NOCLAN;
			case RECRUIT:
				return RECRUIT;
			case PRIVATE:
				return RECRUIT;
			case SECONDOFFICER:
				return PRIVATE;
			case FIRSTOFFICER:
				return SECONDOFFICER;
			case LEADER:
				return FIRSTOFFICER;
						
		}
	}

}
