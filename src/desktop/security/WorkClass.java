/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktop.security;

import com.google.common.hash.Hashing;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

/**
 *
 * @author onyhouqe
 */
public class WorkClass {
    
    public byte[] encryptPdfFile(Key key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
//            FXMLDcoumentController.view_output.setText(">>Exception : "+e);
        }
        return encrypted;
    }
    
    public byte[] decryptPdfFile(Key key, byte[] textCryp) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(textCryp);
        } catch (Exception e) {
//            FXMLDcoumentController.view_output.setText(">>Exception : "+e);
        }

        return decrypted;
    }
    
    public byte[] getFile(File f) {
        InputStream is;
        try {
            is = new FileInputStream(f);
            byte[] content;
            content = new byte[is.available()];
            is.read(content);
            return content;
        } catch (FileNotFoundException e) {
//            view_output.setText(">>FileNotFoundException : "+e);
        } catch (IOException e) {
//            FXMLDcoumentController.view_output.setText(">>IOException : "+e);
        }
        return null;
    }
    
    public void saveFile(byte[] bytes, String path) {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(path);
            fos.write(bytes);
        fos.close();
        } catch (FileNotFoundException e) {
//            FXMLDcoumentController.view_output.setText(">>FileNotFoundException : "+e);
        } catch (IOException e) {
//            FXMLDcoumentController.view_output.setText(">>IOException : "+e);
        }
    }
    
    public Key keyMaker(String str){
        String str_hash = Hashing.sha256()
                .hashString(str, StandardCharsets.UTF_8)
                .toString();
        str_hash = str_hash.substring(0,16);
        Key key = new SecretKeySpec(str_hash.getBytes(),0,str_hash.getBytes().length, "AES");
        return key;
    }
    
    public File selectDirectory(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(null);
        if(option == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            return file;
        }else{
            return null;
        }
    }
    public ArrayList<File> getFilesList(final File folder){
        ArrayList<File> file_list = new ArrayList<File>();
        listFilesForFolder(folder, file_list);
        return file_list;
    }
    public static void listFilesForFolder(final File folder, ArrayList<File> file_list) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, file_list);
            } else {
                file_list.add(fileEntry);
            }
        }
    }
    
    
}
