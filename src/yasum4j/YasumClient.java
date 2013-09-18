/*
* Copyright 2013 daniel <daniel@tardis>
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YasumClient {

	public static void main(final String[] args) {
		System.out.println(performVersionCheck("0.1.1", "MyTestApp", "127.0.0.1", 6963));
	}

	/*
	 * returns: 
	 * -1- update is manditory! 
	 * 0 - update is possible
	 * 1 - update is not possible
	 * 2 - ERROR
	 */
	public static int performVersionCheck(String ownVersion, String AppName,
			String updateServerHostName, int updateServerPort) {
		int result;
		JSONObject check = getVersionFromServer(AppName, updateServerHostName,
				updateServerPort);
		if (!((String) check.get("MinVersion")).contains("ERROR")) {

			String minVersion = (String) check.get("MinVersion");
			String currVersion = (String) check.get("CurrVersion");

			// WTF?! split(".") always returned an empty String[], but
			// contains(".") returned true.

			String ownV[] = ownVersion.split("\\.");
			String minV[] = minVersion.split("\\.");
			String currV[] = currVersion.split("\\.");

			// Arrays.asList(minV).size());
			int i = 0;
			while (i < ownV.length && i < minV.length
					&& ownV[i].equals(minV[i])) {
				i++;
			}

			if (i < ownV.length && i < minV.length) {
				int diff = Integer.valueOf(ownV[i]).compareTo(
						Integer.valueOf(minV[i]));
				result = diff < 0 ? -1 : diff == 0 ? 0 : 1;
			} else {
				result = ownV.length < minV.length ? -1
						: ownV.length == minV.length ? 0 : 1;
			}
			// own version ahead of minversion, check for currversion
			int result2;
				i = 0;
				while (i < ownV.length && i < currV.length
						&& ownV[i].equals(currV[i])) {
					i++;
				}

				if (i < ownV.length && i < currV.length) {
					int diff = Integer.valueOf(ownV[i]).compareTo(
							Integer.valueOf(currV[i]));
					result2 = diff < 0 ? -1 : diff == 0 ? 0 : 1;
				} else {
					result2 = ownV.length < currV.length ? -1
							: ownV.length == currV.length ? 0 : 1;
				}

			System.out.println(result+ " " + result2);
			if (result==-1) return -1;
			if ((result==0 && result2==0)|| (result==1 && result2==0)) return 1;
			if ((result==0 && result2==-1)|| (result==1 && result2==-1) ) return 0;
			
			
		}
		
		
		return 2;
	
	}

	private static JSONObject getVersionFromServer(String AppName,
			String updateServerHostName, int updateServerPort) {
		JSONObject result = new JSONObject();

		try {

			Socket socket = new Socket(updateServerHostName, updateServerPort);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String inputLine, outputLine;

			if (socket.isConnected()) {
				out.println("{\"ApplicationName\":\"" + AppName
						+ "\",\"Request\":\"All\"}");
				while ((inputLine = in.readLine()) != null) {

					System.out.println("Debug:Input= " + inputLine);
					result = (JSONObject) new JSONParser().parse(inputLine);
					break;
				}

				out.close();
				in.close();
				socket.close();
				return result;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return null;

	}
}
