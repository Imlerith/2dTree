import com.myalgos.utils.Point2D;
import com.myalgos.utils.RectHV;
import com.myalgos.utils.StdDraw;
import com.myalgos.utils.StdOut;

public class KdTreeVisualizer {

	public static void main(String[] args) {
		RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
		StdDraw.enableDoubleBuffering();
		KdTree kdtree = new KdTree();
		while (true) {
			if (StdDraw.isMousePressed()) {
				double x = StdDraw.mouseX();
				double y = StdDraw.mouseY();
				StdOut.printf("%8.6f %8.6f\n", x, y);
				Point2D p = new Point2D(x, y);
				if (rect.contains(p)) {
					StdOut.printf("%8.6f %8.6f\n", x, y);
					kdtree.insert(p);
					StdDraw.clear();
					kdtree.draw();
					StdDraw.show();
				}
			}
			StdDraw.pause(20);
		}

	}
}