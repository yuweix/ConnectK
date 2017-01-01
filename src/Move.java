import java.awt.Point;

public class Move {
		private int v;
		private Point p;
		
		public Move(int value, Point point){
			v = value;
			p = point;
		}
		
		public Point getP(){
			return p;
		}
		
		public int getV(){
			return v;
		}
		
		public void setP(Point point){
			p = point;
		}
		
		public void setV(int value){
			v = value;
		}
		
		public void setAll(int value,Point point){
			v=value;
			p=point;
		}
	}