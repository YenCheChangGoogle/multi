package weblog.examples.multi;

public interface IRandomNumberGenerator {
    RandNumber getRandomNumber();
    RandNumber[] getRandomNumbers(int num);
    void submitRandomNumber(RandNumber randNumber);
    void submitRandomNumbers(RandNumber[] randNumbers);
}
