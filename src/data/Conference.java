package data;

public enum Conference {
	LAK, EDM,JETS;
	
	public static Conference getConferenceFromAcro(String acronym) {
		String lower = acronym.toLowerCase();
		
		if (lower.contains("edm"))
			return EDM;
		else if (lower.contains("lak"))
			return LAK;
		else if (lower.contains("jets"))
			return JETS;
		else
			throw new IllegalStateException("Could not decode acronym from "+acronym);
	}
}
