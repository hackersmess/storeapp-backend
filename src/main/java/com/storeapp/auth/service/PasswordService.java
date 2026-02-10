package com.storeapp.auth.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Service per l'hashing e la verifica delle password usando BCrypt.
 */
@ApplicationScoped
public class PasswordService {

    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hash di una password in chiaro.
     *
     * @param plainPassword password in chiaro
     * @return password hashata con BCrypt
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifica se una password in chiaro corrisponde all'hash.
     *
     * @param plainPassword password in chiaro
     * @param hashedPassword password hashata
     * @return true se la password corrisponde, false altrimenti
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
