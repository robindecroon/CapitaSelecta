package data;

import de.fhpotsdam.unfolding.geo.Location;

public class Country {
	private String name;
	private String abbreviation;
	private Location location;

	public Country(String name, String abbriviation, Location location) {
		setName(name);
		setAbbriviation(abbriviation);
		setLocation(location);
	}

	public Country(Country country) {
		setName(country.getName());
		setAbbriviation(country.getAbbreviation());
		setLocation(country.getLocation());
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("The given name is null!");
		this.name = name;
	}

	public void setAbbriviation(String abbreviation) {
		if (abbreviation == null)
			throw new NullPointerException("The given abbreviation is null!");
		if (abbreviation.length() != 3)
			throw new IllegalArgumentException(
					"The abbreviation does not consist of three letters!");
		this.abbreviation = abbreviation.toUpperCase();
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setLocation(Location location) {
		if (location == null)
			throw new NullPointerException("The given location is null!");
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((abbreviation == null) ? 0 : abbreviation.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (abbreviation == null) {
			if (other.abbreviation != null)
				return false;
		} else if (!abbreviation.equals(other.abbreviation))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}
