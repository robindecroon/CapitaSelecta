package keywordmap;

import java.util.ArrayList;
import java.util.List;

import data.University;
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

	public static List<UniversityCluster> getClusters(
			List<University> universities, float distanceBound) {
		List<UniversityCluster> clusters = new ArrayList<UniversityCluster>();
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

				float xx = (float) Math.pow(location.getLat() - ul.getLat(),
						2.f);
				float yy = (float) Math.pow(location.getLon() - ul.getLon(),
						2.f);
				float distance = (float) Math.sqrt(xx + yy);

				if (distance < distanceBound)
					currentCluster.add(todo.remove(i));
			}

			clusters.add(new UniversityCluster(currentCluster));

		}

		return clusters;
	}
}
