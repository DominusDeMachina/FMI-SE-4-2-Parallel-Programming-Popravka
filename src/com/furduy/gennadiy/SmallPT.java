package com.furduy.gennadiy;

import java.util.ArrayList;
import java.util.List;

class SmallPT {

    public static void main(String[] args) {
        RandomGenerator rng = new RandomGenerator(42);

        Camera camera = new Camera(new Vector3(50, 52, 295.6), new Vector3(0, -0.042612, -1).normalize());

        // final int nb_samples = (args.length > 0) ? Integer.parseInt(args[0]) / 4 : 1;
        final int nb_samples = 30;

        final int w = 1024;
        final int h = 768;

        List<Worker> workers = new ArrayList<>();

        Scene scene = new Scene(w, h);

        for (int y = 0; y < h; ++y) {
            // String slog = String.format("\rRendering (%1$d spp) %2$.2f%%", nb_samples *
            // 4, 100.0 * y / (h - 1));
            // System.out.print(slog);
            Worker worker = new Worker(y, nb_samples, camera, scene, rng);
            workers.add(worker);
        }

        for (Worker worker : workers) {
            worker.start();
        }

        while (true) {
            boolean isAllFinished = true;
            for (Worker worker : workers) {
                if (!worker.isFinished) {
                    isAllFinished = false;
                }
            }
            if (isAllFinished) break;
        }

        ImageIO.writePPM(w, h, scene.Ls, "java-image.ppm");
    }
}
