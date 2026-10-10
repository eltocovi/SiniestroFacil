package cl.duoc.siniestrofacil.ms_poliza.util;

public final class RutUtil {

    private RutUtil() {
    }

    public static String calcularDigitoVerificador(int rut) {
        int suma = 0;
        int multiplicador = 2;
        for (int numero = rut; numero > 0; numero /= 10) {
            suma += (numero % 10) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        return switch (resultado) {
            case 11 -> "0";
            case 10 -> "K";
            default -> String.valueOf(resultado);
        };
    }

    public static boolean esValido(int rut, String digitoVerificador) {
        return calcularDigitoVerificador(rut).equalsIgnoreCase(digitoVerificador);
    }

    public static String formatear(int rut, String digitoVerificador) {
        return rut + "-" + digitoVerificador;
    }
}
