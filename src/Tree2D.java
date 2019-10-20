import java.util.LinkedList;

import com.myalgos.utils.Point2D;
import com.myalgos.utils.RectHV;
import com.myalgos.utils.StdDraw;



public class Tree2D {

	private Node2D root;

	private class Node2D {
		private Node2D left, right;
		private boolean split_is_vertical;
		private double x;
		private double y;

		public Node2D(double x, double y, boolean split_is_vertical) {
			this.x = x;
			this.y = y;
			this.split_is_vertical = split_is_vertical;
		}
	}

	private int N;
	private static final RectHV RECTANGLE = new RectHV(0.0, 0.0, 1.0, 1.0);

	// construct an empty set of points
	public Tree2D() {
		N = 0;
		root = null;
	}

	// is the set empty?
	public boolean isEmpty() {
		return size() == 0;
	}

	// number of points in the set
	public int size() {
		return N;
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		root = insert(root, p, true);
	}

	private Node2D insert(Node2D node, Point2D p, boolean split_is_vertical) {
		if (node == null) {
			N++;
			return new Node2D(p.x(), p.y(), split_is_vertical);
		}

		if (node.x == p.x() && node.y == p.y()) {
			return node;
		}

		// if the point to be inserted has a smaller x-coordinate than the point at the
		// root, go left
		// if the point to be inserted has a smaller y-coordinate than the point in the
		// node, go left
		if (node.split_is_vertical && p.x() < node.x || !node.split_is_vertical && p.y() < node.y) {
			node.left = insert(node.left, p, !node.split_is_vertical);
		} else {
			node.right = insert(node.right, p, !node.split_is_vertical);
		}

		return node;
	}

	// does the set contain point p?
	public boolean contains(Point2D p) {
		return contains(root, p.x(), p.y());
	}

	private boolean contains(Node2D node, double x, double y) {
		if (node == null) {
			return false;
		}

		if (node.x == x && node.y == y) {
			return true;
		}
		// recursively go left or right in the subtree depending on whether
		// the node is to the left or to the right of the split line
		if (node.split_is_vertical && x < node.x || !node.split_is_vertical && y < node.y) {
			return contains(node.left, x, y);
		} else {
			return contains(node.right, x, y);
		}

	}

	// draw all points to standard draw
	public void draw() {
		StdDraw.setScale(0, 1);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius();
		RECTANGLE.draw();
		draw(root, RECTANGLE);
	}

	private void draw(Node2D node, RectHV rectangle) {
		if (node == null)
			return;

		// draw the node point
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.01);
		new Point2D(node.x, node.y).draw();

		// get the min and max points of the split
		Point2D min, max;
		if (node.split_is_vertical) {
			StdDraw.setPenColor(StdDraw.RED);
			min = new Point2D(node.x, rectangle.ymin());
			max = new Point2D(node.x, rectangle.ymax());
			// draw the split
			StdDraw.setPenRadius();
			min.drawTo(max);
		} else {
			StdDraw.setPenColor(StdDraw.BLUE);
			min = new Point2D(rectangle.xmin(), node.y);
			max = new Point2D(rectangle.xmax(), node.y);
			// draw the split
			StdDraw.setPenRadius();
			min.drawTo(max);
		}

