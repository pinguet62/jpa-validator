package fr.pinguet62.jpavalidator.comp;

public class Counter {

    public static int N = 0;

    public static void logValidator(Validator validator) {
        System.out.println(repeat("    ", N) + validator.getClass().getSimpleName());
    }

    private static String repeat(String pattern, int nb) {
        String result = "";
        for (int i = 0; i < nb; i++)
            result += pattern;
        return result;
    }

}