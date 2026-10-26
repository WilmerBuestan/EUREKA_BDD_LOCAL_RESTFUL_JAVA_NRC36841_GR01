package ec.edu.monster.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Hash de claves. La tabla usuario de EurekaBank guarda la clave como SHA-1
 * en hexadecimal (40 caracteres), nunca en texto plano.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class UtilHash {

    private UtilHash() {
    }

    public static String sha1(String texto) {
        return digest("SHA-1", texto);
    }

    private static String digest(String algoritmo, String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            return HexFormat.of().formatHex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algoritmo + " no disponible", e);
        }
    }

    /** Compara dos hashes en tiempo constante (evita ataques de tiempo). */
    public static boolean iguales(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.trim().toLowerCase().getBytes(StandardCharsets.UTF_8),
                b.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
