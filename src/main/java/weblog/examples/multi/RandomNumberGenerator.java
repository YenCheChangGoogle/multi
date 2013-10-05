package weblog.examples.multi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class RandomNumberGenerator implements IRandomNumberGenerator {
    private Queue<RandNumber> randNumberQueue;

    public RandomNumberGenerator() {
        randNumberQueue = new LinkedList<RandNumber>();
    }

    @Override
    public RandNumber getRandomNumber() {
        synchronized (this) {
            if (randNumberQueue.size() == 0) {
                return new RandNumber.Builder().randomlyGenerate().build();
            }
            return randNumberQueue.poll();
        }
    }

    @Override
    public RandNumber[] getRandomNumbers(int num) {
        RandNumber[] numbers = new RandNumber[num];
        for (int index = 0; index < num; index++) {
            numbers[index] = getRandomNumber();
        }
        return numbers;
    }

    @Override
    public void submitRandomNumber(RandNumber randNumber) {
        synchronized (this) {
            this.randNumberQueue.add(randNumber);
        }
    }

    @Override
    public void submitRandomNumbers(RandNumber[] randNumbers) {
        synchronized (this) {
            this.randNumberQueue.addAll(Arrays.asList(randNumbers));
        }
    }
}
