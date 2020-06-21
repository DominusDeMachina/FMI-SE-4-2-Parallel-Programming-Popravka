package com.furduy.gennadiy;

public class Vector3 {

	public double x, y, z;

	public Vector3() {
    	this(0);
    }
	public Vector3(double a) {
    	this(a, a, a);
    }
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3(Vector3 v) {
    	this(v.x, v.y, v.z);
    }

    @Override
    public Vector3 clone() {
        return new Vector3(this);
    }

    public Vector3 minus() {
        return new Vector3(-x, -y, -z);
    }
    public Vector3 add(Vector3 v2) {
        return new Vector3(x + v2.x, y + v2.y, z + v2.z);
    }
    public Vector3 sub(Vector3 v2) {
        return new Vector3(x - v2.x, y - v2.y, z - v2.z);
    }
    public Vector3 mul(Vector3 v2) {
        return new Vector3(x * v2.x, y * v2.y, z * v2.z);
    }
    public Vector3 mul(double a) {
        return new Vector3(x * a, y * a, z * a);
    }

    public Vector3 div(double a) {
        final double inv_a = 1.0 / a;
        return new Vector3(x * inv_a, y * inv_a, z * inv_a);
    }
    public double dot(Vector3 v) {
	    return this.x * v.x + this.y * v.y + this.z * v.z;
    }
    public Vector3 cross(Vector3 v) {
	    return new Vector3(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }

    public Vector3 clamp(double low, double high) {
        return new Vector3(MathUtils.clamp(x, low, high), MathUtils.clamp(y, low, high), MathUtils.clamp(z, low, high));
    }

    public double min() {
        return (this.x < this.y && this.x < this.z) ? this.x : ((this.y < this.z) ? this.y : this.z);
    }
    public double max() {
        return (this.x > this.y && this.x > this.z) ? this.x : ((this.y > this.z) ? this.y : this.z);
    }

    public Vector3 normalize() {
        final double a = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= a;
        this.y *= a;
        this.z *= a;
        return this;
    }

    @Override
    public String toString() {
        return "[" + this.x + ' ' + this.y + ' ' + this.z + ']';
    }
}
