package com.furduy.gennadiy;

public class RandomGenerator {
    protected java.util.Random rnd;

    public RandomGenerator(int seed) {
        seed(seed);
    }

    public void seed(int seed) {
        this.rnd = new java.util.Random(seed);
    }

    public double uniformFloat() {
        return this.rnd.nextDouble();
    }
}
