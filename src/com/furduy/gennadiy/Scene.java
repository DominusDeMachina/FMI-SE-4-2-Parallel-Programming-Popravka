package com.furduy.gennadiy;

public class Scene {
  public static final double REFRACTIVE_INDEX_OUT = 1.0;
  public static final double REFRACTIVE_INDEX_IN = 1.5;
  public static final double FIELD_OF_VIEW = 0.5135;

  public Vector3[] Ls;
  public int w;
  public int h;

  public static Sphere[] spheres = {
      new Sphere(1e5, new Vector3(1e5 + 1, 40.8, 81.6), new Vector3(), new Vector3(0.75, 0.25, 0.25),
          Sphere.ReflectionType.DIFFUSE), // Left
      new Sphere(1e5, new Vector3(-1e5 + 99, 40.8, 81.6), new Vector3(), new Vector3(0.25, 0.25, 0.75),
          Sphere.ReflectionType.DIFFUSE), // Right
      new Sphere(1e5, new Vector3(50, 40.8, 1e5), new Vector3(), new Vector3(0.75), Sphere.ReflectionType.DIFFUSE), // Back
      new Sphere(1e5, new Vector3(50, 40.8, -1e5 + 170), new Vector3(), new Vector3(), Sphere.ReflectionType.DIFFUSE), // Front
      new Sphere(1e5, new Vector3(50, 1e5, 81.6), new Vector3(), new Vector3(0.75), Sphere.ReflectionType.DIFFUSE), // Bottom
      new Sphere(1e5, new Vector3(50, -1e5 + 81.6, 81.6), new Vector3(), new Vector3(0.75),
          Sphere.ReflectionType.DIFFUSE), // Top
      new Sphere(16.5, new Vector3(27, 16.5, 47), new Vector3(), new Vector3(0.999), Sphere.ReflectionType.SPECULAR), // Mirror
      new Sphere(16.5, new Vector3(73, 16.5, 78), new Vector3(), new Vector3(0.999), Sphere.ReflectionType.REFRACTIVE), // Glass
      new Sphere(600, new Vector3(50, 681.6 - .27, 81.6), new Vector3(12), new Vector3(), Sphere.ReflectionType.DIFFUSE) // Light
  };

  public static boolean intersect(Ray ray, Intersection isect) {
    boolean hit = false;
    for (int i = 0; i < spheres.length; ++i) {
      if (spheres[i].intersect(ray)) {
        hit = true;
        isect.id = i;
      }
    }
    return hit;
  }

  public Scene(int w, int h) {
    this.w = w;
    this.h = h;
    Ls = new Vector3[w * h];
    for (int i = 0; i < w * h; ++i)
      Ls[i] = new Vector3();
  }
}