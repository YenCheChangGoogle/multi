package weblog.examples.multi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;
import sun.awt.image.BufferedImageDevice;

import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.List;

public class RandomNumberGeneratorProxy implements IRandomNumberGenerator {

    private static Logger log = Logger.getLogger(IRandomNumberGenerator.class);

    private IRandomNumberSerializer randomNumberSerializer;
    private HttpClient httpClient;
    private String getOneUrl;
    private String getMultipleUrl;
    private String submitOneUrl;
    private String submitMultipleUrl;

    public RandomNumberGeneratorProxy(HttpClient client,
                               IRandomNumberSerializer randomNumberSerializer,
                               String getOneUrl,
                               String getMultipleUrl,
                               String submitOneUrl,
                               String submitMultipleUrl) {
        this.httpClient = client;
        this.randomNumberSerializer = randomNumberSerializer;
        this.getOneUrl = getOneUrl;
        this.getMultipleUrl = getMultipleUrl;
        this.submitOneUrl = submitOneUrl;
        this.submitMultipleUrl = submitMultipleUrl;
    }

    @Override
    public RandNumber getRandomNumber() {
        HttpGet httpGet = new HttpGet(getOneUrl);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                RandNumber number = randomNumberSerializer.deserializeRandomNumber(
                        httpResponse.getEntity().getContent()
                );
                return number;
            } else {
                HttpEntity httpDataEntity = httpResponse.getEntity();
                EntityUtils.consumeQuietly(httpDataEntity);
                return null;
            }
        } catch (Exception ex) {
            log.error("Failed to get random number", ex);
            return null;
        }
    }

    @Override
    public RandNumber[] getRandomNumbers(int num) {
        HttpGet httpGet = new HttpGet(getMultipleUrl);
        httpGet.setHeader("MAX_NUM_NUMS", Integer.toString(num));
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayDataSource source = new ByteArrayDataSource(httpResponse.getEntity().getContent(),
                        "multipart/mixed");
                MimeMultipart multipart = new MimeMultipart(source);

                List<RandNumber> numbers = new ArrayList<RandNumber>();

                for (int index = 0; index < multipart.getCount(); index++) {
                    numbers.add(randomNumberSerializer.deserializeRandomNumber(multipart.getBodyPart(index).getInputStream()));
                }
                return numbers.toArray(new RandNumber[numbers.size()]);
            } else {
                HttpEntity httpDataEntity = httpResponse.getEntity();
                EntityUtils.consumeQuietly(httpDataEntity);
                return null;
            }
        } catch (Exception ex) {
            log.error("Failed to get multiple random numbers", ex);
            return null;
        }
    }

    @Override
    public void submitRandomNumber(RandNumber randNumber) {
        HttpPost httpPost = new HttpPost(submitOneUrl);
        try {
            httpPost.setEntity(
                    new StringEntity(randomNumberSerializer.serializeRandomNumber(randNumber))
            );

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpDataEntity = httpResponse.getEntity();
            EntityUtils.consumeQuietly(httpDataEntity);
        } catch (Exception ex) {
            log.error("Failed to get random number", ex);
        }
    }

    @Override
    public void submitRandomNumbers(RandNumber[] randNumbers) {
        HttpPost httpPost = new HttpPost(submitMultipleUrl);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            for (RandNumber number : randNumbers) {
                builder.addTextBody(number.getUuid(),
                        randomNumberSerializer.serializeRandomNumber(number));
            }

            httpPost.setEntity(builder.build());

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpDataEntity = httpResponse.getEntity();
            EntityUtils.consumeQuietly(httpDataEntity);
        } catch (Exception ex) {
            log.error("Failed to get random number", ex);
        }
    }

    public static class Builder {
        private IRandomNumberSerializer randomNumberSerializer;
        private HttpClient httpClient;
        private String getOneUrl;
        private String getMultipleUrl;
        private String submitOneUrl;
        private String submitMultipleUrl;

        public Builder() {

        }

        public Builder withHttpClient(HttpClient client) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder withDefaultHttpClient() {
            this.httpClient = new DefaultHttpClient();
            return this;
        }

        public Builder usesRandomSerializer(IRandomNumberSerializer serializer) {
            this.randomNumberSerializer = serializer;
            return this;
        }

        public Builder withGetOneUrl(String url) {
            this.getOneUrl = url;
            return this;
        }

        public Builder withGetMultipleUrl(String url) {
            this.getMultipleUrl = url;
            return this;
        }

        public Builder withSubmitOneUrl(String url) {
            this.submitOneUrl = url;
            return this;
        }

        public Builder withSubmitMultipleUrl(String url) {
            this.submitMultipleUrl = url;
            return this;
        }

        public RandomNumberGeneratorProxy build() {
            return new RandomNumberGeneratorProxy(
                    this.httpClient,
                    this.randomNumberSerializer,
                    this.getOneUrl,
                    this.getMultipleUrl,
                    this.submitOneUrl,
                    this.submitMultipleUrl
            );
        }
    }
}
