package com.joymeng.common.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileAndByte {
	
	/**
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFromFile(File file) {
		byte[] b = new byte[1024];
		int alllen = 0;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] bytes = new byte[(int) file.length()];
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			int len = -1;
			while ((len = bis.read(b)) != -1) {
				for (int i = 0; i < len; i++) {
					bytes[alllen++] = b[i];
				}
			}
			bis.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}

	
    /**
     * 
     * @param data
     * @return
     */
    public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("asfdff/zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    /**
     * 
     * @param data
     * @return
     */
    public static byte[] unZip(byte[] data, String baseDir) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            ZipEntry zipEntry = null; 
            while ((zipEntry = zip.getNextEntry()) != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                OutputStream os = null;
                if (baseDir != null)
                	os = new FileOutputStream(baseDir + zipEntry.getName());
                else {
                	os = new ByteArrayOutputStream();
                }
                while ((num = zip.read(buf)) != -1) {
                    os.write(buf, 0, num);
                    os.flush();
                }
                os.close();
                if (baseDir == null) {
                	b = ((ByteArrayOutputStream) os).toByteArray();
                }
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
    /**
     * 
     */
    public static void writeToFile(byte []b,String path){				
        File file = new File(path);		
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }


    
//    public static void main(String[] args) throws Exception {
//    	ArrayList<File> files = new ArrayList<File>();
//    	files.add(new File("./updatefiles/1-1-gameData.xml"));
//    	List<String> filePaths = new ArrayList<String>();
//    	filePaths.add("gameData.xml");
//    	byte[] bytes = zipFileByJavaZip(files, filePaths);
//    	FileOutputStream fos = new FileOutputStream("D:\\zz.zip");
//    	fos.write(bytes);
//    	fos.flush();
//    	fos.close();
//    	unZip(bytes, "d:/");
//	}
    
    
    public static void unzipZipFile(String dir, File zipFile) {
    	byte[] bytes = readFromFile(zipFile);
    	unZip(bytes, dir);
    }
    
    public static byte[] zipFileByJavaZip(List<File> files, List<String> filePaths) {
    	List<byte[]> fileBytes = new ArrayList<byte[]>(files.size());
    	for (int i = 0; i < files.size(); i++) {
    		byte[] bytes;
			try {
				FileInputStream fis = new FileInputStream(files.get(i));
				bytes = new byte[(int) files.get(i).length()];
				fis.read(bytes);
				fis.close();
	    		fileBytes.add(bytes);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return zipByJavaZip(fileBytes, filePaths);
    }
    
    public static byte[] zipByJavaZip(List<byte[]> unzippedFileBytes, List<String> filePaths) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            for (int i = 0; i < filePaths.size(); i++) {
	            ZipEntry entry = new ZipEntry(filePaths.get(i));
	            byte[] fileBytes = unzippedFileBytes.get(i);
	            zip.putNextEntry(entry);
	            zip.write(fileBytes);
	            zip.closeEntry();
            }
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    
    }
    
    
    
    

}
