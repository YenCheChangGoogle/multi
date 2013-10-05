package weblog.examples.multi;

import java.util.Random;
import java.util.UUID;

public class RandNumber {
    private String uuid;
    private Integer number;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return number + " " + uuid;
    }

    public static class Builder {
        private RandNumber number;

        public Builder() {
            number = new RandNumber();
        }

        public Builder randomlyGenerate() {
            Random random = new Random();
            number.setNumber(random.nextInt(100));
            number.setUuid(UUID.randomUUID().toString());
            return this;
        }

        public Builder withNumber(int num) {
            number.setNumber(num);
            return this;
        }

        public Builder withUuid(String uuid) {
            number.setUuid(uuid);
            return this;
        }

        public RandNumber build() {
            return number;
        }
    }
}
