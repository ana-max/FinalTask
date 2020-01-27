package serializators;


import iseriallizator.ISerializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class DoubleSerializator implements ISerializator {
    private String separator;

    public DoubleSerializator(String sep){
        separator = sep;
    }

    @Override
    public byte[] trySerializeObject(Field o, Object value) {
        if (o.getGenericType().getTypeName().equals("double")){
            byte[] sep = separator.getBytes();
            byte[] name = o.getName().getBytes();
            byte val = Double.valueOf(value.toString()).byteValue();
            byte[] type = "double".getBytes();
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
        if (type.equals("double"))
            return Double.valueOf(value.toString());
        return null;
    }
}
