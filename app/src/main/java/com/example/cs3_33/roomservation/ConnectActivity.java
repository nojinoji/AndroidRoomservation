package com.example.cs3_33.roomservation;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by CS3-33 on 2017-04-15.
 */

public class ConnectActivity {



    String SendByHttp(String id, String check) {
        if(id == null)
            id = "";
        if(check == null)
            check = "";

        String URL = "http://210.123.254.135:8080/Roomservation/Check/checkServer.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {
			/* 체크할 id와 pwd값 서버로 전송 */
            HttpPost post = new HttpPost(URL+"?check="+ check+"&id="+id);

			/* 지연시간 최대 3초 */
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */

            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown();	// ���� ���� ����
            return "";
        }

    }

}
