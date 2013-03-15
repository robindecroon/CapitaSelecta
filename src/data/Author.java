package data;

import java.util.ArrayList;
import java.util.List;

public class Author {
	private String authorResource;
	private String firstName;
	private String lastName;
	private University university;
	private Country country;
	private List<Paper> papers = new ArrayList<Paper>();

	public Author(String authorResource, String firstName, String lastName,
			University university, Country country) {
		setFirstName(firstName);
		setLastName(lastName);
		setAuthorResource(authorResource);
		setUniversity(university);
		setCountry(country);
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

	public String getAuthorResource() {
		return authorResource;
	}

	public void setAuthorResource(String authorResource) {
		if (authorResource == null)
			throw new NullPointerException("The given author resource is null!");
		this.authorResource = authorResource;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		if (university == null)
			throw new NullPointerException("The given university is null!");
		this.university = university;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		if (country == null)
			throw new NullPointerException("The given country is null!");
		this.country = country;
	}

	public void setPapers(List<Paper> papers) {
		this.papers = papers;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authorResource == null) ? 0 : authorResource.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((papers == null) ? 0 : papers.hashCode());
		result = prime * result
				+ ((university == null) ? 0 : university.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (authorResource == null) {
			if (other.authorResource != null)
				return false;
		} else if (!authorResource.equals(other.authorResource))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (papers == null) {
			if (other.papers != null)
				return false;
		} else if (!papers.equals(other.papers))
			return false;
		if (university == null) {
			if (other.university != null)
				return false;
		} else if (!university.equals(other.university))
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
		return "Author: " + getFirstName() + " " + getLastName();
	}
}
