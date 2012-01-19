/******************************************************************************
 *
 * Copyright (c) 1999-2011 Cryptzone Group AB. All Rights Reserved.
 * 
 * This file contains Original Code and/or Modifications of Original Code as
 * defined in and that are subject to the MindTerm Public Source License,
 * Version 2.0, (the 'License'). You may not use this file except in compliance
 * with the License.
 * 
 * You should have received a copy of the MindTerm Public Source License
 * along with this software; see the file LICENSE.  If not, write to
 * Cryptzone Group AB, Drakegatan 7, SE-41250 Goteborg, SWEDEN
 *
 *****************************************************************************/


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.security.SecureRandom;

import com.mindbright.nio.NetworkConnection;
import com.mindbright.util.SecureRandomAndPad;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.ExpectOutputStream;
import com.mindbright.util.Util;

import com.mindbright.ssh2.*;

/**
 * This is a simple demo of running a list of command-lines given as text
 * file. The commands are run sequentially and the stdout output from each
 * command is printed to the local stdout (stderr is also redirected to the
 * local stderr).
 * <p>
 * Usage:
 * <code> java -cp examples.jar examples.RemoteShellScript
 * <em>host</em>[:<em>port</em>] <em>username</em> <em>password</em>
 * <em>script_file</em>
 *
 * @see com.mindbright.util.ExpectOutputStream
 */
public class RemoteShell extends Thread implements ExpectOutputStream.Expector {
    private OutputStream       stdin;
    private ExpectOutputStream stdout;
    private String             result;
    private String [] array = new String [4];

    private static String EOL = "\n";
    
    public RemoteShell(String host, String user, String key, String file) {
    	
    	array[0] = host;
    	array[1] = user;
    	array[2] = key;
    	array[3] = file;

    }
    /**
     * Constructor which takes the remote console where the script
     * should be executed as argument.
     *
     * @param console connection to server
     */
    public RemoteShell(SSH2ConsoleRemote console) {
        this.stdin   = console.getStdIn();
        this.stdout  = new ExpectOutputStream(this, "___END_CMD_MARKER___");
        console.changeStdOut(stdout);
    }

    /**
     * Launch a single command on the server
     *
     * @param cmd command line to execute
     *
     * @return the output from the given command
     */
    public String run(String cmd) {
        try {
            // Send the command to the server
            stdin.write((cmd + EOL).getBytes());

            /*
             * Send end-marker (which the ExpectOutputStream is
             * waiting for). The trick with the quotes is so that the
             * ExpectOutputStream does not trigger when the echo
             * command is echoed but just when it is executed.
             */
            stdin.write(("echo \"___END\"_CMD_MARKER___" + EOL).getBytes());
            synchronized(this) {
                // Wait until the end marker has been seen
                wait();
            }
            // Result was filled in by the reached function
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Log off from server
     */
    public void exit() {
        try {
            stdin.write(("exit" + EOL).getBytes());
        } catch (Exception e) {}
    }

    /**
     * Run the application
     */
    public void run() {
        
    	if(array.length < 4) {
            System.out.println("usage: RemoteShellScript <server[:port]> <username> <keyFile> <script-file>");
            System.exit(1);
        }

        try {
//        	sleep (((long)Math.random())*10000);
            String host    = array[0];
            String user    = array[1];
            String keyFile = array[2];
            String file    = array[3];

            int    port;

            port = Util.getPort(host, 22);
            host = Util.getHost(host);

            /*
             * Connect to the server and authenticate using private-key
             * authentication (if other authentication method needed
             * check other constructors for SSH2SimpleClient).
             */
                      
            SSH2Preferences prefs = new SSH2Preferences();

            SSH2Transport transport = new SSH2Transport(NetworkConnection.open(host, port),
            							prefs, createSecureRandom());
            SSH2SimpleClient client = new SSH2SimpleClient(transport, user, keyFile,
                    null);

            /*
             * Create the remote console to use for shell execution. Here we
             * redirect stderr of all sessions started with this console to our
             * own stderr (NOTE: stdout is NOT redirected here but is instead
             * changed in the RemoteShellScript constructor).
             */
            SSH2ConsoleRemote console =
                new SSH2ConsoleRemote(client.getConnection(), null,System.err);

            /*
             * Start a shell on server (note: we don't want a PTY here)
             */
            if(!console.shell(true, "dumb", 20, 80)) {
                throw new Exception("Couldn't execute shell on server!");
            }

            /*
             * Prepare for our home-brew shell interaction.
             */
            RemoteShell shell = new RemoteShell(console);
            BufferedReader script = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));

            /*
             * Run each line from the given file in the remote shell and print
             * the output to stdout which is returned by the run() method here.
             */
            String line;
            while((line = script.readLine()) != null) {
                System.out.print(shell.run(line));
            }

            /*
             * Exit the shell (if not already done in the script-file).
             */
//            shell.exit();

            /*
             * Retrieve the exit status of the shell (from the remote end).
             */
//            int exitStatus = console.waitForExitStatus();

            /*
             * Disconnect the transport layer gracefully
             */
            client.getTransport().normalDisconnect("User disconnects");

            /*
             * Exit with same status as remote shell did
             */
//            System.exit(exitStatus);

        } catch (Exception e) {
            System.out.println("An error occured: " + e);
            System.exit(1);
        }
    }

    /**
     * Create a random number generator. This implementation uses the
     * system random device if available to generate good random
     * numbers. Otherwise it falls back to some low-entropy garbage.
     */
    private static SecureRandomAndPad createSecureRandom() {
        byte[] seed;
        File devRandom = new File("/dev/urandom");
        if (devRandom.exists()) {
            RandomSeed rs = new RandomSeed("/dev/urandom", "/dev/urandom");
            seed = rs.getBytesBlocking(20);
        } else {
            seed = RandomSeed.getSystemStateHash();
        }
        return new SecureRandomAndPad(new SecureRandom(seed));
    }

    /*
     * ExpectOutputStream.Expector interface implementation
     */
    public synchronized void reached(ExpectOutputStream out,
                                     byte[] buf, int len) {
        result = new String(buf, 0, len);
        synchronized(this) {
            notify();
        }
    }

    public void closed(ExpectOutputStream out, byte[] buf, int len) {
        result = null;
        synchronized(this) {
            notify();
        }
    }

}
