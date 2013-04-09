package data;

import de.fhpotsdam.unfolding.geo.Location;

public class University {
	private String name;
	private Country country;
	private Location location;

	public University(String name, Country country, Location location) {
		setName(name);
		setLocation(location);
		setCountry(country);
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		if (country == null)
			throw new NullPointerException("The given country is null!");
		this.country = country;
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("The given name is null!");
		this.name = name;
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
		result = prime * result + ((country == null) ? 0 : country.hashCode());
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
		University other = (University) obj;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
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
		return getName() + " in " + getCountry() + " at location "
				+ getLocation();
	}
}
