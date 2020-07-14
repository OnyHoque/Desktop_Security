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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;

/**
 *
 * @author onyhouqe
 */
public class FXMLDcoumentController implements Initializable {
    
    @FXML
    private PasswordField input_password;
    @FXML
    private Label label;
    @FXML
    private Label view_output;
    @FXML
    private Button btn_encrypt;
    @FXML
    private Button btn_decrypt;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    @FXML
    private void help(MouseEvent event) {
        String str = " =================== Encrypt files ===================\n";
        str += " 1. You need to separate the files in a folder.\n";
        str += " 2. Enter a password in the password field.\n";
        str += " 3. Press the encrypt button and select the previously created folder with all the files.\n";
        str += "\n\n =================== Decrypt files ===================\n";
        str += " 1. You need to separate the encrypted files in a folder.\n";
        str += " 2. Enter the encryption password in the password field.\n";
        str += " 3. Press the decrypt button and select the previously created folder with all the files.\n";
        view_output.setText(str);
    }

    @FXML
    private void Encrypt(MouseEvent event) {
        String pass = input_password.getText();
        if(pass.length() < 4){
            view_output.setText(">>Password length needs to be more than 4 characters!");
        }else{
            Key key = keyMaker(pass);
            File folder = selectDirectory();
            ArrayList<File> file_list = getFilesList(folder);
            if(file_list.size() < 1){
                view_output.setText(">>No Files found!");
            }else{
                String str = " >>List of files found:\n ";
                for(int i = 0; i < file_list.size(); ++i){
                    File file = file_list.get(i);
                    String file_name = file.getName();
                    str = str+file.getName()+" => ";
                    if(file_name.indexOf(".enc") == -1){
                        byte[] file_byte = getFile(file);
                        byte[] encrypted_file_byte = encryptFile(key, file_byte);
                        String save_path = folder.getAbsolutePath()+"\\"+file.getName()+".enc";
                        saveFile(encrypted_file_byte,save_path);
                        
                        str = str+"Encrypted file saved in: "+save_path+"\n ";
                        view_output.setText(str);
                    }else{
                        str = str+"File already encrypted"+"\n ";
                        view_output.setText(str);
                    }
                }
            }
        }
    }

    @FXML
    private void Decrypt(MouseEvent event) {
        String pass = input_password.getText();
        if(pass.length() < 4){
            view_output.setText(">>Password length needs to be more than 4 characters!");
        }else{
            Key key = keyMaker(pass);
            File folder = selectDirectory();
            ArrayList<File> file_list = getFilesList(folder);
            if(file_list.size() < 1){
                view_output.setText(">>No Files found!");
            }else{
                String str = " >>List of files found:\n ";
                for(int i = 0; i < file_list.size(); ++i){
                    File file = file_list.get(i);
                    String file_name = file.getName();
                    str = str+file.getName()+" => ";
                    if(file_name.indexOf(".enc") > -1){
                        byte[] file_byte = getFile(file);
                        try{
                            byte[] decrypted_file_byte = decryptFile(key, file_byte);
                            String save_path = folder.getAbsolutePath()+"\\"+file.getName().replace(".enc", "");
                            saveFile(decrypted_file_byte,save_path);
                            str = str+"Decrypted, stored in: "+save_path+"\n ";
                            view_output.setText(str);
                        }catch(Exception e){
                            str = str+"Not decrypted. wrong password! "+"\n ";
                            view_output.setText(str);
                        }
                    }else{
                        str = str+"Not decrypted. File does not have .enc extension"+"\n ";
                        view_output.setText(str);
                    }
                }
            }
        }
    }
    
    public byte[] encryptFile(Key key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            view_output.setText(">>Exception : "+e);
        }
        return encrypted;
    }
    
    public byte[] decryptFile(Key key, byte[] textCryp) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(textCryp);
        } catch (Exception e) {
            view_output.setText(">>Exception : "+e);
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
            view_output.setText(">>FileNotFoundException : "+e);
        } catch (IOException e) {
            view_output.setText(">>IOException : "+e);
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
            view_output.setText(">>FileNotFoundException : "+e);
        } catch (IOException e) {
            view_output.setText(">>IOException : "+e);
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
