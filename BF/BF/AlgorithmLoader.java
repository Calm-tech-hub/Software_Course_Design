import java.io.*;
import java.net.*;

/**
 * 动态算法加载器
 * 用于从指定目录加载不同组的算法类
 */
public class AlgorithmLoader extends ClassLoader {
    private String classPath;
    
    public AlgorithmLoader(String classPath) {
        this.classPath = classPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            String fileName = classPath + File.separator + name + ".class";
            FileInputStream fis = new FileInputStream(fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            
            byte[] data = bos.toByteArray();
            fis.close();
            bos.close();
            
            return defineClass(name, data, 0, data.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("无法加载类: " + name, e);
        }
    }
    
    /**
     * 从指定目录编译并加载Java源文件
     */
    public static boolean compileJavaFile(String sourceDir, String fileName) {
        try {
            String javaFile = sourceDir + File.separator + fileName + ".java";
            Process process = Runtime.getRuntime().exec("javac -encoding UTF-8 " + javaFile);
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            System.err.println("编译失败: " + e.getMessage());
            return false;
        }
    }
}
