import java.util.LinkedList;
import java.util.Scanner;

import com.myalgos.utils.Point2D;
import com.myalgos.utils.RectHV;

public class App {

	public static void main(String[] args) {

		KdTree kdtree = new KdTree();

		System.out.println("Enter the number of points you want to add:");
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		System.out.println("Enter " + n + " points:");
		for (int i = 0; i < n; i++) {
			double x = in.nextDouble();
			double y = in.nextDouble();
			Point2D p = new Point2D(x, y);
			kdtree.insert(p);
			System.out.println("Point " + p.toString() + " added");
		}

		System.out.println("Enter the search box:");
		double xmin = in.nextDouble();
		double ymin = in.nextDouble();
		double xmax = in.nextDouble();
		double ymax = in.nextDouble();
		System.out.println("Box (" + xmin + ", " + ymin + "), (" + xmax + ", " + ymax + ") set");

		RectHV search_box = new RectHV(xmin, ymin, xmax, ymax);
		LinkedList<Point2D> points_in_box = (LinkedList<Point2D>) kdtree.range(search_box);

		System.out.println("Points in the box:");
		for (Point2D point : points_in_box) {
			System.out.println(point.toString());
		}

		System.out.println("Enter the query point for nearest-neighbor search:");
		double xquery = in.nextDouble();
		double yquery = in.nextDouble();
		Point2D p_query = new Point2D(xquery, yquery);
		System.out.println("Query point " + p_query.toString() + " entered");

		Point2D p_nearest = kdtree.nearest(p_query);
		System.out.println("The nearest point to " + p_query.toString() + " is " + p_nearest.toString());

		in.close();
		kdtree.draw();

	}

}
