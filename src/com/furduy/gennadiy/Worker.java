package com.furduy.gennadiy;

public class Worker extends Thread {
  int y;
  int nb_samples;
  Camera camera;
  Scene scene;
  RandomGenerator rng;
  public boolean isFinished;
  int id;

  public Worker(int y, int nb_samples, Camera camera, Scene scene, RandomGenerator rng) {
    this.y = y;
    this.nb_samples = nb_samples;
    this.camera = camera;
    this.scene = scene;
    this.rng = rng;
    this.id = y;

    isFinished = true;
  }

  public static Vector3 radiance(Ray ray, RandomGenerator rng) {
    Ray r = ray;
    Vector3 L = new Vector3();
    Vector3 F = new Vector3(1);

    while (true) {
      Intersection isect = new Intersection();
      if (!Scene.intersect(r, isect))
        return L;

      Sphere shape = Scene.spheres[isect.id];
      Vector3 p = r.eval(r.tmax);
      Vector3 n = p.sub(shape.center).normalize();

      L = L.add(F.mul(shape.emission));
      F = F.mul(shape.color);

      // Russian roulette
      if (r.depth > 4) {
        final double continue_probability = shape.color.max();
        if (rng.uniformFloat() >= continue_probability)
          return L;
        F = F.div(continue_probability);
      }

      // Next path segment
      switch (shape.reflection_t) {
        case SPECULAR: {
          Vector3 d = Specular.idealSpecularReflect(r.direction, n);
          r = new Ray(p, d, Sphere.EPSILON_SPHERE, Double.POSITIVE_INFINITY, r.depth + 1);
          break;
        }
        case REFRACTIVE: {
          Probability probability = new Probability();
          Vector3 d = Specular.idealSpecularTransmit(r.direction, n, Scene.REFRACTIVE_INDEX_OUT,
              Scene.REFRACTIVE_INDEX_IN, probability, rng);
          F = F.mul(probability.pr);
          r = new Ray(p, d, Sphere.EPSILON_SPHERE, Double.POSITIVE_INFINITY, r.depth + 1);
          break;
        }
        default: {
          Vector3 w = n.dot(r.direction) < 0 ? n : n.minus();
          Vector3 u = (Math.abs(w.x) > 0.1 ? new Vector3(0.0, 1.0, 0.0) : new Vector3(1.0, 0.0, 0.0)).cross(w)
              .normalize();
          Vector3 v = w.cross(u);

          Vector3 sample_d = Sampling.cosineWeightedSampleOnHemisphere(rng.uniformFloat(), rng.uniformFloat());
          Vector3 d = u.mul(sample_d.x).add(v.mul(sample_d.y)).add(w.mul(sample_d.z)).normalize();
          r = new Ray(p, d, Sphere.EPSILON_SPHERE, Double.POSITIVE_INFINITY, r.depth + 1);
          break;
        }
      }
    }
  }

  @Override
  public void run() {

    isFinished = false;

    Vector3 cx = new Vector3(scene.w * Scene.FIELD_OF_VIEW / scene.h, 0.0, 0.0);
    Vector3 cy = cx.cross(camera.direction).normalize().mul(Scene.FIELD_OF_VIEW);

    for (int x = 0; x < scene.w; ++x) {
      // pixel column
      for (int sy = 0, i = (scene.h - 1 - y) * scene.w + x; sy < 2; ++sy) {
        // 2 subpixel row
        for (int sx = 0; sx < 2; ++sx) {
          // 2 subpixel column
          Vector3 L = new Vector3();
          for (int s = 0; s < nb_samples; ++s) {
            // samples per subpixel
            final double u1 = 2.0 * rng.uniformFloat();
            final double u2 = 2.0 * rng.uniformFloat();
            final double dx = u1 < 1 ? Math.sqrt(u1) - 1.0 : 1.0 - Math.sqrt(2.0 - u1);
            final double dy = u2 < 1 ? Math.sqrt(u2) - 1.0 : 1.0 - Math.sqrt(2.0 - u2);
            Vector3 d = cx.mul((((sx + 0.5 + dx) / 2 + x) / scene.w - 0.5))
                .add(cy.mul((((sy + 0.5 + dy) / 2 + y) / scene.h - 0.5))).add(camera.direction);
            L = L.add(radiance(new Ray(camera.position.add(d.mul(130)), d.normalize(), Sphere.EPSILON_SPHERE), rng)
                .div(nb_samples));
          }
          scene.Ls[i] = scene.Ls[i].add(L.clamp(0.0, 1.0).mul(0.25));
        }
      }
    }

    isFinished = true;
  }
}