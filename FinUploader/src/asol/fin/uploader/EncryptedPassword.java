package asol.fin.uploader;

import net.sourceforge.jdpapi.DataProtector;

import org.apache.xmlbeans.impl.util.Base64;

public class EncryptedPassword {

	private final DataProtector protector;

	public EncryptedPassword() {
		this.protector = new DataProtector();
	}

	public String getEncryptedPassword(String plainPassword) {
		byte[] data = protector.protect(plainPassword);

		return new String(Base64.encode(data));
	}

	public String getDecryptedPassword(String encryptedPassword) {
		byte[] data = Base64.decode(encryptedPassword.getBytes());
     
		return protector.unprotect(data);
	}

	static {
		System.loadLibrary("jdpapi-native-1.0.1");
	}

}
