package maven.delelastupdate;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.filefilter.FileFilterUtils;




public class DelLastUpdated { 
 
    private static final String KEY_MAVEN_REPO = "maven.repo"; 
    private static final String FILE_SUFFIX = "lastUpdated"; 
 
    /** 
     * @param args 
     */ 
    public static void main(String[] args) { 
        File mavenRep = new File("e:/.m2/"); 
        if (!mavenRep.exists()) { 
            //logger.warn("Maven repos is not exist."); 
            return; 
        } 
        File[] files = mavenRep.listFiles((FilenameFilter) FileFilterUtils 
                .directoryFileFilter()); 
        delFileRecr(files,null); 
        System.out.println("Clean lastUpdated files finished."); 
    } 
 
    private static void delFileRecr(File[] dirs, File[] files) { 
        if (dirs != null && dirs.length > 0) { 
            for(File dir: dirs){ 
                File[] childDir = dir.listFiles((FilenameFilter) FileFilterUtils 
                .directoryFileFilter()); 
                File[] childFiles = dir.listFiles((FilenameFilter) FileFilterUtils 
                .suffixFileFilter(FILE_SUFFIX)); 
                delFileRecr(childDir,childFiles); 
            } 
        } 
        if(files!=null&&files.length>0){ 
            for(File file: files){ 
                if(file.delete()){ 
                	System.out.println("File: ["+file.getName()+"] has been deleted."); 
                } 
            } 
        } 
    } 
 
} 
