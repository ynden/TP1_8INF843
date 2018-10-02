import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private BufferedReader br;
	private PrintWriter pw;

	public void execute(int protocole, String message) {
		// Process the message

		connect(message, protocole);
	}

	private void connect(String message, int protocole) {
		try {
			// Create the socket, and get the output and input stream
			socket = new Socket("localhost", 6532);
			os = socket.getOutputStream();
			is = socket.getInputStream();

			pw = new PrintWriter(os);
			br = new BufferedReader(new InputStreamReader(is));

			// We send the protocole code & message
			// 0 => Source
			// 1 => Byte
			// 2 => Object : we have to instanciate an ObjectOutputStream

			pw.println(protocole);
			pw.println(message);
			pw.flush();

			// Read the response code
			// and close the socket if an error occurred
			int reponseCode = Integer.parseInt(br.readLine());
			if (reponseCode != 200) {
				br.close();
				pw.close();
				socket.close();

				System.out.println("Error");
				System.exit(0); // we exit
			}

			// We continue if everything was sent correctly
			if (protocole == 0) {
				// We will send a java file
				String className = message.split("&")[0];
				String basePath = System.getProperty("user.dir");

				File fileToSend = new File(basePath + "/" + className + ".java");
				System.out.println(fileToSend.getAbsolutePath());
				FileInputStream fis = new FileInputStream(fileToSend);

				byte bytesArray[] = new byte[(int) fileToSend.length()];
				// we get the bytes from the file
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(bytesArray); // we put the bytes in the array

				bis.close();
				fis.close();

				System.out.println("We send the size : " + bytesArray.length);
				pw.println(bytesArray.length);
				pw.println(message);
				pw.flush();

				os.write(bytesArray);
				os.flush();
				
				

			} else if (protocole == 1) {
				// We will send a class file
				String className = message.split("&")[0];
				String basePath = System.getProperty("user.dir");

				File fileToSend = new File(basePath + "/bin/" + className + ".class");
				FileInputStream fis = new FileInputStream(fileToSend);
				BufferedInputStream bis = new BufferedInputStream(fis);

				byte arrayBytes[] = new byte[(int) fileToSend.length()];
				bis.read(arrayBytes);

				bis.close();
				fis.close();
				
				pw.println(arrayBytes.length); // send the length
				pw.println(message); // send the message
				pw.flush();

				os.write(arrayBytes); // send the bytes
				os.flush();

			} else if (protocole == 2) {
				// We will send an object (serialized)
				String className = message.split("&")[0];
				ObjectOutputStream oos = new ObjectOutputStream(os);

				Class<?> objectClass = Class.forName(className);
				Object instanciateObject = objectClass.newInstance();
				oos.writeObject(instanciateObject); // we send the object
			}

			// We read the result from the server
			int result = Integer.parseInt(br.readLine());
			System.out.println("The result is : " + result);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.execute(1, "Calc&add&3,5");
	}
}
