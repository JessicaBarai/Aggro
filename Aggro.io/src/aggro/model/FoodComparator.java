package aggro.model;
import java.io.Serializable;
import java.util.Comparator;
/**
 * This class compare food instances
 * @author grupp8
 * 
 *
 */
public class FoodComparator implements Comparator<Food>, Serializable{

	private static final long serialVersionUID = 2007000116403055116L;
	/**
	* compare food instances 
	* @param o1 the first food instance to be compared
	* @param o2 the second food instance to be compared
	* @return an integer indicating the relationship between the coordinates y values. -1 if the first coordinate is less, 0 if the coordinates are equal or 1 otherwise.
	*/
	@Override
	public int compare(Food o1, Food o2) {
		return o1.compareTo(o2.getPosition());
	}

}
