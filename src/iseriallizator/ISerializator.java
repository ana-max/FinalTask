package iseriallizator;

import java.lang.reflect.Field;

public interface ISerializator<T> {

    byte[] trySerializeObject(Field o, Object value);

    T tryDeserializeObject(String type, Object value);
}
