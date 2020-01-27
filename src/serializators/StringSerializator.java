package serializators;


import iseriallizator.ISerializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class StringSerializator implements ISerializator {
    private String separator;

    public StringSerializator(String sep){
        separator = sep;
    }

    @Override
    public byte[] trySerializeObject(Field o, Object value) {
        if (value == null) value = "null";
        if (o.getGenericType().getTypeName().equals("java.lang.String")){
            byte[] sep = separator.getBytes();
            byte[] name = o.getName().getBytes();
            byte[] val = value.toString().getBytes();
            byte[] type = "java.lang.String".getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            try{
                outputStream.write(sep);
                outputStream.write(name);
                outputStream.write(sep);
                outputStream.write(type);
                outputStream.write(sep);
                outputStream.write(val);
            }
            catch (IOException io) { io.printStackTrace(); }
            return outputStream.toByteArray();
        }
        return null;
    }

    @Override
    public Object tryDeserializeObject(String type, Object value) {
        if (type.equals("java.lang.String"))
            if (value.equals("null")) return null;
            else return value.toString();
        return null;
    }
}
