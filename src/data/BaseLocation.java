package data;

import de.fhpotsdam.unfolding.geo.Location;

public class BaseLocation extends Location {
	private static final long serialVersionUID = -358192789404741695L;
	private String countryName;

	public BaseLocation(BaseLocation location) {
		super(location);
		setCountryName(location.getCountryName());
	}

	public BaseLocation(float lattitude, float longitude, String countryName) {
		super(lattitude, longitude);
		setCountryName(countryName);
	}

	public void setCountryName(String name) {
		if (name == null)
			throw new NullPointerException("The given name is null!");
		this.countryName = name;
	}

	public String getCountryName() {
		return countryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() + getCountryName().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object))
			return false;
		if (object == null)
			return false;
		if (!(object instanceof BaseLocation))
			return false;
		BaseLocation a = (BaseLocation) object;

		if (!getCountryName().equals(a.getCountryName()))
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
		return "(" + getLat() + "," + getLon() + ")";
	}

}
