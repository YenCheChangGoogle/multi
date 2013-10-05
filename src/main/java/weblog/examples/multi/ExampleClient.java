package weblog.examples.multi;

public class ExampleClient {
    public static void main(String[] args) throws Exception {
        RandomNumberGeneratorProxy.Builder builder = new RandomNumberGeneratorProxy.Builder();

        String baseUrl = "http://localhost:8080/multi-1.0-SNAPSHOT/";

        builder.withDefaultHttpClient()
               .usesRandomSerializer(new RandomNumberSerializer())
               .withGetOneUrl(baseUrl + "get-one")
               .withGetMultipleUrl(baseUrl + "get-multi")
               .withSubmitOneUrl(baseUrl + "submit-one")
               .withSubmitMultipleUrl(baseUrl + "submit-multi");
        IRandomNumberGenerator generator = builder.build();

        RandNumber number = generator.getRandomNumber();
        System.out.println(number);

        RandNumber[] numbers = generator.getRandomNumbers(5);
        for (RandNumber num : numbers) {
            System.out.println(num);
        }

        System.out.println();

        generator.submitRandomNumber(number);
        generator.submitRandomNumbers(numbers);

        number = generator.getRandomNumber();
        System.out.println(number);

        numbers = generator.getRandomNumbers(5);
        for (RandNumber num : numbers) {
            System.out.println(num);
        }
    }
}
