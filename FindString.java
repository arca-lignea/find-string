package org.findstr;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Walk a directory tree containing Jar files and find all Jars
 * containing any class containing the given sub string.
 * @author
 *
 */

public class FindString {

       public static void main(String[] args) {
              String pathStr = args[0];
              String searchStr = args[1];
             
              if (pathStr != null) {
                     Path path = Paths.get(pathStr);
                     try {
                           Files.walkFileTree(path, new FindStringVisitor(searchStr));
                     } catch (IOException e) {
                           e.printStackTrace();
                     }

              }
       }
      

       static class FindStringVisitor extends SimpleFileVisitor<Path> {

              String searchStr;
      
              FindStringVisitor(String searchStr) {
                     this.searchStr = searchStr;
              }
             
              @Override
              public FileVisitResult visitFile(Path filePath, BasicFileAttributes arg1) throws IOException {
                    
                     // filter paths for jar files
                     String pathStr = filePath.toString();
                     if (pathStr.endsWith("jar")) {
                          
                           try (JarFile jarFile = new JarFile(filePath.toFile())) {
                                 
                                  boolean foundInJar = false;
                                 
                                  // iterate through entries in jar
                                  Enumeration<JarEntry> entries = jarFile.entries();
                                  while (entries.hasMoreElements()) {
                                         JarEntry entry = entries.nextElement();
                                         String entryName = entry.getName();
                                        
                                         // filter entries for class files
                                         if (entryName.endsWith(".class")) {
                                                try (InputStream inputStream = jarFile.getInputStream(entry);
                                                       InputStreamReader isReader = new InputStreamReader(inputStream,"UTF-8"); // decode class file using UTF-8
                                                       BufferedReader buffReader = new BufferedReader(isReader)) {
                                                      
                                                       String line = null;
                                                       while ((line = buffReader.readLine()) != null) {
                                                              if (line.contains(searchStr)) {
                                                                     System.out.println(entry.getName());
                                                                     if (!foundInJar) {
                                                                           foundInJar = true;
                                                                     }
                                                                     break;
                                                              }
                                                       }
                                                }
                                         }
                                  }
                                  if (foundInJar) {
                                         System.out.println(pathStr);
                                         System.out.println();
                                  }
                           }
                     }
                     return FileVisitResult.CONTINUE;
              }
             
       }
      
}
