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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;



public class YasumMain {
	
    public static void main(final String[] args) {
    	if (true){	
            ServerSocket serverSocket = null;
            boolean listening = true;

            // Starts TCP ServerSocket + ExceptioHandling
            try {
                serverSocket = new ServerSocket(6963);
            } catch (IOException e) {
                System.err.println("Could not listen on port: 6963.");
                System.exit(-1);
            }

            while (listening) {
                try { // on new connection start new Thread
                
                    new YasumServer(serverSocket.accept()).start();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
    	}
    	System.out.println();
	}

}
