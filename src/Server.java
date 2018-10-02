import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Server {
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private InputStream is;
	private OutputStream os;
	private BufferedReader br;
	private PrintWriter pw;

	public void execute() {
		try {
			serverSocket = new ServerSocket(6532);
			System.err.println("Waiting for connections...");
			clientSocket = serverSocket.accept(); // get the client socket
			System.err.println("Connexion Ok");

			// Initialize the input & output streams
			os = clientSocket.getOutputStream();
			is = clientSocket.getInputStream();

			// bw = new BufferedWriter(new OutputStreamWriter(os));
			pw = new PrintWriter(os);
			br = new BufferedReader(new InputStreamReader(is));

			// Read the code and the message sent
			int protocole = Integer.parseInt(br.readLine());
			System.err.println("Protocole ok");
			String message = br.readLine();
			System.err.println("Message ok " + message.length());

			// We sent back a message code to say
			// if everything was received correctly
			if (protocole == 0) { // File
				pw.println(200);
				pw.flush();

				processProtocolSource();
			} else if (protocole == 1) { // Byte
				pw.println(200);
				pw.flush();

				processProtocolBytes();
			} else if (protocole == 2) { // Object
				pw.println(200);
				pw.flush();

				processProtocolObject(message, is);
			} else {
				pw.println(500);
				pw.flush();
			}

			System.err.println("Read everything");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processProtocolObject(String message, InputStream is) {
		// We read the object received
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			Object receivedObject = ois.readObject();
			System.out.println(receivedObject.getClass().getName());

			String methodName = message.split("&")[1];
			String params = message.split("&")[2];
			int result = callMethod(receivedObject, methodName, params);

			// We send the result to the client
			pw.println(result);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private void processProtocolBytes() {
		try {
			// Get the size of the array
			int arraySize = Integer.parseInt(br.readLine());
			byte arrayBytes[] = new byte[arraySize];

			// Get the message
			String messageReceived = br.readLine();

			// Get the byte array
			is.read(arrayBytes);

			String className = messageReceived.split("&")[0];
			String methodName = messageReceived.split("&")[1];
			String params = messageReceived.split("&")[2];

			File root = new File(System.getProperty("user.dir") + "/" + "bin/");
			URL urls[] = new URL[] { root.toURI().toURL() };

		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		pw.print(200);
		pw.flush();

	}

	private void processProtocolSource() {
		try {
			// We read the size of the byte array
			int byteSize = Integer.parseInt(br.readLine());
			System.err.println("Byte size received => " + byteSize);
			byte byteArray[] = new byte[byteSize];

			// We read the message
			String messageReceived = br.readLine();
			String className = messageReceived.split("&")[0];
			String methodName = messageReceived.split("&")[1];
			String params = messageReceived.split("&")[2];

			// We read the bytes
			is.read(byteArray);

			System.out.println("Here");
			// We create the file
			String basePath = System.getProperty("user.dir");

			System.out.println("We create the file");
			File fileToCreate = new File(basePath + "/src/business/" + className + ".java");
			FileOutputStream fos = new FileOutputStream(fileToCreate);
			fos.write(byteArray); // we write the bytes into the file
			fos.close();

			// The file was created
			// We will compile it and execute specifics methods
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			int reponse = javaCompiler.run(null, null, null, fileToCreate.getPath());
			System.out.println("Reponse " + reponse);

			File root = new File(System.getProperty("user.dir") + "/" + "src/");
			URL urls[] = new URL[] { root.toURI().toURL() };

			// Load the class
			ClassLoader loader = new URLClassLoader(urls);
			Class<?> cls = loader.loadClass("business.Calc");
			System.out.println(cls.getName());

			// Instanciate the class
			Object objInstanciate = cls.newInstance();
			int result = callMethod(objInstanciate, methodName, params);

			pw.println(result);
			pw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int callMethod(Object objectInstanciate, String methodName, String params) {
		Object paramsValues[] = params.split(",");
		Class<?> typesValues[] = new Class[paramsValues.length];

		typesValues[0] = paramsValues[0].getClass();
		typesValues[1] = paramsValues[1].getClass();

		Method methodCall;
		int resultValue = 0;
		try {
			methodCall = objectInstanciate.getClass().getMethod(methodName, typesValues);
			resultValue = (int) methodCall.invoke(objectInstanciate, paramsValues);
			System.out.println("Result : " + resultValue);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return resultValue;
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.execute();
	}
}