		// recursively draw points
		if (node.split_is_vertical) {
			// draw to the right and left of the node point along the vertical split
			draw(node.right, new RectHV(node.x, rectangle.ymin(), rectangle.xmax(), rectangle.ymax()));
			draw(node.left, new RectHV(rectangle.xmin(), rectangle.ymin(), node.x, rectangle.ymax()));
		} else {
			// draw to the right and left of the node point along the horizontal split
			draw(node.right, new RectHV(rectangle.xmin(), node.y, rectangle.xmax(), rectangle.ymax()));
			draw(node.left, new RectHV(rectangle.xmin(), rectangle.ymin(), rectangle.xmax(), node.y));
		}
	}

	// all points that are inside the rectangle (or on the boundary)
	public Iterable<Point2D> range(RectHV rect) {
		LinkedList<Point2D> points = new LinkedList<>();
		range(root, RECTANGLE, rect, points);
		return points;
	}

	private void range(Node2D node, RectHV nrect, RectHV qrect, LinkedList<Point2D> points) {
		if (node == null)
			return;
		if (qrect.intersects(nrect)) {
			Point2D p = new Point2D(node.x, node.y);
			if (qrect.contains(p)) {
				points.add(p);
			}
			if (node.split_is_vertical) {
				// create a new node rectangle and search to the right and to the left of the
				// (vertical) node split
				RectHV nrect_right = new RectHV(node.x, nrect.ymin(), nrect.xmax(), nrect.ymax());
				RectHV nrect_left = new RectHV(nrect.xmin(), nrect.ymin(), node.x, nrect.ymax());
				range(node.right, nrect_right, qrect, points);
				range(node.left, nrect_left, qrect, points);
			} else {
				// create a new node rectangle and search above and below the (horizontal) node
				// split
				RectHV nrect_above = new RectHV(nrect.xmin(), node.y, nrect.xmax(), nrect.ymax());
				RectHV nrect_below = new RectHV(nrect.xmin(), nrect.ymin(), nrect.xmax(), node.y);
				range(node.right, nrect_above, qrect, points);
				range(node.left, nrect_below, qrect, points);
			}
		}
	}

	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(final Point2D p) {
		return nearest(root, RECTANGLE, p.x(), p.y(), null);
	}

	private Point2D nearest(Node2D node, RectHV nrect, double x, double y, Point2D champion) {
		if (node == null)
			return champion;
		double dist_to_champion = 0.0;
		double dist_to_nrect = 0.0;
		RectHV nrect_left = null;
		RectHV nrect_right = null;
		Point2D p_query = new Point2D(x, y); // query point
		Point2D nearest = champion;

		if (nearest != null) {
			dist_to_champion = p_query.distanceSquaredTo(nearest); // distance to the current champion
			dist_to_nrect = nrect.distanceSquaredTo(p_query); // distance to the node rectangle
		}

		// only seach a node if the distance to the node rectangle is smaller than the
		// best distance found so far
		if (nearest == null || dist_to_champion > dist_to_nrect) {
			Point2D champion_candidate = new Point2D(node.x, node.y);

			// check the distance to the champion candidate
			if (nearest == null || dist_to_champion > p_query.distanceSquaredTo(champion_candidate))
				nearest = champion_candidate;

			// determine the rectangles where to search depending on the split direction
			if (node.split_is_vertical) {
				nrect_left = new RectHV(nrect.xmin(), nrect.ymin(), node.x, nrect.ymax());
				nrect_right = new RectHV(node.x, nrect.ymin(), nrect.xmax(), nrect.ymax());
				if (x < node.x) {
					// choose the subtree that is on the same side of the splitting line as the
					// query point
					// as the first subtree to explore: may prune the second subtree in the process!
					nearest = nearest(node.left, nrect_left, x, y, nearest);
					nearest = nearest(node.right, nrect_right, x, y, nearest);
				} else {
					nearest = nearest(node.right, nrect_right, x, y, nearest);
					nearest = nearest(node.left, nrect_left, x, y, nearest);
				}
			} else {
				nrect_left = new RectHV(nrect.xmin(), nrect.ymin(), nrect.xmax(), node.y);
				nrect_right = new RectHV(nrect.xmin(), node.y, nrect.xmax(), nrect.ymax());
				if (y < node.y) {
					// choose the subtree that is on the same side of the splitting line as the
					// query point
					// as the first subtree to explore: may prune the second subtree in the process!
					nearest = nearest(node.left, nrect_left, x, y, nearest);
					nearest = nearest(node.right, nrect_right, x, y, nearest);
				} else {
					nearest = nearest(node.right, nrect_right, x, y, nearest);
					nearest = nearest(node.left, nrect_left, x, y, nearest);
				}
			}
		}

		return nearest;
	}

}

