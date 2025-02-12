package com.oous.authorizationserver.util;

import com.oous.authorizationserver.domain.response.exception.service.CryptoException;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@UtilityClass
public class CryptoUtils {

    public byte[] pbkdf(String password, byte[] salt, int derivedKeyLength, int iterations) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory keyFactory;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException ex) {
            throw new CryptoException("error.crypto.unsupported.algorithm");
        }

        try {
            return keyFactory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException ex) {
            throw new CryptoException("error.crypto.invalid.key.spec");
        }
    }
}
