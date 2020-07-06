package mksgroup.java.pdf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;

import mksgroup.java.common.FileUtil;

class PdfProtectorTest {

	public String getTempPath() {
		String property = "java.io.tmpdir";

		// Get the temporary directory and print it.
		String tempDir = System.getProperty(property);
		System.out.println("OS temporary directory is " + tempDir);

		return tempDir;
	}

	@Test
	void testEncrypt() {
		String passwd = "ThankYouVeryMuch_VoCungCamOn@1a";

		String resourceFile = "/TouchAI10Days_Draft_YourName_yourphonenumber_free_4days.pdf";
		boolean isResource = true;

		String outputFilepath = FileUtil.buildPath(getTempPath(), "output_setpasswd-pdf.pdf");
		String ownerPassword = "ThachLN";
		String userPassword = "testing only";
		try {
			PdfProtector.encrypt(resourceFile, isResource, passwd, outputFilepath, ownerPassword, userPassword);

			// Check the result by read the output
			InputStream is = new FileInputStream(outputFilepath);
			PdfReader reader = new PdfReader(is, userPassword.getBytes());

			int numOfPages = reader.getNumberOfPages();
			System.out.println("Output of file is at :" + outputFilepath);
			System.out.println("Check number of pages:" + numOfPages);
			assertThat(numOfPages).isEqualTo(266);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	@Test
	public void givenPassword_whenHashing_thenVerifying() throws NoSuchAlgorithmException {
		String hash = "71C1CD04BFBEDD5FF0ADADC79F18941F";
		String password = "ThankYouVeryMuch_VoCungCamOn@1a";

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte[] digest = md.digest();
		String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		
		assertThat(myHash.equals(hash)).isTrue();
	}

}
