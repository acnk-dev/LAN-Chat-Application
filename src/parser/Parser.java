package parser;

public interface Parser<T> {
    T parse(String text);

    String stringify(T object);
}
