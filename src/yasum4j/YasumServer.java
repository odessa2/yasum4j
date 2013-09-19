/*
* Copyright 2013 daniel götz <odessa2@web.de>
* 
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301, USA.
* 
* 
* 
* Uses JSONSimple library (Apache License 2.0) Version 1.1.1
 * http://code.google.com/p/json-simple/
 * 
* 
* */


package yasum4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YasumServer extends Thread {
	private Socket socket = null;
	static private ArrayList<JSONObject> versions = new ArrayList<JSONObject>();

	public YasumServer(Socket socket) {
		super("YasumServer");
		System.out.println("Starting Thread for new User");
		this.socket = socket;
	}

	public int readVersions() {
		File file = new File("versions.json");
		if (file.exists()) {
			System.out.println("Reading file");

			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				String line;

				while ((line = br.readLine()) != null) {
					System.out.println("VersionObject: " + line);
					JSONObject tmp = (JSONObject) new JSONParser().parse(line);
					versions.add((JSONObject) tmp);
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return 0;
		}
		return -1;
	}

	public String getMinVersionFromFile(String applicationName) {

		if (versions.size() == 0)
			if (readVersions() != 0)
				return "ERROR_READING_VERSIONFILE";
		for (int i = 0; i < versions.size(); i++) {

			if (versions.get(i).get("ApplicationName").toString()
					.equals(applicationName)) {
				return (String) versions.get(i).get("MinVersion");
			}
		}

		return "ERROR_NO_SUCH_APPLICATION";

	}

	public String getCurrVersionFromFile(String applicationName) {

		if (versions.size() == 0)
			if (readVersions() != 0)
				return "ERROR_READING_VERSIONFILE";
		for (int i = 0; i < versions.size(); i++) {
			if (versions.get(i).get("ApplicationName").toString()
					.equals(applicationName)) {
				return (String) versions.get(i).get("CurrVersion");
			}
		}

		return "ERROR_NO_SUCH_APPLICATION";

	}

	@Override
	public void run() {

		try {
			// Initialize Buffer Reader and Writer for Socket.
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String inputLine, outputLine;
			String minV = getMinVersionFromFile("MyTestApp");
			if (minV != "NULL") {
				System.out.println(minV);
			} else
				;

			while ((inputLine = in.readLine()) != null) {

				System.out.println("Debug:Input= " + inputLine);
				JSONObject req = null;
				try {
					req = (JSONObject) new JSONParser().parse(inputLine);
					System.out.println(req.toJSONString());

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					out.println("ERROR");
				}
				if (req != null && req.get("ApplicationName") != null
						&& req.get("Request").toString().equals("All")) {
					JSONObject reply = new JSONObject();
					reply.put("MinVersion",
							getMinVersionFromFile(req.get("ApplicationName")
									.toString()));
					reply.put("CurrVersion",
							getCurrVersionFromFile(req.get("ApplicationName")
									.toString()));
					outputLine = reply.toJSONString();
					out.println(outputLine);
				} else if (req != null && req.get("ApplicationName") != null
						&& req.get("Request").toString().equals("MinVersion")) {
					JSONObject reply = new JSONObject();
					reply.put("MinVersion",
							getMinVersionFromFile(req.get("ApplicationName")
									.toString()));
					outputLine = reply.toJSONString();
					out.println(outputLine);

				} else if (req != null && req.get("ApplicationName") != null
						&& req.get("Request").toString().equals("CurrVersion")) {

					JSONObject reply = new JSONObject();

					reply.put("CurrVersion",
							getCurrVersionFromFile(req.get("ApplicationName")
									.toString()));
					outputLine = reply.toJSONString();
					out.println(outputLine);
				}

				// out.println(outputLine);

				// out.flush();

				if (inputLine.equals("Bye")) {
					out.println("Connection wird nun beendet");
					break;
				}
				break;
			}

			out.close();
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
