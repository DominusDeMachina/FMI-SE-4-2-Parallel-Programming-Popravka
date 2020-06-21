package com.furduy.gennadiy;

class SmallPT {

    public static void main(String[] args) {
        RandomGenerator rng = new RandomGenerator(42);

        Camera camera = new Camera(new Vector3(50, 52, 295.6), new Vector3(0, -0.042612, -1).normalize());

        final int nb_samples = (args.length > 0) ? Integer.parseInt(args[0]) / 4 : 1;

        final int w = 1024;
        final int h = 768;

        Scene scene = new Scene(w, h);

        Worker worker = new Worker(w, h, 0, 0, camera, scene, nb_samples, rng);

        worker.worker();

        ImageIO.writePPM(w, h, scene.Ls, "java-image.ppm");
    }
}
