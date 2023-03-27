package com.gtech.pegasus.server.bundle.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private Map<File, DependencyDestination> files;
   
    public ZipUtils() {
    	files = new HashMap<File, DependencyDestination>();
    }

    public File zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for (File file: this.files.keySet()) {
            	ZipEntry ze = null;
            	if( file.isFile() ) {
	            	DependencyDestination dest = this.files.get(file);
	                System.out.println("File Added : " + file.getName()+ " "+dest.toString());
	                
	                ze = new ZipEntry(dest + File.separator + file.getName());
            	} else if ( file.isDirectory() ) {
            		System.out.println("Folder Added : " + file.getName());
//            		ze = new ZipEntry(file.getName());
            	}
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(file.getAbsolutePath());
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                	if( in != null )
                		in.close();
                }
            }
          
            zos.closeEntry();
            System.out.println("Folder successfully compressed");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return new File( zipFile );
    }

    public void addFile(File lib, DependencyDestination dest) {
        // add file only
       
           files.put(lib, dest);


    }

}