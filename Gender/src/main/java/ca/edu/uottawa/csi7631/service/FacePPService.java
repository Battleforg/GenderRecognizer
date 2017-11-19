package ca.edu.uottawa.csi7631.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.SSLException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service implementation class of Face++ platform
 * 
 * @author Yicong Li
 */
public class FacePPService {

    // setting timeout limit
    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;

    /**
     * public calling interface for this service
     * 
     * @param path
     *            the path of the input image
     * @return A string indicating the gender of the input image
     */
    public String getGenderResult(String path) {
        String genderResult = "Error";

        try {
            String result = detectImage(path);
            System.out.println(result);
            if (result.equals("Error")) {
                return genderResult;
            } else {
                JSONObject data = new JSONObject(result);
                JSONArray faces = data.getJSONArray("faces");
                for (int i = 0; i < faces.length(); i++) {
                    JSONObject object = faces.getJSONObject(i);
                    if (object.keySet().contains("attributes")) {
                        genderResult = object.getJSONObject("attributes").getJSONObject("gender").getString("value");
                    }
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return genderResult;
    }

    /**
     * @param path
     *            the path of the input image
     * @return A JSON string will be returned if the detection is successful.
     *         Otherwise, a error message string will be returned.
     */
    private String detectImage(String path) throws Exception {

        // get input file
        File file = new File(path);
        byte[] buff = getBytesFromFile(file);
        // set Face++ api url
        String url = "https://api-cn.faceplusplus.com/facepp/v3/detect";
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, byte[]> byteMap = new HashMap<String, byte[]>();
        map.put("api_key", "Your api_key");
        map.put("api_secret", "Your api_secret");
        map.put("return_attributes", "gender,age,ethnicity");
        byteMap.put("image_file", buff);
        try {
            // call sub-method to send data to Face++
            byte[] bacd = post(url, map, byteMap);
            String str = new String(bacd);
            return str;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "Error";
    }

    /**
     * This method send data to Face++ api and get result string
     * 
     * @param url
     *            the url of Face++ api we called
     * @param map
     *            http post attrbutes setting
     * @param fileMap
     *            the image to be posted
     * @return result JSON string in the binary form
     */
    protected byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        String boundaryString = getBoundary();
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if (fileMap != null && fileMap.size() > 0) {
            Iterator fileIter = fileMap.entrySet().iterator();
            while (fileIter.hasNext()) {

                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();

                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey() + "\"; filename=\""
                        + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try {
            if (code == 200) {
                ins = conne.getInputStream();
            } else {
                ins = conne.getErrorStream();
            }
        } catch (SSLException e) {
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while ((len = ins.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }

    private String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(
                    random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    // transform a file into a binary array
    private byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

}
