package weblog.examples.multi;

import java.io.InputStream;
import java.io.OutputStream;

public interface IRandomNumberSerializer {
    String serializeRandomNumber(RandNumber number);
    RandNumber deserializeRandomNumber(String input);
    void serializeRandomNumber(RandNumber number, OutputStream stream);
    RandNumber deserializeRandomNumber(InputStream stream);
    String getContentType();
}
