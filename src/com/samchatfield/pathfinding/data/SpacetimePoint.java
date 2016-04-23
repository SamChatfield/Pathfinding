package com.samchatfield.pathfinding.data;

/**
 * A point with two spatial dimensions and one temporal dimension. Used for paths in multiple agent planning to indicate the time at which an
 * agent is at a given point for reservations etc.
 * @author Sam
 */
public class SpacetimePoint {
	
	private final int x, y, time;
	
	/**
	 * Create a new point at position x,y at a given time
	 * @param x x pos
	 * @param y y pos
	 * @param time time at x,y
	 */
	public SpacetimePoint(int x, int y, int time) {
		this.x = x;
		this.y = y;
		this.time = time;
	}
	
	/**
	 * Create a new point at the given node and at the given time
	 * @param n Node position
	 * @param time time at this Node
	 */
	public SpacetimePoint(Node n, int time) {
		this(n.getX(), n.getY(), time);
	}
	
	/**
	 * Get the x position of this point
	 * @return x pos
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y position of this point 
	 * @return y pos
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Get the time of this point
	 * @return time
	 */
	public int getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + time + ")";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		SpacetimePoint that = (SpacetimePoint) o;
		
		if (x != that.x)
			return false;
		if (y != that.y)
			return false;
		if (time != that.time)
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		// Hash on the x position, y position and the time
		int result = x;
		result = 31 * result + y;
		result = 31 * result + time;
		return result;
	}
	
}
