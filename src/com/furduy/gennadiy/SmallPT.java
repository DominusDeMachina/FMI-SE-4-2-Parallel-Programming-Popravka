package com.furduy.gennadiy;

class SmallPT {

    // Scene
    public static final double REFRACTIVE_INDEX_OUT = 1.0;
    public static final double REFRACTIVE_INDEX_IN = 1.5;
    public static final double FIELD_OF_VIEW = 0.5135;

    public static Sphere[] spheres = {
            new Sphere(1e5, new Vector3(1e5 + 1, 40.8, 81.6), new Vector3(), new Vector3(0.75, 0.25, 0.25),
                    Sphere.ReflectionType.DIFFUSE), // Left
            new Sphere(1e5, new Vector3(-1e5 + 99, 40.8, 81.6), new Vector3(), new Vector3(0.25, 0.25, 0.75),
                    Sphere.ReflectionType.DIFFUSE), // Right
            new Sphere(1e5, new Vector3(50, 40.8, 1e5), new Vector3(), new Vector3(0.75),
                    Sphere.ReflectionType.DIFFUSE), // Back
            new Sphere(1e5, new Vector3(50, 40.8, -1e5 + 170), new Vector3(), new Vector3(),
                    Sphere.ReflectionType.DIFFUSE), // Front
            new Sphere(1e5, new Vector3(50, 1e5, 81.6), new Vector3(), new Vector3(0.75),
                    Sphere.ReflectionType.DIFFUSE), // Bottom
            new Sphere(1e5, new Vector3(50, -1e5 + 81.6, 81.6), new Vector3(), new Vector3(0.75),
                    Sphere.ReflectionType.DIFFUSE), // Top
            new Sphere(16.5, new Vector3(27, 16.5, 47), new Vector3(), new Vector3(0.999),
                    Sphere.ReflectionType.SPECULAR), // Mirror
            new Sphere(16.5, new Vector3(73, 16.5, 78), new Vector3(), new Vector3(0.999),
                    Sphere.ReflectionType.REFRACTIVE), // Glass
            new Sphere(600, new Vector3(50, 681.6 - .27, 81.6), new Vector3(12), new Vector3(),
                    Sphere.ReflectionType.DIFFUSE) // Light
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

    public static Vector3 radiance(Ray ray, RandomGenerator rng) {
        Ray r = ray;
        Vector3 L = new Vector3();
        Vector3 F = new Vector3(1);

        while (true) {
            Intersection isect = new Intersection();
            if (!intersect(r, isect))
                return L;

            Sphere shape = spheres[isect.id];
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
                    Vector3 d = Specular.idealSpecularTransmit(r.direction, n, REFRACTIVE_INDEX_OUT,
                            REFRACTIVE_INDEX_IN, probability, rng);
                    F = F.mul(probability.pr);
                    r = new Ray(p, d, Sphere.EPSILON_SPHERE, Double.POSITIVE_INFINITY, r.depth + 1);
                    break;
                }
                default: {
                    Vector3 w = n.dot(r.direction) < 0 ? n : n.minus();
                    Vector3 u = (Math.abs(w.x) > 0.1 ? new Vector3(0.0, 1.0, 0.0) : new Vector3(1.0, 0.0, 0.0)).cross(w)
                            .normalize();
                    Vector3 v = w.cross(u);

                    Vector3 sample_d = Sampling.cosineWeightedSampleOnHemisphere(rng.uniformFloat(),
                            rng.uniformFloat());
                    Vector3 d = u.mul(sample_d.x).add(v.mul(sample_d.y)).add(w.mul(sample_d.z)).normalize();
                    r = new Ray(p, d, Sphere.EPSILON_SPHERE, Double.POSITIVE_INFINITY, r.depth + 1);
                    break;
                }
            }
        }
    }

    public static Vector3[] worker(int w, int h, Camera camera, int nb_samples, RandomGenerator rng) {
        Vector3[] Ls = new Vector3[w * h];
        for (int i = 0; i < w * h; ++i)
            Ls[i] = new Vector3();

        Vector3 cx = new Vector3(w * FIELD_OF_VIEW / h, 0.0, 0.0);
        Vector3 cy = cx.cross(camera.direction).normalize().mul(FIELD_OF_VIEW);


        for (int y = 0; y < h; ++y) {
            // pixel row
            String slog = String.format("\rRendering (%1$d spp) %2$.2f%%", nb_samples * 4, 100.0 * y / (h - 1));
            System.out.print(slog);
            for (int x = 0; x < w; ++x) {
                // pixel column
                for (int sy = 0, i = (h - 1 - y) * w + x; sy < 2; ++sy) {
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
                            Vector3 d = cx.mul((((sx + 0.5 + dx) / 2 + x) / w - 0.5))
                                    .add(cy.mul((((sy + 0.5 + dy) / 2 + y) / h - 0.5))).add(camera.direction);
                            L = L.add(
                                    radiance(new Ray(camera.position.add(d.mul(130)), d.normalize(), Sphere.EPSILON_SPHERE),
                                            rng).div(nb_samples));
                        }
                        Ls[i] = Ls[i].add(L.clamp(0.0, 1.0).mul(0.25));
                    }
                }
            }
        }
        return Ls;
    }

    public static void main(String[] args) {
        RandomGenerator rng = new RandomGenerator(42);

        Camera camera = new Camera(new Vector3(50, 52, 295.6), new Vector3(0, -0.042612, -1).normalize());

        final int nb_samples = (args.length > 0) ? Integer.parseInt(args[0]) / 4 : 1;

        final int w = 1024;
        final int h = 768;

        Vector3[] Ls = worker(w, h, camera, nb_samples, rng);

        ImageIO.writePPM(w, h, Ls, "java-image.ppm");
    }
}
