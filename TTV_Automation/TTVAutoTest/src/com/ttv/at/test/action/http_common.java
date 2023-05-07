package com.ttv.at.test.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.ttv.at.log.log.File;
import com.ttv.at.test.parameter;
import com.ttv.at.test.testsetting;

public class http_common {
	
	static String folder_data = "data/";
	static String folder_json_update = "/data_json_update/";
	static String folder_input = "/input/";
	static SimpleDateFormat simple_date_time_format = new SimpleDateFormat("yyyyMMdd");

	static public response send_request(ArrayList<parameter> inputs, String method) throws Exception { 
		
		StringBuilder builder = new StringBuilder();
		
		String sce = inputs.get(0).get_value();
		String url = inputs.get(1).get_value();
		String header = inputs.get(2).get_value();
		String body = inputs.get(3).get_value();
		String key = inputs.get(4).get_value();
		
		String [] str = sce.split("_");
		String apiName = str[0];
		String sceName = str[1];
		String seqName = str[2];
		
		long startTime = System.currentTimeMillis();
		builder.append("-- " + sce + " START --\n");
		URL host = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) host.openConnection();
		conn.setDoOutput(true); 
		conn.setRequestMethod(method);
		
		
		String path_header = folder_data + apiName + folder_input +  header;
		JSONObject json_header = util.parse_json_object(path_header);
		builder.append("-- request.header: " + json_header.toString() + " --\n");
		//String str_headerString = 
		conn.setRequestProperty("X-apiName", apiName);
		conn.setRequestProperty("X-sceName", apiName + "_" + sceName);
		conn.setRequestProperty("X-seqName", seqName);
		conn.setRequestProperty("Authorization", json_header.getString("Authorization"));
		conn.setRequestProperty("X-TransactionId", json_header.getString("X-TransactionId"));
		conn.setRequestProperty("Content-Type", json_header.getString("Content-Type"));
		conn.setRequestProperty("Accept", json_header.getString("Accept"));
		 
		String path_body = folder_data + apiName + folder_json_update +  body;
		String json_body = util.parse_json_string(path_body);
		builder.append("-- request.body: " + json_body.toString() + " --\n");
		
		OutputStream os = conn.getOutputStream(); 
		os.write(json_body.getBytes());
		os.flush();
		
		int status = conn.getResponseCode();
		response res = new response();
		long endTime = System.currentTimeMillis();
		
		double measure_time = (double)(endTime - startTime) / 1000;
		builder.append(apiName + "_" + sceName + "measurement " + measure_time + "(sec) \n");
		
		if(status == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			 
			String output; 	
			StringBuffer content = new StringBuffer();
			while ((output =br.readLine()) != null) { 
				 content.append(output);
			}
			 
			System.out.println(content.toString());
			builder.append("-- response.body: " + content.toString() + " --\n");
			JSONObject obj = new JSONObject(content.toString());
			String fileName = sce + ".json";
			String path = folder_data + apiName + folder_json_update + fileName;
			res.setStatus(200);
			res.setData(obj);
			util.change_value(obj, key);
			util.write_json_file(path, obj);
		} else {
			res.setStatus(400);
		}
		builder.append("-- " + sce + " END --\n");
		
		String filename = apiName + "_" + sceName + "_" + simple_date_time_format.format(new Date());
		String api_folder = testsetting.get_default_log_api_folder();
		String log_file = api_folder + System.getProperty("file.separator") + filename + ".log";
		
		if(!File.create_file_write_line_if_file_not_exists(builder.toString(), log_file)) {
			File.write_line(builder.toString(), log_file);
		}
		
		conn.disconnect();
		return res;
	}
}
