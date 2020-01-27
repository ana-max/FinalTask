package serializator;

import iseriallizator.ISerializator;
import serializators.DoubleSerializator;
import serializators.FileSerializator;
import serializators.IntSerializator;
import serializators.StringSerializator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


public class Serializator<T> {

    private ArrayList<ISerializator> serializators;
    private String separator = "--bound--";

    public Serializator(){
        serializators = new ArrayList<>();
        register(new IntSerializator(separator));
        register(new DoubleSerializator(separator));
        register(new StringSerializator(separator));
        register(new FileSerializator(separator));
    }

    public byte[] Serialize(Object o) throws IllegalAccessException, IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (o == null) return null;
        Class clazz = o.getClass();

        String type = clazz.getTypeName();
        outputStream.write(type.getBytes());

        Field[] fields = clazz.getDeclaredFields();

        for (Field field: fields){
            byte[] serializedField = new byte[0];
            field.setAccessible(true);

            for (ISerializator serializator: serializators) {
                serializedField = serializator.trySerializeObject(field, field.get(o));
                if (serializedField != null) break;
            }

            if (serializedField != null && serializedField.length != 0)
                outputStream.write(serializedField);
            else {
                try {
                    outputStream.write(serializeNonPrimitiveType(field));
                }catch (ClassNotFoundException|InstantiationException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return outputStream.toByteArray();
    }

    public T Deserialize(byte[] raw) throws IOException
    {
        try {
            String[] info = new String(raw).split(separator);
            String className = info[0];
            Class clazz =  Class.forName(className);
            Object obj = clazz.newInstance();
            for (int i=1; i+2 < info.length; i+=3){
                String fieldName = info[i];
                String fieldType = info[i+1];
                String fieldValue = info[i+2];

                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                Object value = null;
                for (ISerializator serializator: serializators) {
                    value = serializator.tryDeserializeObject(fieldType, fieldValue);
                    if (value != null){
                        field.set(obj, value);
                        break;
                    }
                }
                if (value==null && !fieldType.equals("java.lang.String")){
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int startOfInfo = Arrays.asList(info).indexOf("");
                    info[startOfInfo] = separator;
                    int endOfInfo = Arrays.asList(info).indexOf("");
                    if (endOfInfo != -1) info[endOfInfo] = separator;
                    else endOfInfo = info.length;
                    for (int j=startOfInfo+1; j < endOfInfo; j++){
                        outputStream.write(info[j].getBytes());
                        outputStream.write(separator.getBytes());
                    }
                    field.set(obj, Deserialize(outputStream.toByteArray()));
                    i = endOfInfo - 2;
                }
                if (value==null && fieldType.equals("java.lang.String")) field.set(obj, null);
            }
            return (T)obj;
        }
        catch (ClassNotFoundException|NoSuchFieldException|InstantiationException|IllegalAccessException ex){
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] serializeNonPrimitiveType(Field field) throws IOException, ClassNotFoundException,java.lang.InstantiationException, java.lang.IllegalAccessException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Class clazz = Class.forName(field.getGenericType().getTypeName());
        byte[] sep = separator.getBytes();
        byte[] resultOfField = Serialize(clazz.newInstance());
        outputStream.write(sep);
        outputStream.write(field.getName().getBytes());
        outputStream.write(sep);
        outputStream.write(field.getGenericType().getTypeName().getBytes());
        outputStream.write(sep);
        outputStream.write(sep);
        outputStream.write(resultOfField);
        outputStream.write(sep);
        return outputStream.toByteArray();
    }

    private void register(ISerializator ser){ serializators.add(ser); }

}
