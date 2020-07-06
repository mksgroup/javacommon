/**
 * 
 */
package mksgroup.java.pdf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;

import mksgroup.java.common.CommonUtil;

/**
 * @author ThachLN
 *
 */
public class PdfProtector {
	public static void encrypt(String inputFilepath, boolean isResource, String curPasswd, String outputFilepath, String ownerPassword, String userPassword) throws DocumentException, IOException {
	      int permissions = 0;
	      
	      // If inputFilepath is a resource file in CLASSPATH, read resource. Otherwise, read the file.
	      InputStream is = (isResource) ?
	    		  		CommonUtil.loadResource(inputFilepath):
	    		  		new FileInputStream(inputFilepath);
	      PdfReader reader;
	      
	      // Read file pdf with or without password
	      reader = (curPasswd != null) ? new PdfReader(is, curPasswd.getBytes()):
	    	  new PdfReader(is);

	      
	      FileOutputStream fos = new FileOutputStream(outputFilepath);

	      PdfEncryptor.encrypt(reader, fos, userPassword.getBytes(), ownerPassword.getBytes(), permissions, true);
	}

}
