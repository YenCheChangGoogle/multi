package weblog.examples.multi;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.Multipart;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.OutputStream;
import java.util.Collection;
import java.util.UUID;

@Controller
public class MultiPartRequestServlet {

    private static Logger log = Logger.getLogger(MultiPartRequestServlet.class);

    @Autowired
    private IRandomNumberSerializer randomNumberSerializer;
    @Autowired

    private IRandomNumberGenerator randomNumberGenerator;

    @RequestMapping(value="/get-multi", method = RequestMethod.GET)
    public void getMultiple(
            @RequestHeader("MAX_NUM_NUMS") int maxCount,
            HttpServletRequest request,
            HttpServletResponse response,
            ServletOutputStream outputStream) throws Exception {
        log.info("multi part get is called");
        response.setStatus(HttpServletResponse.SC_OK);
        String boundary = UUID.randomUUID().toString();
        response.setContentType("multipart/x-mixed-replace;boundary=" + boundary);

        RandNumber[] numbers = randomNumberGenerator.getRandomNumbers(maxCount);

        for (int index = 0; index < maxCount; index++) {
            outputStream.println("--" + boundary);
            outputStream.println("Content-Type: " + randomNumberSerializer.getContentType());
            outputStream.println();

            outputStream.println(randomNumberSerializer.serializeRandomNumber(numbers[index]));

            outputStream.println();
        }
        outputStream.println("--" + boundary + "--");
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value="/submit-multi", method = RequestMethod.POST)
    public void submitMultiple(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("multi part submit is called");
        if (!request.getContentType().startsWith("multipart/form-data")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            request.setAttribute("org.eclipse.multipartConfig",
                    new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                RandNumber number = randomNumberSerializer.deserializeRandomNumber(part.getInputStream());
                randomNumberGenerator.submitRandomNumber(number);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @RequestMapping(value="/get-one", method = RequestMethod.GET)
    public void getOne(HttpServletRequest request, HttpServletResponse response, OutputStream outputStream) throws Exception {
        log.info("get is called");
        RandNumber number = randomNumberGenerator.getRandomNumber();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(randomNumberSerializer.getContentType());
        randomNumberSerializer.serializeRandomNumber(number, outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value="/submit-one", method = RequestMethod.POST)
    public void submitOne(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("submit is called");
        RandNumber number = randomNumberSerializer.deserializeRandomNumber(request.getInputStream());
        randomNumberGenerator.submitRandomNumber(number);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
