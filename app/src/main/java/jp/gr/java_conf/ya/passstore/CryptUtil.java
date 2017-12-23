package jp.gr.java_conf.ya.passstore; // Copyright (c) 2017 YA<ya.androidapp@gmail.com> All rights reserved.

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class CryptUtil {
    private static final String TAG = "PassStore";

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String CHARSET = "UTF-8";
    private static final String KEY_PROVIDER = "AndroidKeyStore";
    private static final String KEY_ALIAS = "PassStoreKey1";

    private static KeyStore mKeyStore = null;

    public static void createNewKey() {
        try {
            if (!mKeyStore.containsAlias(KEY_ALIAS)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER);
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(
                                KEY_ALIAS,
                                KeyProperties.PURPOSE_DECRYPT)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                                .build());
                keyPairGenerator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static String decryptString(String encryptedText) {
        String plainText = null;
        try {
            PrivateKey privateKey = (PrivateKey) mKeyStore.getKey(KEY_ALIAS, null);

            OAEPParameterSpec sp = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, sp);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT));
            CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStream, cipher);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int b;
            while ((b = cipherInputStream.read()) != -1)
                outputStream.write(b);
            outputStream.close();
            plainText = outputStream.toString(CHARSET);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return plainText;
    }

    public static String encryptString(String plainText) {
        String encryptedText = null;
        try {
            PublicKey publicKey = mKeyStore.getCertificate(KEY_ALIAS).getPublicKey();

            OAEPParameterSpec sp = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, sp);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
            cipherOutputStream.write(plainText.getBytes(CHARSET));
            cipherOutputStream.close();

            byte [] bytes = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return encryptedText;
    }

    public static boolean isSet() {
        try {
            return mKeyStore.containsAlias(KEY_ALIAS);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    public static void prepareKeyStore() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            createNewKey();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public static boolean resetPw(){
        try {
            mKeyStore.deleteEntry(KEY_ALIAS);
            mKeyStore.setCertificateEntry(KEY_ALIAS, null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
}
