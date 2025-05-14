package test;

public class Base62Encoder implements BaseEncoder {

    private final int base;
    private final String characters;

    public Base62Encoder() {
        this.base = 62;
        this.characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    }

    @Override
    public String encode(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            stringBuilder.append(characters.charAt((int) (number % base)));
            number /= base;
        } while (number > 0);
        return stringBuilder.toString();
    }
}