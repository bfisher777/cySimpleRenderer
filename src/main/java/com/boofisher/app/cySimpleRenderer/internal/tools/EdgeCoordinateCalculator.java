package com.boofisher.app.cySimpleRenderer.internal.tools;

import com.boofisher.app.cySimpleRenderer.internal.geometric.Vector3;

public class EdgeCoordinateCalculator {
	private static final double MIN_LENGTH = Double.MIN_NORMAL;
	
	// Generates coordinates along a straight edge, broken up into the specified number of segments
	public static Vector3[] generateStraightEdgeCoordinates(Vector3 start, Vector3 end, int segments) {
		
		Vector3 offset = end.subtract(start);
		double offsetLength = offset.magnitude();
		double segmentLength = offsetLength / segments;
		
		Vector3 offsetDirection = offset.normalize();
		
		Vector3[] coordinates = new Vector3[segments + 1];
		
		coordinates[0] = start.copy();
		for (int i = 1; i <= segments; i++) {
			coordinates[i] = start.plus(offsetDirection.multiply(segmentLength * i));
		}
		
		return coordinates;
	}
	
	// Generates coordinates along a straight edge, governed by the distance between the coordinates
	public static Vector3[] generateStraightEdgeSparseCoordinates(Vector3 start, Vector3 end, double distance) {
		
		Vector3 offset = end.subtract(start);
		double offsetLength = offset.magnitude();
		
		Vector3 offsetDirection = offset.normalize();
		Vector3 middlePoint = start.towards(end, 0.5);
		
		int halfPoints = (int) (offsetLength / distance / 2);
		
		Vector3[] coordinates = new Vector3[2 * halfPoints + 1];
		
		// Coordinates from middle point to start, not including start
		for (int i = 0; i < halfPoints; i++ ) {
			coordinates[halfPoints - i] = middlePoint.plus(offsetDirection.multiply(-i * distance));
		}
		
		// Coordinates from middle point to end, not including the middle point nor end
		for (int i = 1; i < halfPoints; i++ ) {
			coordinates[halfPoints + i] = middlePoint.plus(offsetDirection.multiply(i * distance));
		}
		
		// Add the start and end coordinates
		coordinates[0] = start.copy();
		coordinates[coordinates.length - 1] = end.copy();
		
		return coordinates;
	}
	
	/**
	 * Generates points along the arc of a circle connecting 2 positions in 3D space, given
	 * the desired arc of the circle, the angle to rotate the arc about the displacement axis
	 * by the right-hand rule from the positive z-axis, and the number of points desired.
	 * 
	 * @param start The starting position of the arc
	 * @param end The terminal position of the arc
	 * @param circleCenter The center of the circle used to generate the arc
	 * @param segments The number of straight segments to divide the arc into. The number
	 * of points returned is equal to the number of segments + 1. Must be at least 1.
	 * @param invert When set to true, generates the coordinates of the arc spanning greater
	 * than 180 degrees of the circle, instead of the smaller obtuse or acute arc. This
	 * can be useful for drawing edges from an edge to itself.
	 * @return An array of position vectors representing the arc, where the first point
	 * is equal to the start of the arc, and the last point is equal to the end of the arc.
	 */
	public static Vector3[] generateArcEdgeCoordinates(Vector3 start, Vector3 end, 
			Vector3 circleCenter, int segments, boolean invert) {
		
		Vector3[] arcCoordinates = new Vector3[segments + 1];
		
		Vector3 startOffset = start.subtract(circleCenter);
		Vector3 endOffset = end.subtract(circleCenter);
		Vector3 rotationNormal;
		
		// Self-edge suspected, use default normal to avoid division by 0
		if (start.distanceSquared(end) < MIN_LENGTH) {
			rotationNormal = startOffset.cross(new Vector3(0, 1, 0));
		} else {
			rotationNormal = startOffset.cross(endOffset);
		}
		
		double arcAngle = startOffset.angle(endOffset);
		double rotation = arcAngle / segments;
		
		// Invert the angle and the rotation direction if needed
		if (invert) {
			arcAngle = 2 * Math.PI - arcAngle;
			rotation = -arcAngle / segments;
		}
		
		for (int i = 0; i < segments; i++) {
			arcCoordinates[i] = circleCenter.plus(startOffset.rotate(rotationNormal, rotation * i));
		}
		
		arcCoordinates[arcCoordinates.length - 1] = end.copy();

		
		return arcCoordinates;
	}	
	
	
}
