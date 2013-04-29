package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fhpotsdam.unfolding.geo.Location;

/**
 * Represents a cluster of university's which are located closely to each other.
 * 
 * @author niels
 * 
 */
public class UniversityCluster {
	private final List<University> universities = new ArrayList<University>();
	private Location averageLocation;

	public UniversityCluster(University university) {
		universities.add(university);
		averageLocation = university.getLocation();
	}

	public UniversityCluster(List<University> universities) {
		float xx = 0;
		float yy = 0;

		for (University university : universities) {
			Location universityLocation = university.getLocation();
			xx += universityLocation.getLat();
			yy += universityLocation.getLon();
			this.universities.add(university);
		}

		xx /= (float) universities.size();
		yy /= (float) universities.size();

		averageLocation = new Location(xx, yy);
	}

	public Location getLocation() {
		return averageLocation;
	}

	public List<University> getUniversities() {
		return universities;
	}

	public static Set<UniversityCluster> getClusters(
			List<University> universities, float distanceBound) {
		Set<UniversityCluster> clusters = new HashSet<UniversityCluster>();

		if (distanceBound == 0.f) {
			for(University university : universities)
				clusters.add(new UniversityCluster(university));
		} else {
			List<University> todo = new ArrayList<University>(universities);

			while (todo.size() > 0) {
				// Pick the a university.
				University university = todo.remove(0);
				Location location = university.getLocation();

				// Initialize the current cluster.
				List<University> currentCluster = new ArrayList<University>();
				currentCluster.add(university);

				for (int i = todo.size() - 1; i >= 0; i--) {
					University u = todo.get(i);
					Location ul = u.getLocation();

					float xx = (float) Math.pow(
							location.getLat() - ul.getLat(), 2.f);
					float yy = (float) Math.pow(
							location.getLon() - ul.getLon(), 2.f);
					float distance = (float) Math.sqrt(xx + yy);

					if (distance < distanceBound)
						currentCluster.add(todo.remove(i));
				}

				clusters.add(new UniversityCluster(currentCluster));
			}
		}

		return clusters;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((averageLocation == null) ? 0 : averageLocation.hashCode());
		result = prime * result
				+ ((universities == null) ? 0 : universities.hashCode());
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
		UniversityCluster other = (UniversityCluster) obj;
		if (averageLocation == null) {
			if (other.averageLocation != null)
				return false;
		} else if (!averageLocation.equals(other.averageLocation))
			return false;
		if (universities == null) {
			if (other.universities != null)
				return false;
		} else if (!universities.equals(other.universities))
			return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "Cluster at "+averageLocation.toString()+" containing universities:";
		for(University university : universities)
			result+="\n"+university.getName();
		return result;
	}
}
