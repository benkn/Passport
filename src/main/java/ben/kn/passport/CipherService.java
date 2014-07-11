package ben.kn.passport;

import ben.kn.dps.EncryptionService;
import ben.kn.passport.lang.PassportException;

/**
 * Default access class for using the encryption and decryption services
 * provided by DPS.
 * 
 * @author Ben (bknear@gmail.com)
 * @since Nov 21, 2012
 */
public class CipherService {

	public CipherService(String passphrase, String salt) {
		if ( passphrase != null && passphrase.trim().length() > 0 && salt != null
				&& salt.trim().length() > 0 ) {
			EncryptionService.setSalt(salt);
		}
	}

	public String encryptPassword(String passphrase) throws PassportException {
		if ( passphrase != null && passphrase.length() > 0 ) {
			try {
				return EncryptionService.encrypt(passphrase);
			} catch (Exception e) {
				throw new PassportException("Failed to execute cipher: " + e.getMessage());
			}
		}

		else {
			return null;
		}
	}

	public String decrypt(String passphrase) throws PassportException {
		if ( passphrase != null && passphrase.length() > 0 ) {
			try {
				return EncryptionService.decrypt(passphrase);
			} catch (Exception e) {
				throw new PassportException("Failed to execute cipher: " + e.getMessage());
			}
		}

		else {
			return null;
		}
	}

	public static void main(String[] args) throws PassportException {
		CipherService cipherService = new CipherService(null, null);
		// Default value
		String testText = "the wild cat";

		if ( args == null || args.length != 1 ) {
			System.out.println("Usage: CipherService [string to encrypt]");
			System.out
					.println("Since invalid arguments were provided, a sample will be provided.\n");
		} else {
			testText = args[0];
		}

		System.out.println("Starting with \"" + testText + "\"");
		String e = cipherService.encryptPassword(testText);
		System.out.println("Encrypting to " + e);
		String d = cipherService.decrypt(e);
		System.out.println("Decrypting to " + d);
	}
}