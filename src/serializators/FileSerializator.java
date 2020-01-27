package serializators;

import iseriallizator.ISerializator;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSerializator implements ISerializator {
    private String separator;
    private int counter;

    public FileSerializator(String sep){
        separator = sep;
    }

    @Override
    public byte[] trySerializeObject(Field o, Object value) {
        if (!o.getGenericType().getTypeName().equals("java.io.File")) return null;
        File file = (File)value;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try{
            byte[] sep = separator.getBytes();
            byte[] name = o.getName().getBytes();
            byte[] val = Files.readAllBytes(Paths.get(file.getPath()));
            byte[] type = "java.io.File".getBytes();
            try{
                outputStream.write(sep);
                outputStream.write(name);
                outputStream.write(sep);
                outputStream.write(type);
                outputStream.write(sep);
                outputStream.write(val);
            }
            catch (IOException io) { io.printStackTrace(); }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @Override
    public Object tryDeserializeObject(String type, Object value) {
        if (type.equals("java.io.File")){
            File file = new File("C:\\Users\\Public\\IdeaProjects\\FinalTask\\src\\serverFiles\\" + counter + ".txt");
            counter++;

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(value.toString().getBytes());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            return file;
        }
        return null;
    }
}
