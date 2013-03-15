package data;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;

public class Author {
	private String firstName;
	private String lastName;
	private String authorResource;
	private String affiliation;
	private Location affiliationLocation;
	private Location countryLocation;
	private List<Paper> papers = new ArrayList<Paper>();

	public Author(String authorResource, String firstName, String lastName,
			String affiliation, Location countryLocation,
			Location affiliationLocation) {
		setFirstName(firstName);
		setLastName(lastName);
		setAuthorResource(authorResource);
		setAffiliation(affiliation);
		setCountryLocation(countryLocation);
		setAffiliationLocation(affiliationLocation);
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		if (affiliation == null)
			throw new NullPointerException("The given affiliation is null!");
		this.affiliation = affiliation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if (firstName == null)
			throw new NullPointerException("The given firstName is null!");
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if (firstName == null)
			throw new NullPointerException("The given lastName is null!");
		this.lastName = lastName;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public Location getLocation() {
		return affiliationLocation;
	}

	public String getAuthorResource() {
		return authorResource;
	}

	public void setAuthorResource(String authorResource) {
		if (authorResource == null)
			throw new NullPointerException("The given author resource is null!");
		this.authorResource = authorResource;
	}

	public void addPaper(Paper paper) {
		if (paper == null)
			throw new NullPointerException("The given paper is null!");
		if (papers.contains(paper))
			return;
		papers.add(paper);
	}

	public List<Paper> getPapers() {
		return new ArrayList<Paper>(papers);
	}

	public Location getAffiliationLocation() {
		return affiliationLocation;
	}

	public void setAffiliationLocation(Location affiliationLocation) {
		this.affiliationLocation = affiliationLocation;
	}

	public Location getCountryLocation() {
		return countryLocation;
	}

	public void setCountryLocation(Location countryLocation) {
		this.countryLocation = countryLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getFirstName().hashCode() + getLastName().hashCode()
				+ getCountryLocation().hashCode()
				+ getAffiliationLocation().hashCode()
				+ getAffiliation().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof Author))
			return false;
		Author a = (Author) object;

		if (!a.getFirstName().equals(getFirstName()))
			return false;
		if (!a.getLastName().equals(getLastName()))
			return false;
		if (!a.getCountryLocation().equals(getCountryLocation()))
			return false;
		if (!a.getPapers().equals(getPapers()))
			return false;
		if (!a.getAffiliationLocation().equals(getAffiliationLocation()))
			return false;
		if (!a.getAffiliation().equals(getAffiliation()))
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
		return "Author: " + getFirstName() + " " + getLastName() + " at "
				+ getAffiliationLocation() + " from " + getAffiliation();
	}

}
