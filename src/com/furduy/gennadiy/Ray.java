package com.furduy.gennadiy;

public class Ray {

	public Vector3 origin;
    public Vector3 direction;
    public double tmin;
    public double tmax;
    public int depth;

    public Ray(Vector3 origin, Vector3 direction) {
        this(origin, direction, 0);
    }
    public Ray(Vector3 origin, Vector3 direction, double tmin) {
        this(origin, direction, tmin, Double.POSITIVE_INFINITY, 0);
    }
    public Ray(Vector3 origin, Vector3 direction, double tmin, double tmax, int depth) {
        this.origin = origin.clone();
        this.direction = direction.clone();
        this.tmin = tmin;
        this.tmax = tmax;
        this.depth = depth;
    }

    public Vector3 eval(double t) {
        return Vector3.add(this.origin, Vector3.mul(this.direction,  t));
    }

    @Override
    public String toString() {
        return "o: " + origin.toString() + '\n' + "d: " + direction.toString() + '\n';
    }
}