package com.furduy.gennadiy;

public class Sphere {
    public enum ReflectionType { DIFFUSE, SPECULAR, REFRACTIVE };

    public static final double EPSILON_SPHERE = 1e-4;

    public double radius;
    public Vector3 center;
    public Vector3 emission;
    public Vector3 color;
    public ReflectionType reflection_t;

    public Sphere(double radius, Vector3 center, Vector3 emission, Vector3 color, ReflectionType reflectionType) {
        this.radius = radius;
        this.center = center.clone();
        this.emission = emission.clone();
        this.color = color.clone();
        this.reflection_t = reflectionType;
    }

    public boolean intersect(Ray ray) {

        final Vector3 op = center.sub(ray.origin);
        final double dop = ray.direction.dot(op);
        final double D = dop * dop - op.dot(op) + radius * radius;

	    if (D < 0)
		    return false;

	    final double sqrtD = Math.sqrt(D);

        final double tmin = dop - sqrtD;
	    if (ray.tmin < tmin && tmin < ray.tmax) {
		    ray.tmax = tmin;
		    return true;
	    }

        final double tmax = dop + sqrtD;
	    if (ray.tmin < tmax && tmax < ray.tmax) {
		    ray.tmax = tmax;
		    return true;
	    }

	    return false;
    }
}
