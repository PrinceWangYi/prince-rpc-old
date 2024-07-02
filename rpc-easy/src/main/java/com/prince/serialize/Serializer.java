package com.prince.serialize;

import java.io.IOException;

public interface Serializer {

    <T> byte[] serialize(T t) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
