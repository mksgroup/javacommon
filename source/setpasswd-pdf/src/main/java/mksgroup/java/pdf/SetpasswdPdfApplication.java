package mksgroup.java.pdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class SetpasswdPdfApplication implements ApplicationRunner {

	@Value("${inputFilepath}")
	String inputFilepath;

	@Value("${isResource}")
    private Boolean isResource;
	
	@Value("${curPasswd}")
    private String curPasswd;
	
	@Value("${outputFilepath}")
    private String outputFilepath;
	

	@Value("${ownerPassword}")
    private String ownerPassword;
	
	@Value("${userPassword}")
    private String userPassword;
	
	public static void main(String[] args) {
		SpringApplication.run(SetpasswdPdfApplication.class, args);
	}

	@Override
    public void run(ApplicationArguments args ) throws Exception
    { 
        System.out.println("OptionNames: {}" + args.getOptionNames());
        System.out.println("isResource: " + isResource);
        
        if ("_default".contentEquals(curPasswd)) {
        	curPasswd = "ThankYouVeryMuch_VoCungCamOn@1a";
        }

        PdfProtector.encrypt(inputFilepath, isResource, curPasswd, outputFilepath, ownerPassword, userPassword);
    }
}
